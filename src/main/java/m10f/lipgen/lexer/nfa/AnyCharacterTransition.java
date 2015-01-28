package m10f.lipgen.lexer.nfa;

import m10f.lipgen.lexer.LexerStream;

public class AnyCharacterTransition implements NfaStateTransition {
    private NfaState target;

    public AnyCharacterTransition(NfaState target) {
        this.target = target;
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
        return new AnyCharacterTransition(newTarget);
    }

    @Override
    public boolean accept(LexerStream stream) {
        return stream.readChar() > 0;
    }
}
