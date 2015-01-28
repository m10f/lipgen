package m10f.lipgen.parser;

import java.util.Collections;
import java.util.List;

public interface ParseTree {
    public Symbol getSymbol();
    public List<ParseTree> getChildren();

    public static class Terminal implements ParseTree {
        private final Symbol symbol;
        private final String lexeme;

        public Terminal(Symbol symbol, String lexeme) {
            this.symbol = symbol;
            this.lexeme = lexeme;
        }

        @Override
        public Symbol getSymbol() {
            return symbol;
        }

        @Override
        public List<ParseTree> getChildren() {
            return Collections.emptyList();
        }

        public String getLexeme() {
            return lexeme;
        }
    }

    public static class Nonterminal implements ParseTree {
        private final GrammarRule rule;
        private final List<ParseTree> children;

        public Nonterminal(GrammarRule rule, List<ParseTree> children) {
            this.rule = rule;
            this.children = children;
        }

        @Override
        public Symbol getSymbol() {
            return rule.getNonterminalSymbol();
        }

        @Override
        public List<ParseTree> getChildren() {
            return children;
        }

        public GrammarRule getRule() {
            return rule;
        }
    }
}
