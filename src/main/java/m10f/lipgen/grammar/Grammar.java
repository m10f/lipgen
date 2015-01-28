package m10f.lipgen.grammar;

import m10f.lipgen.lexer.LexicalAnalyzer;
import m10f.lipgen.parser.lr.LRParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO: make this the unambigous grammar description?
public class Grammar {
    private Nonterminal startSymbol;
    private List<Terminal> terminals;
    private List<Nonterminal> nonterminals;

    public Grammar() {
        terminals = new ArrayList<>();
        nonterminals = new ArrayList<>();
    }

    public Terminal terminal(String symbolName, String regex) {
        Terminal t = new Terminal(symbolName, regex);
        terminals.add(t);
        return t;
    }

    public Nonterminal nonterminal(String symbolName) {
        Nonterminal nt = new Nonterminal(symbolName);
        nonterminals.add(nt);
        return nt;
    }

    public Nonterminal getStartSymbol() {
        return startSymbol;
    }

    public void setStartSymbol(Nonterminal startSymbol) {
        this.startSymbol = startSymbol;
    }

    public List<Terminal> getTerminals() {
        return terminals;
    }

    public List<Nonterminal> getNonterminals() {
        return nonterminals;
    }

    // TODO: write regex->nfa conversion
    public LexicalAnalyzer makeLexer() {
        return null;
    }

    public LRParser makeLRParser() {
        return null;
    }

    public static interface GrammarSymbol {
        public String getSymbol();
        public String getTag();
    }

    public static class Production {
        private String tag;
        private List<GrammarSymbol> production;

        public Production(String tag, List<GrammarSymbol> production) {
            this.tag = tag;
            this.production = production;
        }

        public String getTag() {
            return tag;
        }

        public List<GrammarSymbol> getProduction() {
            return production;
        }
    }

    public static class Nonterminal implements GrammarSymbol {
        private final String symbolName;
        private List<Production> productions;

        private Nonterminal(String symbolName) {
            this.symbolName = symbolName;
            this.productions = new ArrayList<>();
        }

        @Override
        public String getSymbol() {
            return null;
        }

        @Override
        public String getTag() {
            return null;
        }

        public Tagged<Nonterminal> tag(String tag) {
            return new Tagged<>(this, tag);
        }

        public Nonterminal withRule(String ruleTag, GrammarSymbol... symbols) {
            productions.add(new Production(ruleTag, Arrays.asList(symbols)));
            return this;
        }
    }

    public static class Tagged<T extends GrammarSymbol> implements GrammarSymbol {
        private final T sub;
        private final String tag;

        public Tagged(T sub, String tag) {
            this.sub = sub;
            this.tag = tag;
        }

        @Override
        public String getSymbol() {
            return sub.getSymbol();
        }

        @Override
        public String getTag() {
            return tag;
        }

        public T getTarget() {
            return sub;
        }
    }

    public static class Terminal implements GrammarSymbol {
        private final String symbolName;
        private final String regex;

        private Terminal(String symbolName, String regex) {
            this.symbolName = symbolName;
            this.regex = regex;
        }

        public Tagged<Terminal> tag(String tag) {
            return new Tagged<>(this, tag);
        }

        @Override
        public String getSymbol() {
            return symbolName;
        }

        @Override
        public String getTag() {
            return null;
        }

        public String getRegex() {
            return regex;
        }
    }
}
