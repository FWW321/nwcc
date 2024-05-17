package fww.regular;

import fww.regular.proxy.ActionProxy;

import java.util.*;

import static fww.regular.NFA.contains;

public class DFA {
    private int line = 1;

    private Set<CharSet> alphabet;

    private Set<DState> states = new HashSet<>();

    private DState start;

    private ActionProxy actionProxy;

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

    private DState getEqualState(DState d) {
        for (DState state : states) {
            if (state.equals(d)) {
                return state;
            }
        }
        return d;
    }

    public boolean isFinal(DState dState) {
        return dState.isFinal();
    }

    public boolean isAhead(DState dState) {
        return dState.isAhead();
    }



    public String match(String s) {
        String result = null;
        Set<Integer> sits = new HashSet<>();
        Stack<Character> characterStack = new Stack<>();
        Stack<DState> aheadStack = new Stack<>();
        DState dState = start;
        if (isAhead(dState)) {
            aheadStack.push(dState);
            sits.add(-1);
        }
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '\n') {
                line++;
            }
            characterStack.push(chars[i]);
            System.out.println(dState);
            dState = dState.getTarge(chars[i]);
            System.out.println(chars[i]);
            if(dState == null){
                break;
            }
            if (isAhead(dState)) {
                aheadStack.push(dState);
                sits.add(i);
            }
            if (i == chars.length - 1) {
                if (isFinal(dState)) {
                    if (aheadStack.isEmpty()) {
                        result = s;
                    } else {
                        int j = i + 1;
                        DState ahead = aheadStack.pop();
                        Set<CharSet> charSets = ahead.getAheadCharSet();
                        while (!characterStack.isEmpty()) {
                            char c = characterStack.pop();
                            j--;
                            if (contains(charSets, c) && sits.contains(j)) {
                                result = s.substring(0, j + 1);
                            }
                        }
                    }
                } else {
                    result = null;
                }
            }
        }
        if (result == null) {
            actionProxy.failed(s, line);
        } else {
            actionProxy.success(result, line);
        }
        return result;
    }

    public static void main(String[] args) {
        DFA dfa = new DFA("ab");
        System.out.println(dfa.match("ab"));
    }
}

