package m10f.lipgen.lexer;

public class TokenIterator {
    private final LexicalAnalyzer analyzer;
    private final LexerStream stream;

    public TokenIterator(LexicalAnalyzer analyzer, LexerStream stream) {

        this.analyzer = analyzer;
        this.stream = stream;
    }

    public boolean hasNext() {
        return !stream.endOfStream();
    }

    public Token next() throws LexicalException {
        return analyzer.getNextToken(stream);
    }
}
