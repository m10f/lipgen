package m10f.lipgen.lexer;

import m10f.lipgen.lexer.nfa.Nfa;
import java.util.ArrayList;
import java.util.List;


// TODO: line numbers
// TODO: dfa generation
public class LexicalAnalyzer {
    private List<LexerRule> rules;

    public LexicalAnalyzer() {
        rules = new ArrayList<>();
    }

    public void addRule(String symbol, Nfa nfa) {
        rules.add(new LexerRule(symbol, nfa, false));
    }
    public void addSkip(Nfa nfa) { rules.add(new LexerRule(null, nfa, true)); }

    public Token getNextToken(LexerStream stream) throws LexicalException {
        while(true) {
            int position = stream.position();
            LexerRule maxRule = null;
            int maxPosition = -1;
            int maxLength = -1;

            for (LexerRule rule : rules) {
                if (rule.nfa.accept(stream)) {
                    if (maxLength < stream.currentLexemeLength()) {
                        maxLength = stream.currentLexemeLength();
                        maxRule = rule;
                        maxPosition = stream.position();
                    }
                }
                stream.position(position);
            }

            if (maxRule != null) {
                stream.position(maxPosition);
                if (maxRule.skip)
                    stream.consume();
                else
                    return new Token(maxRule.symbol, stream.consume());
            } else {
                break;
            }
        }

        if(stream.endOfStream())
            return null;

        throw new LexicalException("cannot tokenize"); // TODO: add line number
    }

    private class LexerRule {
        public String symbol;
        public Nfa nfa;
        public boolean skip;

        public LexerRule(String symbol, Nfa nfa, boolean skip) {
            this.symbol = symbol;
            this.nfa = nfa;
            this.skip = skip;
        }
    }
}
