package m10f.lipgen.parser.lr;

import m10f.lipgen.grammar.symbol.GrammarRule;

public class LRParsingAction {
    private Type type;
    private long shiftState;
    private GrammarRule reduceRule;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LRParsingAction that = (LRParsingAction) o;

        if (type != that.type) return false;
        if (type == Type.SHIFT && shiftState != that.shiftState) return false;
        return !(type == Type.REDUCE && reduceRule != null ? !reduceRule.equals(that.reduceRule) : that.reduceRule != null);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        if(type == Type.SHIFT)
            result = 31 * result + (int) (shiftState ^ (shiftState >>> 32));
        if(type == Type.REDUCE)
            result = 31 * result + (reduceRule != null ? reduceRule.hashCode() : 0);
        return result;
    }

    private LRParsingAction() {}

    public static LRParsingAction shiftAction(long state) {
        LRParsingAction action = new LRParsingAction();
        action.shiftState = state;
        action.type = Type.SHIFT;
        return action;
    }

    public static LRParsingAction reduceAction(GrammarRule rule) {
        LRParsingAction action = new LRParsingAction();
        action.reduceRule = rule;
        action.type = Type.REDUCE;
        return action;
    }

    public static LRParsingAction acceptAction() {
        LRParsingAction action = new LRParsingAction();
        action.type = Type.ACCEPT;
        return action;
    }

    public Type actionType() {
        return type;
    }

    public long getShiftState() {
        return shiftState;
    }

    public GrammarRule getReduceRule() {
        return reduceRule;
    }

    public enum Type {
        ACCEPT,
        SHIFT,
        REDUCE,
    }
}
