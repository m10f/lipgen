package m10f.lipgen.parser.lr;

import m10f.lipgen.parser.Symbol;

/**
* Created by jlamar on 1/21/2015.
*/
public class StateSymbolKey {
    private long state;
    private Symbol symbol;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StateSymbolKey that = (StateSymbolKey) o;

        if (state != that.state) return false;
        return symbol.equals(that.symbol);

    }

    @Override
    public int hashCode() {
        int result = (int) (state ^ (state >>> 32));
        result = 31 * result + symbol.hashCode();
        return result;
    }

    public StateSymbolKey(long state, Symbol symbol) {
        this.state = state;
        this.symbol = symbol;
    }

    public long getState() {
        return state;
    }

    public Symbol getSymbol() {
        return symbol;
    }
}
