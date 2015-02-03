package m10f.lipgen.parser.lr;

import m10f.lipgen.grammar.symbol.Symbol;

import java.util.Set;

public class LRParseTableConflict {
    private long state;
    private Symbol symbol;
    private LRParsingAction action1;
    private LRParsingAction action2;

    public LRParseTableConflict(long state, Symbol symbol, LRParsingAction action1, LRParsingAction action2) {
        this.state = state;
        this.symbol = symbol;
        this.action1 = action1;
        this.action2 = action2;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public LRParsingAction getAction1() {
        return action1;
    }

    public LRParsingAction getAction2() {
        return action2;
    }

    // TODO: express this as a string
}
