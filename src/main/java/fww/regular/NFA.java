package fww.regular;

import java.util.*;

public class NFA {
    private Set<CharSet> alphabet = new HashSet<>();

    private State start;

    private State end;

    private Set<State> states = new HashSet<>();

    private NFA() {
        alphabet.add(new CharSet());
    }

    public NFA(String regex) {
        NFA nfa = builder(regex);
        this.alphabet = nfa.alphabet;
        this.start = nfa.start;
        this.end = nfa.end;
        this.states = nfa.states;
    }

    public void addState(State state) {
        updateAlphabet(state);
        states.add(state);
    }

    public void updateAlphabet(State state) {
        Set<CharSet> newCharSets = new HashSet<>(alphabet);

        Set<CharSet> cct = new HashSet<>();
        for(CharSet t : state.getTransition()) {
            for(CharSet cc : alphabet) {
                CharSet cc1 = cc.intersection(t);
                if(cc1 == null) {
                    continue;
                }
                CharSet cc2 = cc.minus(t);
                cct.add(cc1);
                if (!cc2.isEmpty()) {
                    newCharSets.remove(cc);
                    newCharSets.add(cc1);
                    newCharSets.add(cc2);
                    updateStates(cc, cc1, cc2);
                }
                System.out.println("----------c-----------");
                System.out.println(t);
                System.out.println(cc);
                t = t.minus(cc);
                System.out.println(t);
                System.out.println("---------c------------");
                if(t.isEmpty()) {
                    break;
                }
            }
        }
        alphabet = newCharSets;
        System.out.println("----------a-----------");
        System.out.println(alphabet);
        System.out.println("---------a------------");
        System.out.println("----------s-----------");
        System.out.println(cct);
        state.setTransition(cct);
        System.out.println("---------s------------");
    }

    public void updateStates(CharSet cc, CharSet cc1, CharSet cc2) {
        for(State state : states) {
            Set<CharSet> newTransitions = new HashSet<>();
            for(CharSet t : state.getTransition()) {
                if(t.equals(cc)) {
                    newTransitions.add(cc1);
                    newTransitions.add(cc2);
                } else {
                    newTransitions.add(t);
                }
            }
            state.setTransition(newTransitions);
        }
    }

