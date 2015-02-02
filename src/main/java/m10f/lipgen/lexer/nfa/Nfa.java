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
        this(f -> new NfaStateTransition(new CharacterAcceptor.Literal(c), f));
    }

    public static Nfa matchAny() {
        return new Nfa(f -> new NfaStateTransition(new CharacterAcceptor.Any(), f));
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
            NfaState targetState = copyMap.get(sourceState);
            for(NfaStateTransition transition : sourceState.getTransitions()) {
                targetState.addTransition(transition.copy(copyMap.get(transition.getTargetState())));
            }

            for(NfaState lambda : sourceState.getLambdaTransitions()) {
                targetState.addLambdaTransition(copyMap.get(lambda));
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
        for(NfaState finalState : finalStates) {
            finalState.addTransitions(otherCopy.startState.getTransitions());
            finalState.addLambdaTransitions(otherCopy.startState.getLambdaTransitions());
        }

        finalStates = otherCopy.finalStates;
        return this;
    }

    public Nfa alternate(Nfa other) {
        Nfa otherCopy = other.copy();

        NfaState newStartState = new NfaState();
        NfaState newFinalState = new NfaState();

        newStartState.addLambdaTransition(startState);
        newStartState.addLambdaTransition(otherCopy.startState);

        for(NfaState finalState : finalStates)
            finalState.addLambdaTransition(newFinalState);

        for(NfaState finalState : otherCopy.finalStates)
            finalState.addLambdaTransition(newFinalState);

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

        newStartState.addLambdaTransition(newFinalState);
        newStartState.addLambdaTransition(startState);

        for(NfaState finalState : finalStates) {
            finalState.addLambdaTransition(newFinalState);
            finalState.addLambdaTransition(startState);
        }

        startState = newStartState;
        finalStates.clear();
        finalStates.add(newFinalState);
        states.add(newFinalState);
        states.add(newStartState);

        return this;
    }

    public Nfa withGreed(boolean greed) {
        for(NfaState state : states) {
            for(NfaStateTransition transition : state.getTransitions())
                transition.setGreed(greed);
        }
        return this;
    }

    public void removeLambdas() {
        HashSet<NfaState> transitionStates = new HashSet<>();
        transitionStates.add(startState);

        for(NfaState state : states) {
            Set<NfaState> lambdaClosure = state.getLambdaClosure();
            for(NfaState closureState : state.getLambdaClosure()) {
                state.addTransitions(closureState.getTransitions());
            }
            if(finalStates.stream().anyMatch(f -> lambdaClosure.contains(finalStates))) {
                finalStates.add(state);
            }
            for(NfaStateTransition transition : state.getTransitions()) {
                transitionStates.add(transition.getTargetState());
            }
        }

        states = transitionStates;
        for(NfaState state : states)
            state.clearLambdaTransitions();

        states.add(startState);
        finalStates.removeIf(s -> !states.contains(s));
    }

    public boolean accept(LexerStream stream) {
        Set<NfaState> currentStates = null;
        Set<NfaState> nextStates = new HashSet<>();
        nextStates.add(startState);
        nextStates.addAll(startState.getLambdaClosure());

        int lastRead = stream.position();

        while(!nextStates.isEmpty()) {
            currentStates = nextStates;
            nextStates = new HashSet<>();

            lastRead = stream.position();
            int readResult = stream.readChar();
            if(readResult == -1) {
                return containsAnyFinalStates(currentStates);
            }

            boolean allNonGreedy = true;
            char currentChar = (char)readResult;
            for(NfaState state : currentStates) {
                for(NfaStateTransition transition : state.getTransitions()) {
                    if(transition.getAcceptor().accept(currentChar)) {
                        nextStates.add(transition.getTargetState());
                        nextStates.addAll(transition.getTargetState().getLambdaClosure());
                        allNonGreedy = allNonGreedy && !transition.isGreedy();
                    }
                }
            }

            // break out of here if we can only add non-greedy characters to a successful match
            if(allNonGreedy && containsAnyFinalStates(currentStates))
                break;
        }

        stream.position(lastRead);
        return containsAnyFinalStates(currentStates);
    }

    private boolean containsAnyFinalStates(Set<NfaState> nfaStates) {
        return finalStates.stream().anyMatch(s -> nfaStates.contains(s));
    }
}
