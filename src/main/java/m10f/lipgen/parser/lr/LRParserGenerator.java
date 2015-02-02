package m10f.lipgen.parser.lr;

import m10f.lipgen.grammar.Grammar;
import m10f.lipgen.grammar.symbol.*;

import java.util.*;
import java.util.stream.Collectors;

public class LRParserGenerator {
    private List<GrammarRule> rules;
    private Symbol startSymbol;
    private GrammarRule augmentedStartRule;
    private Symbol endSymbol;

    private Map<Symbol, List<GrammarRule>> productionIndex;

    public LRParserGenerator(Grammar grammar) {
        this.rules = new ArrayList<>(grammar.getRules());
        this.startSymbol = grammar.getStartSymbol();

        // TODO: better infrastructure for surrogate symbols?
        Nonterminal augmentedStartSymbol = new Nonterminal("!START");
        endSymbol = new Terminal("!END");

        // augment grammar
        List<Symbol> augmentedStartProduction = new ArrayList<>();
        augmentedStartProduction.add(startSymbol);
        augmentedStartRule = new GrammarRule(augmentedStartSymbol, new Production("!START PRODUCTION", augmentedStartProduction));
        this.rules.add(augmentedStartRule);

        // Build indexing structures
        productionIndex = new HashMap<>();
        for(GrammarRule rule : rules) {
            productionIndex
                    .computeIfAbsent(rule.getNonterminal(), (k) -> new ArrayList<>())
                    .add(rule);
        }
    }

    public GrammarRule getAugmentedStartRule() {
        return augmentedStartRule;
    }

    public Symbol getEndSymbol() {
        return endSymbol;
    }

    private boolean isNonterminal(Symbol symbol) {
        return productionIndex.containsKey(symbol);
    }

    public Set<Symbol> first(LRItem item) {
        if(!item.nextSymbol().isPresent())
            return Collections.singleton(item.getLookahead());

        Set<LRItem> items = new HashSet<>();
        Set<LRItem> frontier = Collections.singleton(item);
        while(true) {
            items.addAll(frontier);
            Set<LRItem> nextFrontier = new HashSet<>();

            for (LRItem ruleItem : frontier) {
                if (ruleItem.nextSymbol().isPresent() && isNonterminal(ruleItem.nextSymbol().get())) {
                    for (GrammarRule rule : productionIndex.get(ruleItem.nextSymbol().get())) {
                        LRItem gri = new LRItem(rule, 0, item.getLookahead());
                        if (!items.contains(gri))
                            nextFrontier.add(gri);
                    }
                }
            }

            if (nextFrontier.size() == 0) {
                Set<Symbol> result = items.stream()
                        .filter(i -> i.nextSymbol().isPresent() && !isNonterminal(i.nextSymbol().get()))
                        .map(i -> i.nextSymbol().get())
                        .collect(Collectors.toSet());

                if (items.stream().anyMatch(i -> !i.nextSymbol().isPresent()))
                    result.add(item.getLookahead());

                return result;
            }

            frontier = nextFrontier;
        }
    }

    // TODO: only process the frontier
    public Set<LRItem> itemSetClosure(Set<LRItem> items) {
        Set<LRItem> closure = new HashSet<>(items);
        while(true) {
            Set<LRItem> next = new HashSet<>(closure);
            for(LRItem item : closure) {
                Optional<Symbol> itemNextSymbol = item.nextSymbol();
                if(!itemNextSymbol.isPresent() || !isNonterminal(itemNextSymbol.get()))
                    continue;

                for(GrammarRule reachableRule : productionIndex.get(itemNextSymbol.get()))
                    for(Symbol s : first(item.nextParseLocation()) )
                        next.add(new LRItem(reachableRule, 0, s));
            }
            if(closure.equals(next))
                return closure;
            closure = next;
        }
    }

    // TODO: memoize this result?
    public Set<LRItem> itemSetGoto(Set<LRItem> items, Symbol x) {
        Set<LRItem> gotoSet = new HashSet<>();
        for(LRItem item : items) {
            if(item.nextSymbol().isPresent() && item.nextSymbol().get().equals(x))
                gotoSet.add(item.nextParseLocation());
        }
        return itemSetClosure(gotoSet);
    }

    // TODO: only process the frontier
    public Set<Set<LRItem>> computeItemSets() {
        Set<LRItem> start = Collections.singleton(new LRItem(augmentedStartRule, 0, endSymbol));
        Set<LRItem> startClosure = itemSetClosure(start);

        Set<Set<LRItem>> sets = new HashSet<>();
        sets.add(startClosure);
        while(true) {
            Set<Set<LRItem>> next = new HashSet<>(sets);

            for(Set<LRItem> itemSet : sets) {
                Set<Symbol> seenSymbols = new HashSet<>();
                for(LRItem item : itemSet) {
                    if(item.nextSymbol().isPresent() && !seenSymbols.contains(item.nextSymbol().get())) {
                        next.add(itemSetGoto(itemSet, item.nextSymbol().get()));
                        seenSymbols.add(item.nextSymbol().get());
                    }
                }
            }

            if(next.equals(sets))
                return sets;
            sets = next;
        }
    }

    public LRParsingTable generateParsingTable() {
        Set<Set<LRItem>> items = computeItemSets();
        Map<Set<LRItem>, Long> stateMap = new HashMap<>();
        LRParsingTable table = new LRParsingTable();
        table.setEndSymbol(endSymbol);

        long idx = 0;
        for(Set<LRItem> itemSet : items) {
            stateMap.put(itemSet, idx++);
        }

        for(Set<LRItem> itemSet : items) {
            long state = stateMap.get(itemSet);
            for(LRItem item : itemSet) {
                LRParsingAction action = null;
                Symbol symbol = null;

                if(item.getGrammarRule().equals(augmentedStartRule) && item.getParseLocation() == 0)
                    table.setInitialState(state);

                if(item.nextSymbol().isPresent() && !isNonterminal(item.nextSymbol().get())) {
                    symbol = item.nextSymbol().get();
                    long nextState = stateMap.get(itemSetGoto(itemSet, symbol));
                    action = LRParsingAction.shiftAction(nextState);
                } else if(!item.nextSymbol().isPresent()) {
                    if(!item.getGrammarRule().equals(augmentedStartRule)) {
                        action = LRParsingAction.reduceAction(item.getGrammarRule());
                        symbol = item.getLookahead();
                    } else {
                        action = LRParsingAction.acceptAction();
                        symbol = endSymbol;
                    }
                }

                if(action != null && symbol != null) {
                    Optional<LRParsingAction> existingAction = table.getAction(state, symbol);
                    if (existingAction.isPresent() && !existingAction.get().equals(action)) {
                        throw new RuntimeException(); // TODO: collect these errors and throw a detailed error at the end
                    } else {
                        table.putAction(stateMap.get(itemSet), symbol, action);
                    }
                }

                if(item.nextSymbol().isPresent() && isNonterminal(item.nextSymbol().get())) {
                    Symbol nonterminal = item.nextSymbol().get();
                    table.putGoto(state, nonterminal, stateMap.get(itemSetGoto(itemSet, nonterminal)));
                }
            }
        }
        return table;
    }
}
