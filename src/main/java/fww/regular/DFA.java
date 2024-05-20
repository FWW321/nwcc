package fww.regular;

import fww.regular.proxy.ActionProxy;

import java.util.*;

import static fww.regular.NFA.contains;

public class DFA {
    private int line = 1;

    private final Set<CharSet> alphabet;

    private final Set<DState> states = new HashSet<>();

    private DState start;

    private ActionProxy actionProxy;

    public DFA(String regex) {
        this(new NFA(regex));
    }

    public DFA(NFA nfa) {
        alphabet = nfa.getAlphabet();
//        actionProxy = nfa.getActionProxy();
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

    public Set<DState> getContainSet(Set<Set<DState>> P, DState d) {
        for (Set<DState> set : P) {
            if (set.contains(d)) {
                return set;
            }
        }
        return null;
    }


    public void minSelf(Set<Set<DState>> P) {
        this.start = DState.getDState(getContainSet(P, start));
        this.states.clear();
        for (Set<DState> set : P) {
            states.add(DState.getDState(set));
        }
        minCharSet();
    }

    public boolean putTargetMap(Map<Set<DState>, Set<DState>> map, DState d, Set<DState> target) {
        for (Set<DState> set : map.keySet()) {
            if (map.get(set) == target) {
                set.add(d);
                return true;
            }
        }
        return false;
    }

    private DFA minimization() {
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
        Set<Set<DState>> R = new HashSet<>();
        while (!P.isEmpty()) {
            Set<DState> set = P.iterator().next();
            P.remove(set);
            Set<Set<DState>> T = new HashSet<>();
            T.addAll(P);
            T.addAll(R);
            T.add(set);
            boolean flag = true;
            while (flag) {
                System.out.println(flag);
                flag = false;
                Map<Set<DState>, Set<DState>> map = new HashMap<>();
                for (CharSet c : alphabet) {
                    if (set.size() <= 1) {
                        break;
                    }
                    System.out.println(c);
                    Iterator<DState> iterator = set.iterator();
                    DState d = iterator.next();
                    Set<DState> target = getContainSet(T, d.getTarge(c));
                    map.put(set, target);

                    while (iterator.hasNext()) {
                        DState state = iterator.next();
                        target = getContainSet(T, state.getTarge(c));
                        if (!putTargetMap(map, state, target)) {
                            flag = true;
                            Set<DState> other = new HashSet<>();
                            other.add(state);
                            map.put(other, target);
                            P.add(other);
                            iterator.remove();
                        }
                    }
                }
                if (set.size() <= 1) {
                    break;
                }
            }
            if (!set.isEmpty()) {
                R.add(set);
            }
//            P.remove(set); set最开始的hashcode为1076,所以在set中的位置也是"1076",后来虽然set的hashcode变为了994,但是在P中的位置还是"1076"，所以remove不掉
        }
        minSelf(R);
        return this;
    }

    private boolean MinCharSetHelper(Set<DState> states, CharSet c1, CharSet c2){
        Set<DState> result1 = new HashSet<>();
        Set<DState> result2 = new HashSet<>();
        for(DState dState : states){
            result1.add(dState.getTarge(c1));
            result2.add(dState.getTarge(c2));
        }
        if(result1.equals(result2)){
            return true;
        }
        return false;
    }

    private void minCharSet(){
        Set<CharSet> remove = new HashSet<>();
        Set<CharSet> add = new HashSet<>();
        for(CharSet c1 : alphabet){
            for(CharSet c2 : alphabet){
                if(MinCharSetHelper(states, c1, c2)){
                   remove.add(c1);
                   remove.add(c2);
                   add.add(c1.union(c2));
                }
            }
        }
        alphabet.removeAll(remove);
        alphabet.addAll(add);
    }

    //TODO:1
    public String match(String s) {
        String result = null;
        Set<Integer> aheadSits = new HashSet<>();
        int finalSit = -2;
        Stack<Character> characterStack = new Stack<>();
        Stack<DState> aheadStack = new Stack<>();
        Stack<DState> finalStack = new Stack<>();
        DState dState = start;
        if (isAhead(dState)) {
            aheadStack.push(dState);
            aheadSits.add(-1);
            if(isFinal(dState)){
                finalStack.push(dState);
                finalSit = -1;
            }
        }
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '\n') {
                line++;
            }
            characterStack.push(chars[i]);
            dState = dState.getTarge(chars[i]);
            if (dState != null && isAhead(dState)) {
                aheadStack.push(dState);
                aheadSits.add(i);
            }
            if (dState != null && isFinal(dState)) {
                finalStack.push(dState);
                finalSit = i;
            }
            if (dState == null || i == chars.length -1) {
                System.out.println("final");
                System.out.println(finalSit);
                System.out.println(finalStack);
                if(!finalStack.isEmpty()){
                    if (aheadStack.isEmpty()) {
                        result = s.substring(0, finalSit + 1);
                    } else {
                        int j = finalSit + 1;
                        DState ahead = aheadStack.pop();
                        Set<CharSet> charSets = ahead.getAheadCharSet();
                        while (!characterStack.isEmpty()) {
                            char c = characterStack.pop();
                            j--;
                            if (contains(charSets, c) && aheadSits.contains(j)) {
                                result = s.substring(0, finalSit + 1);
                            }
                        }
                    }
                }
                return result;
            }
        }
        return null;
    }

    //TODO:1
//    public String match(String input) {
//        String result = null;
//        Set<Integer> sitSet = new HashSet<>();
//        Deque<DState> aheadStack = new ArrayDeque<>();
//        DState currentState = start;
//
//        for (int i = 0; i < input.length(); i++) {
//            char currentChar = input.charAt(i);
//            if (currentChar == '\n') {
//                line++;
//            }
//
//            currentState = updateState(currentState, currentChar);
//            if (currentState == null) {
//                break;
//            }
//
//            if (isAhead(currentState, aheadStack)) {
//                aheadStack.push(currentState);
//                sitSet.add(i);
//            }
//
//            if (i == input.length() - 1 && isFinal(currentState)) {
//                result = computeResult(input, aheadStack, sitSet, i);
//                break;
//            }
//        }
//
//        if (result == null) {
//            actionProxy.failed(input, line);
//        } else {
//            actionProxy.success(result, line);
//        }
//
//        return result;
//    }

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
        DFA dfa = new DFA("a*b").minimization();
        System.out.println(dfa.match(""));
    }
}

