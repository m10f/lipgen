package m10f.lipgen.parser;

import m10f.lipgen.grammar.symbol.GrammarRule;
import m10f.lipgen.grammar.symbol.Symbol;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ParseTree {
    public abstract Symbol getSymbol();
    public abstract List<ParseTree> getChildren();
    public boolean isLeaf() {
        return this instanceof Leaf;
    }

    public boolean isNode() {
        return this instanceof Node;
    }

    public Node asNode() {
        if(!isNode())
            throw new RuntimeException("ParseTree element is not a node");
        return (Node)this;
    }

    public Leaf asLeaf() {
        if(!isLeaf())
            throw new RuntimeException("ParseTree element is not a leaf");
        return (Leaf)this;
    }

    public static class Leaf extends ParseTree {
        private final Symbol symbol;
        private final String lexeme;

        public Leaf(Symbol symbol, String lexeme) {
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

    public static class Node extends ParseTree {
        private final GrammarRule rule;
        private final List<ParseTree> children;
        private final Map<String, ParseTree> tagMap;

        public Node(GrammarRule rule, List<ParseTree> children) {
            this.rule = rule;
            this.children = children;
            tagMap = new HashMap<>();
            List<Symbol> productionElements = rule.getProduction().getElements();
            for(int i = 0; i < productionElements.size(); i++) {
                if(productionElements.get(i).getTag() != null)
                    tagMap.put(productionElements.get(i).getTag(), children.get(i));
            }
        }

        @Override
        public Symbol getSymbol() {
            return rule.getNonterminal();
        }

        @Override
        public List<ParseTree> getChildren() {
            return children;
        }

        public String getRuleTag() {
            return rule.getProduction().getTag();
        }

        public ParseTree getChild(String tag) {
            return tagMap.get(tag);
        }

        public ParseTree getLeaf(String tag) {
            return getChild(tag).asLeaf();
        }

        public ParseTree getNode(String tag) {
            return getChild(tag).asNode();
        }

        public GrammarRule getRule() {
            return rule;
        }
    }
}
