package m10f.lipgen.grammar.symbol;

import m10f.lipgen.lexer.nfa.Nfa;

public class Terminal implements Symbol {
    private final String symbolName;

    public Terminal(String symbolName) {
        this.symbolName = symbolName;
    }

    public Tagged<Terminal> tag(String tag) {
        return new Tagged<>(this, tag);
    }

    @Override
    public String getName() {
        return symbolName;
    }

    @Override
    public String getTag() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Symbol)) return false;

        Symbol terminal = (Symbol) o;

        if (!symbolName.equals(terminal.getName())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return symbolName.hashCode();
    }
}