    private NFA builder(String regex){
        List<NFA> concat = new ArrayList<>();
        char[] chars = regex.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            NFA nfa = null;
            char c = chars[i];
            if (c == '(') {
                int j = i + 1;
                int count = 1;
                while (count != 0) {
                    if (chars[j] == '(') {
                        count++;
                    } else if (chars[j] == ')') {
                        count--;
                    }
                    j++;
                }
                nfa = builder(regex.substring(i + 1, j - 1));
                System.out.println(nfa);
                System.out.println(regex.substring(i + 1, j - 1));
                i = j - 1;
            } else if (c == '|') {
                NFA s = concat(concat);
                NFA t = builder(regex.substring(i + 1));
                nfa = orRule(s, t);
                i = chars.length - 1;
            } else if (c == '*') {
                NFA n = concat.getLast();
                concat.removeLast();
                nfa = starRule(n);
            } else if (c == '+') {
                NFA n = concat.getLast();
                concat.removeLast();
                nfa = plusRule(n);
            } else if (c == '?') {
                NFA n = concat.getLast();
                concat.removeLast();
                nfa = questionRule(n);
            } else if (c == '\\') {
                if(i < chars.length - 1){
                    nfa = baseRule(chars[i + 1]);
                }
                i = i + 1;
            } else {
                nfa = baseRule(c);
                System.out.println(nfa);
            }
            System.out.println(nfa);
            concat.add(nfa);
        }
        System.out.println(concat);
        return concat(concat);
    }

    private NFA baseRule(char c){
        NFA nfa = new NFA();
        State start = new State();
        State end = new State();
        end.setType(StateType.FINAL);
        start.addTransition(new CharSet(c), end);
        nfa.addState(start);
        nfa.addState(end);
        nfa.start = start;
        nfa.end = end;
        return nfa;
    }

    private NFA orRule(NFA s, NFA t){
        NFA nfa = new NFA();
        State start = new State();
        State end = new State();
        end.setType(StateType.FINAL);
        start.addEpsilon(s.start);
        start.addEpsilon(t.start);
        s.end.addEpsilon(end);
        t.end.addEpsilon(end);
        s.end.setType(StateType.NORMAL);
        t.end.setType(StateType.NORMAL);
        nfa.addState(start);
        nfa.addState(end);
        nfa.states.addAll(s.states);
        nfa.states.addAll(t.states);
        nfa.start = start;
        nfa.end = end;
        return nfa;
    }

    private NFA concatRule(NFA s, NFA t){
        NFA nfa = new NFA();
        State start = new State();
        State end = new State();
        end.setType(StateType.FINAL);
        start.addEpsilon(s.start);
        s.end.addEpsilon(t.start);
        t.end.addEpsilon(end);
        s.end.setType(StateType.NORMAL);
        t.end.setType(StateType.NORMAL);
        nfa.addState(start);
        nfa.addState(end);
        nfa.states.addAll(s.states);
        nfa.states.addAll(t.states);
        nfa.start = start;
        nfa.end = end;
        return nfa;
    }

    private NFA starRule(NFA n){
        NFA nfa = new NFA();
        State start = new State();
        State end = new State();
        end.setType(StateType.FINAL);
        start.addEpsilon(n.start);
        start.addEpsilon(end);
        n.end.addEpsilon(n.start);
        n.end.addEpsilon(end);
        n.end.setType(StateType.NORMAL);
        nfa.addState(start);
        nfa.addState(end);
        nfa.states.addAll(n.states);
        nfa.start = start;
        nfa.end = end;
        return nfa;
    }

    private NFA plusRule(NFA n){
        NFA nfa = new NFA();
        State start = new State();
        State end = new State();
        end.setType(StateType.FINAL);
        start.addEpsilon(n.start);
        n.end.addEpsilon(end);
        n.end.addEpsilon(n.start);
        n.end.setType(StateType.NORMAL);
        nfa.addState(start);
        nfa.addState(end);
        nfa.states.addAll(n.states);
        nfa.start = start;
        nfa.end = end;
        return nfa;
    }

    private NFA questionRule(NFA n){
        NFA nfa = new NFA();
        State start = new State();
        State end = new State();
        end.setType(StateType.FINAL);
        start.addEpsilon(n.start);
        start.addEpsilon(end);
        n.end.addEpsilon(end);
        n.end.setType(StateType.NORMAL);
        nfa.addState(start);
        nfa.addState(end);
        nfa.states.addAll(n.states);
        nfa.start = start;
        nfa.end = end;
        return nfa;
    }

    private NFA dotRule(){
        NFA nfa = new NFA();
        State start = new State();
        State end = new State();
        end.setType(StateType.FINAL);
        start.addTransition(new CharSet(), end);
        nfa.addState(start);
        nfa.addState(end);
        nfa.start = start;
        nfa.end = end;
        return nfa;
    }

    public Set<State> getNILStates(State state) {
        Set<State> nilStates = new HashSet<>();
        nilStates.add(state);
        for(State epsilon : state.getEpsilon()) {
            nilStates.addAll(getNILStates(epsilon));
        }
        return nilStates;
    }

    public Set<State> getNILStates(Set<State> states) {
        Set<State> nilStates = new HashSet<>();
        for(State state : states) {
            nilStates.addAll(getNILStates(state));
        }
        return nilStates;
    }

    private NFA concat(List<NFA> concat){
        NFA s = concat.getFirst();
        for (int i = 1; i < concat.size(); i++) {
            NFA t = concat.get(i);
            s = concatRule(s, t);
        }
        concat.clear();
        return s;
    }

    private Set<State> move(Set<State> states, char c){
        Set<State> moveStates = new HashSet<>();
        for(State state : states) {
            if(state.contains(c)) {
                moveStates.add(state);
            }
        }
        return moveStates;
    }

    private boolean isFinal(Set<State> states){
        for(State state : states) {
            if(state.getType() == StateType.FINAL) {
                return true;
            }
        }
        return false;
    }

    public boolean match(String s){
        Set<State> states = getNILStates(start);
        if(isFinal(states)){
            return true;
        }
        for (char c : s.toCharArray()) {
            states = getNILStates(move(states, c));
            if(isFinal(states)){
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        NFA nfa = new NFA("a");
        System.out.println(nfa.match("a"));
    }
}
