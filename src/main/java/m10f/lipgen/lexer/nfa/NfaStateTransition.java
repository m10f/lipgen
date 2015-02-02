package m10f.lipgen.lexer.nfa;

public class NfaStateTransition {
    private boolean greedy;
    private NfaState targetState;
    private CharacterAcceptor acceptor;

    public NfaStateTransition(CharacterAcceptor acceptor, NfaState targetState) {
        this(acceptor, targetState, true);
    }

    public NfaStateTransition(CharacterAcceptor acceptor, NfaState targetState, boolean greedy) {
        this.targetState = targetState;
        this.acceptor = acceptor;
        this.greedy = greedy;
    }

    public NfaStateTransition copy(NfaState newState) {
        return new NfaStateTransition(acceptor, newState, greedy);
    }

    public NfaState getTargetState() {
        return targetState;
    }

    public void setTargetState(NfaState targetState) {
        this.targetState = targetState;
    }

    public CharacterAcceptor getAcceptor() {
        return acceptor;
    }

    public void setAcceptor(CharacterAcceptor acceptor) {
        this.acceptor = acceptor;
    }

    public void setGreed(boolean greed) {
        greedy = greed;
    }

    public boolean isGreedy() {
        return greedy;
    }
}
