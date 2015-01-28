package m10f.lipgen.lexer.nfa;

import m10f.lipgen.lexer.LexerStream;


public class LiteralTransition implements NfaStateTransition {
    private final NfaState target;
    private final char literal;

    public LiteralTransition(NfaState target, char literal) {
        this.target = target;
        this.literal = literal;
    }

    @Override
    public NfaState getTargetState() {
        return target;
    }

    @Override
    public boolean isLambda() {
        return false;
    }

    @Override
    public NfaStateTransition copy(NfaState newTarget) {
        return new LiteralTransition(newTarget, literal);
    }

    @Override
    public boolean accept(LexerStream stream) {
        return stream.readChar() == literal;
    }
}
