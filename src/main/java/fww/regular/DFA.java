package fww.regular;

import fww.regular.proxy.ActionProxy;

import java.util.*;

import static fww.regular.NFA.contains;

public class DFA {
    private int line = 1;

    private final Set<CharSet> alphabet;

    private final Set<DState> states = new HashSet<>();

    private final DState start;

    private final ActionProxy actionProxy;

    public DFA(String regex){
        this(new NFA(regex));
    }
    public DFA(NFA nfa) {
        alphabet = nfa.getAlphabet();
        actionProxy = nfa.getActionProxy();;
        Set<Set<NState>> D = new HashSet<>();
        Set<Set<NState>> T = new HashSet<>();
        Set<NState> s = nfa.getNILStates(nfa.getStart());
        D.add(s);
        start = new DState(s);
        states.add(start);
        while ((s = minus(D, T)) != null) {
            T.add(s);
            DState t = getEqualState(new DState(s));
            states.add(t);
            for (CharSet c : alphabet) {
                Set<NState> u = nfa.getNILStates(nfa.move(s, c));
                if (!u.isEmpty()) {
                    D.add(u);
                    DState v = getEqualState(new DState(u));
                    states.add(v);
                    t.addTransition(c, v);
                }
            }
        }
    }

    private Set<NState> minus(Set<Set<NState>> A, Set<Set<NState>> B) {
        Set<NState> result = null;
        for (Set<NState> a : A) {
            if (!B.contains(a)) {
                result = a;
            }
        }
        return result;
    }


    //TODO:2
//    private DState getEqualState(DState d) {
//        for (DState state : states) {
//            if (state.equals(d)) {
//                return state;
//            }
//        }
//        return d;
//    }

    //TODO:2
    private DState getEqualState(DState d) {
        return states.stream()
                .filter(state -> state.equals(d))
                .findFirst()
                .orElse(d);
    }


    public boolean isFinal(DState dState) {
        return dState.isFinal();
    }

    public boolean isAhead(DState dState) {
        return dState.isAhead();
    }

    private void minimization() {
        Set<Set<DState>> P = new HashSet<>();
        Set<DState> F = new HashSet<>();
        Set<DState> N = new HashSet<>();
        for (DState state : states) {
            if (isFinal(state)) {
                F.add(state);
            } else {
                N.add(state);
            }
        }
        P.add(F);
        P.add(N);
        boolean flag = true;
        while(flag){
            flag = false;
            Map<Set<DState>, Set<DState>> map = new HashMap<>();
            for(Set<DState> set : P){
                for(DState state : set){
                    Set<DState> key = new HashSet<>();
                    for(CharSet c : alphabet){
                        DState target = state.getTarge(c);
                        if(target != null){
                            key.add(target);
                        }
                    }
                    if(!map.containsKey(key)){
                        map.put(key, new HashSet<>());
                    }
                    map.get(key).add(state);
                }
            }
        }
    }

    //TODO:1
//    public String match(String s) {
//        String result = null;
//        Set<Integer> sits = new HashSet<>();
//        Stack<Character> characterStack = new Stack<>();
//        Stack<DState> aheadStack = new Stack<>();
//        DState dState = start;
//        if (isAhead(dState)) {
//            aheadStack.push(dState);
//            sits.add(-1);
//        }
//        char[] chars = s.toCharArray();
//        for (int i = 0; i < chars.length; i++) {
//            if (chars[i] == '\n') {
//                line++;
//            }
//            characterStack.push(chars[i]);
//            System.out.println(dState);
//            dState = dState.getTarge(chars[i]);
//            System.out.println(chars[i]);
//            if(dState == null){
//                break;
//            }
//            if (isAhead(dState)) {
//                aheadStack.push(dState);
//                sits.add(i);
//            }
//            if (i == chars.length - 1) {
//                if (isFinal(dState)) {
//                    if (aheadStack.isEmpty()) {
//                        result = s;
//                    } else {
//                        int j = i + 1;
//                        DState ahead = aheadStack.pop();
//                        Set<CharSet> charSets = ahead.getAheadCharSet();
//                        while (!characterStack.isEmpty()) {
//                            char c = characterStack.pop();
//                            j--;
//                            if (contains(charSets, c) && sits.contains(j)) {
//                                result = s.substring(0, j + 1);
//                            }
//                        }
//                    }
//                } else {
//                    result = null;
//                }
//            }
//        }
//        if (result == null) {
//            actionProxy.failed(s, line);
//        } else {
//            actionProxy.success(result, line);
//        }
//        return result;
//    }

    //TODO:1
    public String match(String input) {
        String result = null;
        Set<Integer> sitSet = new HashSet<>();
        Deque<DState> aheadStack = new ArrayDeque<>();
        DState currentState = start;

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            if (currentChar == '\n') {
                line++;
            }

            currentState = updateState(currentState, currentChar);
            if (currentState == null) {
                break;
            }

            if (isAhead(currentState, aheadStack)) {
                aheadStack.push(currentState);
                sitSet.add(i);
            }

            if (i == input.length() - 1 && isFinal(currentState)) {
                result = computeResult(input, aheadStack, sitSet, i);
                break;
            }
        }

        if (result == null) {
            actionProxy.failed(input, line);
        } else {
            actionProxy.success(result, line);
        }

        return result;
    }

    private DState updateState(DState currentState, char currentChar) {
        System.out.println(currentState);
        DState nextState = currentState.getTarge(currentChar);
        System.out.println(currentChar);
        return nextState;
    }

    private boolean isAhead(DState currentState, Deque<DState> aheadStack) {
        return isAhead(currentState) && aheadStack.isEmpty();
    }

    private String computeResult(String input, Deque<DState> aheadStack, Set<Integer> sitSet, int i) {
        int j = i + 1;
        DState aheadState = aheadStack.pop();
        Set<CharSet> charSets = aheadState.getAheadCharSet();
        while (j-- > 0) {
            char c = input.charAt(j);
            if (contains(charSets, c) && sitSet.contains(j)) {
                return input.substring(0, j + 1);
            }
        }
        return input;
    }

    public static void main(String[] args) {
        DFA dfa = new DFA("a/b");
        System.out.println(dfa.match("ab"));
    }
}

