package m10f.lipgen.parser.lr;

import m10f.lipgen.parser.Symbol;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LRParsingTable {
    // TODO: this appears in table form in the dragon book - but it might make more sense in graph form
    private Map<StateSymbolKey, Long> gotoTable;
    private Map<StateSymbolKey, LRParsingAction> actionTable;
    private long initialState;
    private Symbol endSymbol;

    public LRParsingTable() {
        actionTable = new HashMap<>();
        gotoTable = new HashMap<>();
    }

    public void putAction(long state, Symbol s, LRParsingAction action) {
        getActionTable().put(new StateSymbolKey(state, s), action);
    }

    public void putGoto(long initialState, Symbol s, long gotoState) {
        getGotoTable().put(new StateSymbolKey(initialState, s), gotoState);
    }

    public Map<StateSymbolKey, LRParsingAction> getActionTable() {
        return actionTable;
    }

    public Map<StateSymbolKey, Long> getGotoTable() {
        return gotoTable;
    }

    public Optional<LRParsingAction> getAction(long state, Symbol symbol) {
        StateSymbolKey key = new StateSymbolKey(state, symbol);
        if(getActionTable().containsKey(key)) {
            return Optional.of(getActionTable().get(key));
        }
        return Optional.empty();
    }

    public Optional<Long> getGoto(long state, Symbol symbol) {
        StateSymbolKey key = new StateSymbolKey(state, symbol);
        if(getGotoTable().containsKey(key)) {
            return Optional.of(getGotoTable().get(key));
        }
        return Optional.empty();
    }

    public void setInitialState(long initialState) {
        this.initialState = initialState;
    }

    public long getInitialState() {
        return initialState;
    }

    public void setEndSymbol(Symbol endSymbol) {
        this.endSymbol = endSymbol;
    }

    public Symbol getEndSymbol() {
        return endSymbol;
    }


}
