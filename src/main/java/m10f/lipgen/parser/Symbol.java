package m10f.lipgen.parser;

public class Symbol {
    private String name;
    private boolean purelyReferential;

    public Symbol(String symbolName) {
        this(symbolName, false);
    }

    public Symbol(String symbolName, boolean purelyReferential) {
        this.name = symbolName;
        this.purelyReferential = purelyReferential;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (purelyReferential) return false;

        if (o == null || getClass() != o.getClass()) return false;

        Symbol symbol = (Symbol) o;

        return name.equals(symbol.name);

    }

    @Override
    public int hashCode() {
        if(purelyReferential)
            return super.hashCode();

        return name.hashCode();
    }
}
