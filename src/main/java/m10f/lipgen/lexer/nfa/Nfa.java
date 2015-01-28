package m10f.lipgen.lexer.nfa;

import m10f.lipgen.lexer.LexerStream;
import sun.misc.Regexp;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class Nfa {
    private Set<NfaState> states;
    private NfaState startState;
    private Set<NfaState> finalStates;

    public Nfa() {
        states = new HashSet<>();
        finalStates = new HashSet<>();
    }

    public Nfa(Function<NfaState, NfaStateTransition> transitionGenerator) {
        this();
        startState = new NfaState();
        NfaState finalState = new NfaState();
        startState.addTransition(transitionGenerator.apply(finalState));
        finalStates.add(finalState);
        states.add(startState);
        states.add(finalState);

    }

    public Nfa(char c) {
        this(f -> new LiteralTransition(f, c));
    }

    public static Nfa fromString(String literal) {
        Nfa nfa = emptyAcceptor();
        for(int i = 0; i < literal.length(); i++)
            nfa = nfa.concat(new Nfa(literal.charAt(i)));
        return nfa;
    }

    public static Nfa emptyAcceptor() {
        Nfa result = new Nfa();
        NfaState state = new NfaState();
        result.finalStates.add(state);
        result.startState = state;
        result.states.add(state);
        return result;
    }

    public Nfa copy() {
        Nfa newNfa = new Nfa();
        final Map<NfaState, NfaState> copyMap = new HashMap<>();
        for(NfaState sourceState : states) {
            copyMap.put(sourceState, new NfaState());
        }

        for(NfaState sourceState : states) {
            for(NfaStateTransition transition : sourceState.getTransitions()) {
                NfaState targetState = copyMap.get(sourceState);
                targetState.addTransition(transition.copy(copyMap.get(transition.getTargetState())));
            }
        }

        newNfa.startState = copyMap.get(startState);
        newNfa.finalStates = finalStates.stream().map(s -> copyMap.get(s)).collect(Collectors.toSet());
        newNfa.states = new HashSet<>(copyMap.values());
        return newNfa;
    }


    public Nfa concat(Nfa other) {
        Nfa otherCopy = other.copy();

        states.addAll(otherCopy.states);
        states.remove(otherCopy.startState);
        for(NfaState finalState : finalStates)
            finalState.addTransitions(otherCopy.startState.getTransitions());

        finalStates = otherCopy.finalStates;
        return this;
    }

    public Nfa alternate(Nfa other) {
        Nfa otherCopy = other.copy();

        NfaState newStartState = new NfaState();
        NfaState newFinalState = new NfaState();

        newStartState.addTransition(new LambdaTransition(startState));
        newStartState.addTransition(new LambdaTransition(otherCopy.startState));

        for(NfaState finalState : finalStates)
            finalState.addTransition(new LambdaTransition(newFinalState));

        for(NfaState finalState : otherCopy.finalStates)
            finalState.addTransition(new LambdaTransition(newFinalState));

        states.addAll(otherCopy.states);
        states.add(newFinalState);
        states.add(newStartState);

        finalStates.clear();
        finalStates.add(newFinalState);
        startState = newStartState;

        return this;
    }

    public Nfa closure() {
        NfaState newStartState = new NfaState();
        NfaState newFinalState = new NfaState();

        newStartState.addTransition(new LambdaTransition(newFinalState));
        newStartState.addTransition(new LambdaTransition(startState));

        for(NfaState finalState : finalStates) {
            finalState.addTransition(new LambdaTransition(newFinalState));
            finalState.addTransition(new LambdaTransition(startState));
        }

        startState = newStartState;
        finalStates.clear();
        finalStates.add(newFinalState);
        states.add(newFinalState);
        states.add(newStartState);

        return this;
    }

    public boolean accept(LexerStream stream) {
        return accept(stream, startState);
    }

    private boolean accept(LexerStream stream, NfaState current) {
        int initialStreamLocation = stream.position();
        int lexemeStreamLocation = -1;
        int lexemeMaxLength = -1;

        for(NfaStateTransition transition : current.getTransitions()) {
            if(transition.accept(stream) && accept(stream, transition.getTargetState())) {
                if(lexemeMaxLength < stream.currentLexemeLength()) {
                    lexemeStreamLocation = stream.position();
                    lexemeMaxLength = stream.currentLexemeLength();
                }
            }
            stream.position(initialStreamLocation);
        }

        if(lexemeStreamLocation > 0) {
            stream.position(lexemeStreamLocation);
            return true;
        }

        return finalStates.contains(current);
    }
}
