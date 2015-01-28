package m10f.lipgen.lexer.nfa;

import m10f.lipgen.lexer.LexicalAnalyzer;

public class SimpleRegexParser {
    public SimpleRegexParser() {
        LexicalAnalyzer analyzer = new LexicalAnalyzer();

        analyzer.addRule("zero-or-more", Nfa.fromString("*"));
        analyzer.addRule("one-or-more", Nfa.fromString("+"));
        analyzer.addRule("one-or-zero", Nfa.fromString("?"));
        analyzer.addRule("alternate", Nfa.fromString("|"));
        analyzer.addRule("group begin", Nfa.fromString("("));
        analyzer.addRule("group end", Nfa.fromString(")"));


        analyzer.addRule("any", Nfa.fromString("."));
    }
}
