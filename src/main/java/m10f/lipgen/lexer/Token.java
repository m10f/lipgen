package m10f.lipgen.lexer;

import m10f.lipgen.grammar.symbol.Terminal;

public class Token {
    private Terminal symbol;
    private String lexeme;

    public Token(Terminal symbol, String lexeme) {
        this.symbol = symbol;
        this.lexeme = lexeme;
    }

    public String getLexeme() {
        return lexeme;
    }

    public Terminal getSymbol() {
        return symbol;
    }
}
