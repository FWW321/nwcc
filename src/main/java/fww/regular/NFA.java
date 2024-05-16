package fww.regular;

import java.util.*;

public class NFA {
    private Set<CharSet> alphabet = new HashSet<>();

    private State start;

    private State end;

    private Set<State> states = new HashSet<>();

    private NFA() {
        alphabet.add(new CharSet(true));
    }

    public NFA(String regex) {
        NFA nfa = builder(regex);
        this.alphabet = nfa.alphabet;
        this.start = nfa.start;
        this.end = nfa.end;
        this.states = nfa.states;
    }

    public void addState(State state) {
        state.setTransition(updateAlphabet(state.getTransition()));
        states.add(state);
    }

    public Set<CharSet> updateAlphabet(Set<CharSet> charSets) {
        Set<CharSet> newCharSets = new HashSet<>(alphabet);

        Set<CharSet> cct = new HashSet<>();
        for (CharSet t : charSets) {
            for (CharSet cc : alphabet) {
                CharSet cc1 = cc.intersection(t);
                if (cc1 == null) {
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
                t = t.minus(cc);
                if (t.isEmpty()) {
                    break;
                }
            }
        }
        alphabet = newCharSets;
        return cct;
    }

    public void updateStates(CharSet cc, CharSet cc1, CharSet cc2) {
        for (State state : states) {
            Set<CharSet> newTransitions = new HashSet<>();
            for (CharSet t : state.getTransition()) {
                if (t.equals(cc)) {
                    newTransitions.add(cc1);
                    newTransitions.add(cc2);
                } else {
                    newTransitions.add(t);
                }
            }
            state.setTransition(newTransitions);
        }
    }

    private NFA builder(String regex) {
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
                i = j - 1;
            } else if (c == '[') {
                int j = i + 1;
                while (chars[j] != ']') {
                    j++;
                }
                nfa = charClassRule(regex.substring(i + 1, j));
                i = j;
            } else if (c == '.') {
                nfa = dotRule();
            } else if (c == '|') {
                NFA s = concat(concat);
                NFA t = builder(regex.substring(i + 1));
                nfa = orRule(s, t);
                i = chars.length - 1;
            } else if (c == '/') {
                NFA s = concat(concat);
                NFA t = builder(regex.substring(i + 1));
                nfa = lookaheadRule(s, t);
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
                if (i < chars.length - 1) {
                    int j = i + 1;
                    if (chars[j] == 's') {
                        nfa = sRule();
                    } else {
                        nfa = baseRule(chars[j]);
                    }
                    i = j;
                }
            } else {
                nfa = baseRule(c);
            }
            concat.add(nfa);
        }
        return concat(concat);
    }

    private NFA baseRule(char c) {
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

    private NFA orRule(NFA s, NFA t) {
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

    private NFA concatRule(NFA s, NFA t) {
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

    private NFA starRule(NFA n) {
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

    private NFA plusRule(NFA n) {
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

    private NFA questionRule(NFA n) {
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

    private NFA dotRule() {
        NFA nfa = new NFA();
        State start = new State();
        State end = new State();
        end.setType(StateType.FINAL);
        start.addTransition(new CharSet(true), end);
        nfa.addState(start);
        nfa.addState(end);
        nfa.start = start;
        nfa.end = end;
        return nfa;
    }

    private NFA charClassRule(String regex) {
        NFA nfa = new NFA();
        State start = new State();
        State end = new State();
        end.setType(StateType.FINAL);
        CharSet charSet = new CharSet(CharSet.charGroupToArray(regex));
        start.addTransition(charSet, end);
        nfa.addState(start);
        nfa.addState(end);
        nfa.start = start;
        nfa.end = end;
        return nfa;
    }

    //TODO: 该向前看运算符不能识别a/b，当a变长时，a的后缀与b的前缀有公共部分的情况
    private NFA lookaheadRule(NFA s, NFA t){
        NFA nfa = new NFA();
        State start = new State();
        State end = new State();
        end.setType(StateType.FINAL);
        start.addEpsilon(s.start);
        s.end.addEpsilon(t.start);
        t.end.addEpsilon(end);
        t.end.setType(StateType.NORMAL);
        s.end.setType(StateType.AHEAD);
        nfa.addState(start);
        nfa.addState(end);
        nfa.states.addAll(s.states);
        nfa.states.addAll(t.states);
        nfa.start = start;
        nfa.end = end;
        return nfa;
    }

    private NFA sRule(){
        return builder("( |\t|\n|\r)*");
    }

    public Set<State> getNILStates(State state) {
        Set<State> nilStates = new HashSet<>();
        nilStates.add(state);
        for (State epsilon : state.getEpsilon()) {
            nilStates.addAll(getNILStates(epsilon));
        }
        return nilStates;
    }

    public Set<State> getNILStates(Set<State> states) {
        Set<State> nilStates = new HashSet<>();
        for (State state : states) {
            nilStates.addAll(getNILStates(state));
        }
        return nilStates;
    }

    private NFA concat(List<NFA> concat) {
        NFA s = concat.getFirst();
        for (int i = 1; i < concat.size(); i++) {
            NFA t = concat.get(i);
            s = concatRule(s, t);
        }
        concat.clear();
        return s;
    }

    private Set<State> move(Set<State> states, char c) {
        Set<State> moveStates = new HashSet<>();
        for (State state : states) {
            if (state.contains(c)) {
                moveStates.add(state.getTarget());
            }
        }
        System.out.println(moveStates);
        return moveStates;
    }

    private boolean isFinal(Set<State> states) {
        for (State state : states) {
            if (state.getType() == StateType.FINAL || state.getType() == StateType.AHEAD_FINAL){
                return true;
            }
        }
        return false;
    }

    private boolean isAhead(Set<State> states) {
        for (State state : states) {
            if (state.getType() == StateType.AHEAD || state.getType() == StateType.AHEAD_FINAL){
                return true;
            }
        }
        return false;
    }

    private Set<State> getAheads(Set<State> states){
        Set<State> aheadStates = new HashSet<>();
        for(State state : states){
            if(state.getType() == StateType.AHEAD || state.getType() == StateType.AHEAD_FINAL){
                aheadStates.add(state);
            }
        }
        return aheadStates;
    }

    //TODO: 接下来将ahead状态的直接前转换作为ahead状态的属性，以便于在DNA中也能实现向前看运算符
    public String match(String s) {
        System.out.println("match");
        Set<Integer> sits = new HashSet<>();
        Stack<Character> characterStack = new Stack<>();
        Stack<Set<State>> aheadStack = new Stack<>();
        Set<State> states = getNILStates(start);
        if(isAhead(states)){
            aheadStack.push(states);
            sits.add(-1);
        }
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            characterStack.push(chars[i]);
            states = getNILStates(move(states, chars[i]));
            if(isAhead(states)){
                aheadStack.push(states);
                sits.add(i);
            }
            if (i == chars.length - 1) {
                if (isFinal(states)) {
                    if(aheadStack.isEmpty()){
                        return s;
                    }else {
                        int j = i + 1;
                        System.out.println("ahead");
                        System.out.println(aheadStack);
                        Set<State> ahead = aheadStack.pop();
                        Set<CharSet> charSets = getDirectBeforeTranslation(getAheads(ahead));
                        System.out.println(charSets);
                        while(!characterStack.isEmpty()){
                            char c = characterStack.pop();
                            j--;
                            if(contains(charSets, c) && sits.contains(j)){
                                System.out.println("ahead match");
                                System.out.println(j);
                                return s.substring(0, j+1);
                            }
                        }
                    }
                } else {
                    return "";
                }
            }
        }
        return "";
    }

    private boolean contains(Set<CharSet> charSets, char c) {
        for (CharSet charSet : charSets) {
            if (charSet.contains(c)) {
                return true;
            }
        }
        return false;
    }

    private Set<CharSet> getDirectTranslation(State state){
        Set<CharSet> charSets = new HashSet<>();
        for(State s : states){
            if(s.getTarget() == state){
                charSets.addAll(s.getTransition());
            }

            if(s.getEpsilon().contains(state)){
                charSets.addAll(Objects.requireNonNull(getDirectTranslation(s)));
            }
        }
        return charSets;
    }

    private Set<CharSet> getDirectBeforeTranslation(Set<State> states){
        Set<CharSet> charSets = new HashSet<>();
        for(State state : states){
            charSets.addAll(getDirectTranslation(state));
        }
        return charSets;
    }

    @Override
    public String toString() {
        return "NFA{" +
                "alphabet=" + alphabet +
                ", start=" + start +
                ", end=" + end +
                ", states=" + states +
                '}';
    }


    public static void main(String[] args) {
        NFA nfa = new NFA("a(a/b)a");
        System.out.println(nfa.match("aab"));
    }
}
