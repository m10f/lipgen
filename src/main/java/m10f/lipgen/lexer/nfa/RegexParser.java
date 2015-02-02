package m10f.lipgen.lexer.nfa;

import m10f.lipgen.grammar.Grammar;
import m10f.lipgen.grammar.symbol.Nonterminal;
import m10f.lipgen.grammar.symbol.Terminal;
import m10f.lipgen.lexer.*;
import m10f.lipgen.parser.ParseTree;
import m10f.lipgen.parser.SyntaxException;
import m10f.lipgen.parser.lr.LRParser;


public class RegexParser {
    private static final String ANY = "any";
    private static final String LITERAL = "literal";
    private static final String CLOSURE_EXPRESSION = "closure expression";
    private static final String ALTERNATION_EXPRESSION = "alternation expression";
    private static final String CATENATION_EXPRESSION = "catenation expression";
    private static final String BASE_REGEX = "base regex";
    private LexicalAnalyzer lexicalAnalyzer;
    private LRParser parser;

    public RegexParser() {
        Grammar regexGrammar = new Grammar();
        Terminal closure = regexGrammar.terminal("*", Nfa.fromString("*"));
        Terminal plus = regexGrammar.terminal("+", Nfa.fromString("+"));
        Terminal nonGreedyClosure = regexGrammar.terminal("*?", Nfa.fromString("*?"));
        Terminal nonGreedyPlus = regexGrammar.terminal("+?", Nfa.fromString("+?"));
        Terminal nonGreedy = regexGrammar.terminal("?", Nfa.fromString("?"));
        Terminal openGroup = regexGrammar.terminal("(", Nfa.fromString("("));
        Terminal closedGroup = regexGrammar.terminal(")", Nfa.fromString(")"));
        Terminal alternation = regexGrammar.terminal("|", Nfa.fromString("|"));

        Terminal any = regexGrammar.terminal(ANY, Nfa.fromString("."));
        Terminal literal = regexGrammar.terminal(LITERAL, Nfa.matchAny());

        Nonterminal closureExpression = regexGrammar.nonterminal(CLOSURE_EXPRESSION);
        Nonterminal alternationExpression = regexGrammar.nonterminal(ALTERNATION_EXPRESSION);
        Nonterminal catenationExpression = regexGrammar.nonterminal(CATENATION_EXPRESSION);
        Nonterminal baseRegex = regexGrammar.nonterminal(BASE_REGEX);

        // TODO: once precedence rules are in, use precedence
        regexGrammar.setStartSymbol(alternationExpression);

        alternationExpression.withRule("base", catenationExpression.tag("base"))
                .withRule("alt", alternationExpression.tag("left"), alternation, catenationExpression.tag("right"));


        catenationExpression.withRule("base", closureExpression.tag("base"))
                            .withRule("cat", catenationExpression.tag("base"), closureExpression.tag("next"));

        closureExpression.withRule("base", baseRegex.tag("base"))
                .withRule("closure", closureExpression.tag("base"), closure)
                .withRule("plus-closure", closureExpression.tag("base"), plus)
                .withRule("non-greedy-closure", closureExpression.tag("base"), nonGreedyClosure)
                .withRule("non-greedy-plus", closureExpression.tag("base"), nonGreedyPlus)
                .withRule("optional", closureExpression.tag("base"), nonGreedy);


        baseRegex.withRule("literal", literal.tag("value"))
                .withRule("any", any.tag("value"))
                .withRule("group", openGroup, catenationExpression.tag("value"), closedGroup);

        lexicalAnalyzer = regexGrammar.getLexer();
        parser = new LRParser(regexGrammar.getLrParseTable());
    }

    public Nfa buildNfa(ParseTree tree) {
        switch(tree.getSymbol().getName()) {
            case CATENATION_EXPRESSION: {
                ParseTree.Node node = tree.asNode();
                switch (node.getRuleTag()) {
                    case "base":
                        return buildNfa(node.getChild("base"));
                    case "cat":
                        return buildNfa(node.getChild("base")).concat(buildNfa(node.getChild("next")));
                }
            }

            case ALTERNATION_EXPRESSION: {
                ParseTree.Node node = tree.asNode();
                switch (node.getRuleTag()) {
                    case "base":
                        return buildNfa(node.getChild("base"));
                    case "alt":
                        return buildNfa(node.getChild("left")).alternate(buildNfa(node.getChild("right")));
                }
            }

            case CLOSURE_EXPRESSION: {
                ParseTree.Node node = tree.asNode();
                switch(node.getRuleTag()) {
                    case "base":
                        return buildNfa(node.getChild("base"));
                    case "closure":
                        return buildNfa(node.getChild("base")).closure();
                    case "plus-closure":
                        Nfa inner = buildNfa(node.getChild("base"));
                        return inner.concat(inner.copy().closure());
                    case "non-greedy-closure":
                        return buildNfa(node.getChild("base")).closure().withGreed(false);
                    case "non-greedy-plus":
                        inner = buildNfa(node.getChild("base"));
                        return inner.concat(inner.copy().closure().withGreed(false));
                    case "optional":
                        return buildNfa(node.getChild("base")).alternate(Nfa.emptyAcceptor());
                }
            }

            case BASE_REGEX:
                return buildNfa(tree.asNode().getChild("value"));

            case LITERAL:
                return new Nfa(tree.asLeaf().getLexeme().charAt(0));

            case ANY:
                return Nfa.matchAny();
        }
        throw new RuntimeException("Unimplemented symbol " + tree.getSymbol().getName());
    }

    public Nfa parseRegex(String input) throws IllegalArgumentException {
        try {
            LexerStream stream = new LexerStream(input);
            TokenIterator iterator = new TokenIterator(lexicalAnalyzer, stream);
            return buildNfa(parser.parse(iterator));
        } catch(LexicalException | SyntaxException ex) {
            throw new IllegalArgumentException("invalid regex", ex);
        }
    }

    private static RegexParser singleton;
    private static Object singletonLock = new Object();
    public static RegexParser current() {
        if(singleton == null) {
            synchronized (singletonLock) {
                if(singleton == null) {
                    singleton = new RegexParser();
                }
            }
        }
        return singleton;
    }
}