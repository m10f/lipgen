package m10f.lipgen.lexer.nfa;

import m10f.lipgen.lexer.LexerStream;


public class LambdaTransition implements NfaStateTransition {
    private NfaState startState;

    public LambdaTransition(NfaState startState) {
        this.startState = startState;
    }

    @Override
    public NfaState getTargetState() {
        return startState;
    }

    @Override
    public boolean isLambda() {
        return true;
    }

    @Override
    public NfaStateTransition copy(NfaState newTarget) {
        return new LambdaTransition(newTarget);
    }

    @Override
    public boolean accept(LexerStream stream) {
        return true;
    }
}
