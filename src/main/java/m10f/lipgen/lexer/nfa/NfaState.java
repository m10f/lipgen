package m10f.lipgen.lexer.nfa;

import java.util.*;

public class NfaState {
    private Set<NfaStateTransition> transitions;

    public NfaState() {
        transitions = new HashSet<>();
    }

    public Set<NfaStateTransition> getTransitions() {
        return transitions;
    }

    public void setTransitions(Collection<NfaStateTransition> transitions) {
        this.transitions.clear();
        addTransitions(transitions);
    }

    public void addTransitions(Collection<NfaStateTransition> transitions) {
        this.transitions.addAll(transitions);
    }

    public void addTransition(NfaStateTransition transition) {
        if(transition == null)
            throw new IllegalArgumentException("transition cannot be null");

        this.transitions.add(transition);
    }
}
