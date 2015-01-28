package m10f.lipgen.lexer;

public class Token {
    private String symbol;
    private String lexeme;

    public Token(String symbol, String lexeme) {
        this.symbol = symbol;
        this.lexeme = lexeme;
    }

    public String getLexeme() {
        return lexeme;
    }

    public String getSymbol() {
        return symbol;
    }
}
