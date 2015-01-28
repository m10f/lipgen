package m10f.lipgen.lexer.nfa;

import m10f.lipgen.lexer.LexerStream;

import java.util.Set;

// this is basically a compressed form of alternation
public class CharacterClassTransition implements NfaStateTransition {
    private final NfaState targetState;
    private final Set<CharacterRange> range;
    private boolean exclude;

    public CharacterClassTransition(NfaState targetState, Set<CharacterRange> range, boolean exclude) {
        this.targetState = targetState;
        this.range = range;
        this.exclude = exclude;
    }

    @Override
    public NfaState getTargetState() {
        return targetState;
    }

    @Override
    public boolean isLambda() {
        return false;
    }

    @Override
    public NfaStateTransition copy(NfaState newTarget) {
        return new CharacterClassTransition(targetState, range, exclude);
    }

    @Override
    public boolean accept(LexerStream stream) {
        final int c = stream.readChar();
        return c > 0 && range.stream().anyMatch(r -> r.contains((char)c)) == !exclude;
    }

    public static class CharacterRange {
        private final char low;
        private final char high;

        public CharacterRange(char c1, char c2) {
            low = c1 < c2 ? c1 : c2;
            high = c2 > c1 ? c2 : c1;
        }

        public char getLow() {
            return low;
        }

        public char getHigh() {
            return high;
        }

        public boolean contains(char c) {
            return c >= low && c <= high;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CharacterRange that = (CharacterRange) o;

            if (high != that.high) return false;
            if (low != that.low) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = (int) low;
            result = 31 * result + (int) high;
            return result;
        }
    }
}
