package m10f.lipgen.parser.lr;

import m10f.lipgen.grammar.symbol.GrammarRule;
import m10f.lipgen.grammar.symbol.Symbol;

import java.util.Optional;

public class LRItem {
    private GrammarRule rule;
    private int parseLocation;
    private Symbol lookahead;

    public LRItem(GrammarRule rule, int parseLocation, Symbol lookahead) {
        this.rule = rule;
        this.parseLocation = parseLocation;
        this.lookahead = lookahead;
    }

    public int getParseLocation() {
        return parseLocation;
    }

    public Optional<Symbol> nextSymbol() {
        if(parseLocation < rule.getProduction().getElements().size())
            return Optional.of(rule.getProduction().getElements().get(parseLocation));
        else
            return Optional.empty();
    }

    public GrammarRule getGrammarRule() {
        return rule;
    }

    public Symbol getLookahead() {
        return lookahead;
    }

    public LRItem nextParseLocation() {
        if(parseLocation >= rule.getProduction().getElements().size())
            throw new IllegalArgumentException();
        return new LRItem(rule, parseLocation + 1, lookahead);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LRItem that = (LRItem) o;

        if (parseLocation != that.parseLocation) return false;
        if (!lookahead.equals(that.lookahead)) return false;
        return rule.equals(that.rule);

    }

    @Override
    public int hashCode() {
        int result = rule.hashCode();
        result = 31 * result + parseLocation;
        result = 31 * result + lookahead.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(rule.getNonterminal().getName());
        sb.append(" -> ");
        int i = 0;
        for(Symbol symbol : rule.getProduction().getElements()) {
            if(i++ == parseLocation)
                sb.append(" . ");
            sb.append(" ");
            sb.append(symbol.getName());
            sb.append(" ");
        }
        if(!nextSymbol().isPresent())
            sb.append(" . ");
        sb.append(",");
        sb.append(lookahead.getName());
        return sb.toString();
    }
}
