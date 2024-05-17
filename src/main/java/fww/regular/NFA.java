package fww.regular;

import fww.regular.Interface.FailedFunction;
import fww.regular.Interface.SuccessFunction;
import fww.regular.proxy.ActionProxy;

import java.util.*;

public class NFA {
    private int line = 1;

    private Set<CharSet> alphabet = new HashSet<>();

    private NState start;

    private NState end;

    private Set<NState> NStates = new HashSet<>();

    private ActionProxy actionProxy = new ActionProxy();

    private NFA() {
        alphabet.add(new CharSet(true));
    }

    public NFA(String regex) {
        NFA nfa = builder(regex);
        this.alphabet = nfa.alphabet;
        this.start = nfa.start;
        this.end = nfa.end;
        this.NStates = nfa.NStates;
    }

    public Set<CharSet> getAlphabet() {
        return alphabet;
    }

    public void addState(NState NState) {
        NState.setTransition(updateAlphabet(NState.getTransition()));
        NStates.add(NState);
    }

    public void addAllState(Set<NState> nStates){
        for(NState nState : nStates){
            addState(nState);
        }
    }

    public Set<CharSet> updateAlphabet(Set<CharSet> charSets) {
        Set<CharSet> newCharSets = new HashSet<>(alphabet);

        Set<CharSet> cct = new HashSet<>();
        for (CharSet t : charSets) {
            for (CharSet cc : alphabet) {
                CharSet cc1 = cc.intersection(t);
                if (cc1 == null || cc1.isEmpty()) {
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
        System.out.println("cct");
        cct.removeIf(CharSet::isEmpty);
        System.out.println(cct);
        newCharSets.removeIf(CharSet::isEmpty);
        alphabet = newCharSets;
        return cct;
    }

    public NFA onSuccess(SuccessFunction successFunction) {
        actionProxy.onSuccess(successFunction);
        return this;
    }

    public NFA onFailed(FailedFunction failedFunction) {
        actionProxy.onFailed(failedFunction);
        return this;
    }

    public void updateStates(CharSet cc, CharSet cc1, CharSet cc2) {
        for (NState NState : NStates) {
            Set<CharSet> newTransitions = new HashSet<>();
            for (CharSet t : NState.getTransition()) {
                if (t.equals(cc)) {
                    newTransitions.add(cc1);
                    newTransitions.add(cc2);
                } else {
                    newTransitions.add(t);
                }
            }
            NState.setTransition(newTransitions);
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
        NState start = new NState();
        NState end = new NState();
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
        NState start = new NState();
        NState end = new NState();
        end.setType(StateType.FINAL);
        start.addEpsilon(s.start);
        start.addEpsilon(t.start);
        s.end.addEpsilon(end);
        t.end.addEpsilon(end);
        s.end.setType(StateType.NORMAL);
        t.end.setType(StateType.NORMAL);
        nfa.addState(start);
        nfa.addState(end);
        nfa.addAllState(s.NStates);
        nfa.addAllState(t.NStates);
        nfa.start = start;
        nfa.end = end;
        return nfa;
    }

    private NFA concatRule(NFA s, NFA t) {
        NFA nfa = new NFA();
        NState start = new NState();
        NState end = new NState();
        end.setType(StateType.FINAL);
        start.addEpsilon(s.start);
        s.end.addEpsilon(t.start);
        t.end.addEpsilon(end);
        s.end.setType(StateType.NORMAL);
        t.end.setType(StateType.NORMAL);
        nfa.addState(start);
        nfa.addState(end);
        nfa.addAllState(s.NStates);
        nfa.addAllState(t.NStates);
        nfa.start = start;
        nfa.end = end;
        return nfa;
    }

    public ActionProxy getActionProxy() {
        return actionProxy;
    }

    private NFA starRule(NFA n) {
        NFA nfa = new NFA();
        NState start = new NState();
        NState end = new NState();
        end.setType(StateType.FINAL);
        start.addEpsilon(n.start);
        start.addEpsilon(end);
        n.end.addEpsilon(n.start);
        n.end.addEpsilon(end);
        n.end.setType(StateType.NORMAL);
        nfa.addState(start);
        nfa.addState(end);
        nfa.addAllState(n.NStates);
        nfa.start = start;
        nfa.end = end;
        return nfa;
    }

    private NFA plusRule(NFA n) {
        NFA nfa = new NFA();
        NState start = new NState();
        NState end = new NState();
        end.setType(StateType.FINAL);
        start.addEpsilon(n.start);
        n.end.addEpsilon(end);
        n.end.addEpsilon(n.start);
        n.end.setType(StateType.NORMAL);
        nfa.addState(start);
        nfa.addState(end);
        nfa.addAllState(n.NStates);
        nfa.start = start;
        nfa.end = end;
        return nfa;
    }

    private NFA questionRule(NFA n) {
        NFA nfa = new NFA();
        NState start = new NState();
        NState end = new NState();
        end.setType(StateType.FINAL);
        start.addEpsilon(n.start);
        start.addEpsilon(end);
        n.end.addEpsilon(end);
        n.end.setType(StateType.NORMAL);
        nfa.addState(start);
        nfa.addState(end);
        nfa.addAllState(n.NStates);
        nfa.start = start;
        nfa.end = end;
        return nfa;
    }

    private NFA dotRule() {
        NFA nfa = new NFA();
        NState start = new NState();
        NState end = new NState();
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
        NState start = new NState();
        NState end = new NState();
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
        System.out.println("lookahead");
        NFA nfa = new NFA();
        NState start = new NState();
        NState end = new NState();
        end.setType(StateType.FINAL);
        start.addEpsilon(s.start);
        s.end.addEpsilon(t.start);
        t.end.addEpsilon(end);
        t.end.setType(StateType.NORMAL);
        nfa.addState(start);
        nfa.addState(end);
        nfa.addAllState(s.NStates);
        nfa.addAllState(t.NStates);
        nfa.start = start;
        nfa.end = end;
        s.end.AHEADInit(nfa.getDirectTranslation(s.end));
        return nfa;
    }

    private NFA sRule(){
        return builder("( |\t|\n|\r)*");
    }

    public Set<NState> getNILStates(NState NState) {
        Set<NState> nilNStates = new HashSet<>();
        nilNStates.add(NState);
        for (NState epsilon : NState.getEpsilon()) {
            nilNStates.addAll(getNILStates(epsilon));
        }
        return nilNStates;
    }

    public Set<NState> getNILStates(Set<NState> NStates) {
        Set<NState> nilNStates = new HashSet<>();
        for (NState NState : NStates) {
            nilNStates.addAll(getNILStates(NState));
        }
        return nilNStates;
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

    public Set<NState> move(Set<NState> NStates, char c) {
        Set<NState> moveNStates = new HashSet<>();
        for (NState NState : NStates) {
            if (NState.contains(c)) {
                moveNStates.add(NState.getTarget());
            }
        }
        return moveNStates;
    }

    public Set<NState> move(Set<NState> NStates, CharSet charSet) {
        Set<NState> moveNStates = new HashSet<>();
        for (NState NState : NStates) {
            for (CharSet transition : NState.getTransition()) {
                if (transition.equals(charSet)) {
                    moveNStates.add(NState.getTarget());
                }
            }
        }
        return moveNStates;
    }

    static public boolean isFinal(Set<NState> NStates) {
        for (NState NState : NStates) {
            if (NState.getType() == StateType.FINAL || NState.getType() == StateType.AHEAD_FINAL){
                return true;
            }
        }
        return false;
    }

    static public boolean isAhead(Set<NState> NStates) {
        for (NState NState : NStates) {
            if (NState.getType() == StateType.AHEAD || NState.getType() == StateType.AHEAD_FINAL){
                return true;
            }
        }
        return false;
    }

    private Set<NState> getAheads(Set<NState> NStates){
        Set<NState> aheadNStates = new HashSet<>();
        for(NState NState : NStates){
            if(NState.getType() == StateType.AHEAD || NState.getType() == StateType.AHEAD_FINAL){
                aheadNStates.add(NState);
            }
        }
        return aheadNStates;
    }

    public NState getStart() {
        return start;
    }

    public String match(String s) {
        String result = null;
        System.out.println("match");
        Set<Integer> sits = new HashSet<>();
        Stack<Character> characterStack = new Stack<>();
        Stack<Set<NState>> aheadStack = new Stack<>();
        Set<NState> NStates = getNILStates(start);
        if(isAhead(NStates)){
            aheadStack.push(NStates);
            sits.add(-1);
        }
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(chars[i] == '\n'){
                line++;
            }
            characterStack.push(chars[i]);
            NStates = getNILStates(move(NStates, chars[i]));
            if(isAhead(NStates)){
                aheadStack.push(NStates);
                sits.add(i);
            }
            if (i == chars.length - 1) {
                if (isFinal(NStates)) {
                    if(aheadStack.isEmpty()){
                        result = s;
                    }else {
                        int j = i + 1;
                        System.out.println("ahead");
                        System.out.println(aheadStack);
                        Set<NState> ahead = aheadStack.pop();
                        System.out.println(ahead);
                        Set<CharSet> charSets = getAheadCharSet(ahead);
                        System.out.println(charSets);
                        while(!characterStack.isEmpty()){
                            char c = characterStack.pop();
                            j--;
                            if(contains(charSets, c) && sits.contains(j)){
                                System.out.println("ahead match");
                                System.out.println(j);
                                result = s.substring(0, j+1);
                            }
                        }
                    }
                } else {
                    result = null;
                }
            }
        }
        if(result == null){
            actionProxy.failed(s, line);
        }else {
            actionProxy.success(result, line);
        }
        return result;
    }

    public static boolean contains(Set<CharSet> charSets, char c) {
        return charSets != null && charSets.stream().anyMatch(charSet -> charSet.contains(c));
    }


    private Set<CharSet> getDirectTranslation(NState NState) {
        Set<CharSet> charSets = new HashSet<>();
        for (NState s : NStates) {
            if (s.getTarget() == NState) {
                charSets.addAll(s.getTransition());
            }

            if (s.getEpsilon().contains(NState)) {
                charSets.addAll(getDirectTranslation(s));
            }
        }
        return charSets;
    }


    static public Set<CharSet> getAheadCharSet(Set<NState> NStates) {
        Set<CharSet> charSets = new HashSet<>();
        for (NState nState : NStates) {
            if (nState.getType() == StateType.AHEAD || nState.getType() == StateType.AHEAD_FINAL) {
                charSets.addAll(nState.getAheadCharSet());
            }
        }
        return charSets.isEmpty() ? null : charSets;
    }


    static public String match(String regex, NFA... nfas) {
        for(NFA nfa : nfas){
            String s = nfa.match(regex);
            if(s != null){
                return s;
            }
        }
        return null;
    }

    static public String match(String regex, List<NFA> nfas) {
        for(NFA nfa : nfas){
            String s = nfa.match(regex);
            if(s != null){
                return s;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "NFA{" +
                "alphabet=" + alphabet +
                ", start=" + start +
                ", end=" + end +
                ", states=" + NStates +
                '}';
    }


    public static void main(String[] args) {
        NFA nfa = new NFA("ab");
        System.out.println(nfa.match("ab"));
    }
}
