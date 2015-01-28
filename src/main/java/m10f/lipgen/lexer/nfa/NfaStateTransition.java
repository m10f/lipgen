package m10f.lipgen.lexer.nfa;
import m10f.lipgen.lexer.LexerStream;


public interface NfaStateTransition {
    public NfaState getTargetState();
    public boolean isLambda();
    public NfaStateTransition copy(NfaState newTarget);
    public boolean accept(LexerStream stream);
}
