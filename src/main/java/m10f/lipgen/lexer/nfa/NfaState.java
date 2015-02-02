package m10f.lipgen.lexer.nfa;

import java.util.*;

public class NfaState {
    private final HashSet<NfaState> lambdaTransitions;
    private Set<NfaStateTransition> transitions;

    public NfaState() {
        transitions = new HashSet<>();
        lambdaTransitions = new HashSet<>();
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

    public void addLambdaTransition(NfaState target) {
        lambdaTransitions.add(target);
    }

    public Set<NfaState> getLambdaTransitions() {
        return lambdaTransitions;
    }

    public Set<NfaState> getLambdaClosure() {
        if(lambdaTransitions.isEmpty())
            return Collections.singleton(this);

        HashSet<NfaState> states = new HashSet<>();
        states.add(this);
        for(NfaState lambda : lambdaTransitions)
            states.addAll(lambda.getLambdaClosure());

        return states;
    }

    public void addLambdaTransitions(Set<NfaState> other) {
        lambdaTransitions.addAll(other);
    }

    public void clearLambdaTransitions() {
        lambdaTransitions.clear();
    }
}
