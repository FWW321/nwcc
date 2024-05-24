package fww.regular;

import fww.regular.Interface.FailedFunction;
import fww.regular.Interface.SuccessFunction;
import fww.regular.proxy.ActionProxy;

import java.util.*;
import java.util.stream.Collectors;

public class NFA {
    private int line = 1;

    private Set<CharSet> alphabet = new HashSet<>();

    private NState start;

    private NState end;

    private Set<NState> NStates = new HashSet<>();

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

    public NFA(String regex, SuccessFunction successFunction) {
        this(regex);
        this.end.InitActionProxy();
        this.end.getActionProxy().onSuccess(successFunction);
    }

    public NFA(String regex, FailedFunction failedFunction) {
        this(regex);
        this.end.InitActionProxy();
        this.end.getActionProxy().onFailed(failedFunction);
    }

    public NFA(String regex, SuccessFunction successFunction, FailedFunction failedFunction) {
        this(regex);
        this.end.InitActionProxy();
        this.end.getActionProxy().onSuccess(successFunction);
        this.end.getActionProxy().onFailed(failedFunction);
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

    //TODO:4
//    public Set<CharSet> updateAlphabet(Set<CharSet> charSets) {
//        Set<CharSet> newCharSets = new HashSet<>(alphabet);
//
//        Set<CharSet> cct = new HashSet<>();
//        for (CharSet t : charSets) {
//            for (CharSet cc : alphabet) {
//                CharSet cc1 = cc.intersection(t);
//                if (cc1 == null || cc1.isEmpty()) {
//                    continue;
//                }
//                CharSet cc2 = cc.minus(t);
//                cct.add(cc1);
//                if (!cc2.isEmpty()) {
//                    newCharSets.remove(cc);
//                    newCharSets.add(cc1);
//                    newCharSets.add(cc2);
//                    updateStates(cc, cc1, cc2);
//                }
//                t = t.minus(cc);
//                if (t.isEmpty()) {
//                    break;
//                }
//            }
//        }
//        System.out.println("cct");
//        cct.removeIf(CharSet::isEmpty);
//        System.out.println(cct);
//        newCharSets.removeIf(CharSet::isEmpty);
//        alphabet = newCharSets;
//        return cct;
//    }

    //TODO:4
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

        cct.removeIf(CharSet::isEmpty);
        newCharSets.removeIf(CharSet::isEmpty);
        alphabet = newCharSets;
        return cct;
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
    //TODO:1
//    private NFA builder(String regex) {
//        List<NFA> concat = new ArrayList<>();
//        char[] chars = regex.toCharArray();
//        for (int i = 0; i < chars.length; i++) {
//            NFA nfa = null;
//            char c = chars[i];
//            if (c == '(') {
//                int j = i + 1;
//                int count = 1;
//                while (count != 0) {
//                    if (chars[j] == '(') {
//                        count++;
//                    } else if (chars[j] == ')') {
//                        count--;
//                    }
//                    j++;
//                }
//                nfa = builder(regex.substring(i + 1, j - 1));
//                i = j - 1;
//            } else if (c == '[') {
//                int j = i + 1;
//                while (chars[j] != ']') {
//                    j++;
//                }
//                nfa = charClassRule(regex.substring(i + 1, j));
//                i = j;
//            } else if (c == '.') {
//                nfa = dotRule();
//            } else if (c == '|') {
//                NFA s = concat(concat);
//                NFA t = builder(regex.substring(i + 1));
//                nfa = orRule(s, t);
//                i = chars.length - 1;
//            } else if (c == '/') {
//                NFA s = concat(concat);
//                NFA t = builder(regex.substring(i + 1));
//                nfa = lookaheadRule(s, t);
//                i = chars.length - 1;
//            } else if (c == '*') {
//                NFA n = concat.getLast();
//                concat.removeLast();
//                nfa = starRule(n);
//            } else if (c == '+') {
//                NFA n = concat.getLast();
//                concat.removeLast();
//                nfa = plusRule(n);
//            } else if (c == '?') {
//                NFA n = concat.getLast();
//                concat.removeLast();
//                nfa = questionRule(n);
//            } else if (c == '\\') {
//                if (i < chars.length - 1) {
//                    int j = i + 1;
//                    if (chars[j] == 's') {
//                        nfa = sRule();
//                    } else {
//                        nfa = baseRule(chars[j]);
//                    }
//                    i = j;
//                }
//            } else {
//                nfa = baseRule(c);
//            }
//            concat.add(nfa);
//        }
//        return concat(concat);
//    }

    //TODO:1
    public NFA builder(String regex) {
        Stack<NFA> stack = new Stack<>();
        int i = 0;

        while (i < regex.length()) {
            char c = regex.charAt(i);
            switch (c) {
                case '(':
                    int j = findClosingParenthesis(regex, i + 1);
                    stack.push(builder(regex.substring(i + 1, j)));
                    i = j;
                    break;
                case '[':
                    int k = regex.indexOf(']', i);
                    if(regex.substring(i + 1, k).charAt(0) == '^')
                        stack.push(notCharClassRule(regex.substring(i + 2, k)));
                    else
                        stack.push(charClassRule(regex.substring(i + 1, k)));
                    i = k + 1;
                    break;
                case '|':
                    NFA left = concat(stack);
                    stack.push(orRule(left, builder(regex.substring(i + 1))));
                    i = regex.length();
                    break;
                case '/':
                    NFA lookahead = concat(stack);
                    stack.push(lookaheadRule(lookahead, builder(regex.substring(i + 1))));
                    i = regex.length();
                    break;
                case '*':
                case '+':
                case '?':
                    NFA last = stack.pop();
                    stack.push(applyQuantifier(c, last));
                    break;
                case '.':
                    stack.push(dotRule());
                    break;
                case '\\':
                    if (i < regex.length() - 1) {
                        stack.push(handleEscapeCharacter(regex.charAt(i + 1)));
                        i++;
                    }
                    break;
                default:
                    stack.push(baseRule(c));
                    break;
            }
            i++;
        }
        return concat(stack);
    }

    private int findClosingParenthesis(String regex, int start) {
        int count = 1;
        for (int i = start; i < regex.length(); i++) {
            if (regex.charAt(i) == '(') {
                count++;
            } else if (regex.charAt(i) == ')') {
                count--;
                if (count == 0) {
                    return i;
                }
            }
        }
        throw new IllegalArgumentException("Unmatched parenthesis in regex");
    }


    private NFA applyQuantifier(char quantifier, NFA nfa) {
        return switch (quantifier) {
            case '*' -> starRule(nfa);
            case '+' -> plusRule(nfa);
            case '?' -> questionRule(nfa);
            default -> throw new IllegalArgumentException("Unsupported quantifier: " + quantifier);
        };
    }

    private NFA handleEscapeCharacter(char escaped) {
        if (escaped == 's') {
            return sRule();
        }
        return baseRule(escaped);
    }


    //TODO:6
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

    private NFA notCharClassRule(String regex){
        NFA nfa = new NFA();
        NState start = new NState();
        NState end = new NState();
        end.setType(StateType.FINAL);
        CharSet charSet = new CharSet(CharSet.charGroupToArray(regex));
        CharSet U = new CharSet(true);
        charSet = U.minus(charSet);
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


    //TODO:6
//    private NFA baseRule(char c) {
//        return createSimpleNFA(new CharSet(c));
//    }
//
//    private NFA dotRule() {
//        return createSimpleNFA(new CharSet(true));
//    }
//
//    private NFA charClassRule(String regex) {
//        CharSet charSet = new CharSet(CharSet.charGroupToArray(regex));
//        return createSimpleNFA(charSet);
//    }
//
//    private NFA createSimpleNFA(CharSet charSet) {
//        NFA nfa = new NFA();
//        NState start = new NState();
//        NState end = new NState();
//        end.setType(StateType.FINAL);
//        start.addTransition(charSet, end);
//        nfa.addState(start);
//        nfa.addState(end);
//        nfa.start = start;
//        nfa.end = end;
//        return nfa;
//    }
//
//    private NFA orRule(NFA s, NFA t) {
//        return combineNFAs(s, t, true);
//    }
//
//    private NFA concatRule(NFA s, NFA t) {
//        return combineNFAs(s, t, false);
//    }
//
//    private NFA starRule(NFA n) {
//        return applyQuantifier(n, true, true);
//    }
//
//    private NFA plusRule(NFA n) {
//        return applyQuantifier(n, true, false);
//    }
//
//    private NFA questionRule(NFA n) {
//        return applyQuantifier(n, false, true);
//    }
//
//    private NFA applyQuantifier(NFA n, boolean startEpsilon, boolean endEpsilon) {
//        NFA nfa = new NFA();
//        NState start = new NState();
//        NState end = new NState();
//        end.setType(StateType.FINAL);
//
//        if (startEpsilon) {
//            start.addEpsilon(n.start);
//        }
//        if (endEpsilon) {
//            start.addEpsilon(end);
//        }
//        n.end.addEpsilon(end);
//        if (!endEpsilon) {
//            n.end.addEpsilon(n.start);
//        }
//        n.end.setType(StateType.NORMAL);
//        nfa.addState(start);
//        nfa.addState(end);
//        nfa.addAllState(n.NStates);
//        nfa.start = start;
//        nfa.end = end;
//        return nfa;
//    }
//
//    private NFA combineNFAs(NFA s, NFA t, boolean isOrRule) {
//        NFA nfa = new NFA();
//        NState start = new NState();
//        NState end = new NState();
//        end.setType(StateType.FINAL);
//
//        if (isOrRule) {
//            start.addEpsilon(s.start);
//            start.addEpsilon(t.start);
//            s.end.addEpsilon(end);
//        } else {
//            start.addEpsilon(s.start);
//            s.end.addEpsilon(t.start);
//        }
//
//        t.end.addEpsilon(end);
//        s.end.setType(StateType.NORMAL);
//        t.end.setType(StateType.NORMAL);
//
//        nfa.addState(start);
//        nfa.addState(end);
//        nfa.addAllState(s.NStates);
//        nfa.addAllState(t.NStates);
//        nfa.start = start;
//        nfa.end = end;
//        return nfa;
//    }
//
//    private NFA lookaheadRule(NFA s, NFA t) {
//        NFA nfa = new NFA();
//        NState start = new NState();
//        NState end = new NState();
//        end.setType(StateType.FINAL);
//        start.addEpsilon(s.start);
//        s.end.addEpsilon(t.start);
//        t.end.addEpsilon(end);
//        t.end.setType(StateType.NORMAL);
//        nfa.addState(start);
//        nfa.addState(end);
//        nfa.addAllState(s.NStates);
//        nfa.addAllState(t.NStates);
//        nfa.start = start;
//        nfa.end = end;
//        s.end.AHEADInit(nfa.getDirectTranslation(s.end));
//        return nfa;
//    }
//
//    private NFA sRule() {
//        return builder("( |\t|\n|\r)*");
//    }

    //TODO:5
//    public Set<NState> getNILStates(NState NState) {
//        Set<NState> nilNStates = new HashSet<>();
//        nilNStates.add(NState);
//        for (NState epsilon : NState.getEpsilon()) {
//            nilNStates.addAll(getNILStates(epsilon));
//        }
//        return nilNStates;
//    }
//
//    public Set<NState> getNILStates(Set<NState> NStates) {
//        Set<NState> nilNStates = new HashSet<>();
//        for (NState NState : NStates) {
//            nilNStates.addAll(getNILStates(NState));
//        }
//        return nilNStates;
//    }

    //TODO:5
    public Set<NState> getNILStates(NState initialNState) {
        Set<NState> nilNStates = new HashSet<>();
        Stack<NState> stack = new Stack<>();
        stack.push(initialNState);

        while (!stack.isEmpty()) {
            NState current = stack.pop();
            if (nilNStates.add(current)) {
                for (NState epsilon : current.getEpsilon()) {
                    stack.push(epsilon);
                }
            }
        }

        return nilNStates;
    }

    //TODO:5
    public Set<NState> getNILStates(Set<NState> initialNStates) {
        Set<NState> nilNStates = new HashSet<>();
        Stack<NState> stack = new Stack<>();

        for (NState nState : initialNStates) {
            if (nilNStates.add(nState)) {
                stack.push(nState);
            }
        }

        while (!stack.isEmpty()) {
            NState current = stack.pop();
            for (NState epsilon : current.getEpsilon()) {
                if (nilNStates.add(epsilon)) {
                    stack.push(epsilon);
                }
            }
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
        return NStates.stream()
                .flatMap(NState -> NState.getTransition().stream()
                        .filter(transition -> transition.equals(charSet))
                        .map(_ -> NState.getTarget()))
                .collect(Collectors.toSet());
    }


    static public boolean isFinal(Set<NState> NStates) {
        return NStates.stream()
                .anyMatch(NState -> NState.getType() == StateType.FINAL || NState.getType() == StateType.AHEAD_FINAL);
    }


    static public boolean isAhead(Set<NState> NStates) {
        return NStates.stream()
                .anyMatch(NState -> NState.getType() == StateType.AHEAD || NState.getType() == StateType.AHEAD_FINAL);
    }


    public NState getStart() {
        return start;
    }

//    public String match(String s) {
//        String result = null;
//        Set<Integer> sits = new HashSet<>();
//        Stack<Character> characterStack = new Stack<>();
//        Stack<Set<NState>> aheadStack = new Stack<>();
//        Set<NState> NStates = getNILStates(start);
//
//        for (int i = 0; i < s.length(); i++) {
//            char c = s.charAt(i);
//            if (c == '\n') {
//                line++;
//            }
//            characterStack.push(c);
//            NStates = getNILStates(move(NStates, c));
//
//            if (isAhead(NStates)) {
//                aheadStack.push(NStates);
//                sits.add(i);
//            }
//
//            if (i == s.length() - 1) {
//                if (isFinal(NStates)) {
//                    if (aheadStack.isEmpty()) {
//                        result = s;
//                    } else {
//                        int j = i + 1;
//                        Set<NState> ahead = aheadStack.pop();
//                        Set<CharSet> charSets = getAheadCharSet(ahead);
//
//                        StringBuilder sb = new StringBuilder();
//                        while (!characterStack.isEmpty()) {
//                            char ch = characterStack.pop();
//                            j--;
//                            if (contains(charSets, ch) && sits.contains(j)) {
//                                result = sb.insert(0, ch).toString();
//                            }
//                        }
//                    }
//                } else {
//                    result = null;
//                }
//            }
//        }
//
//        if (result == null) {
//            actionProxy.failed(s, line);
//        } else {
//            actionProxy.success(result, line);
//        }
//
//        return result;
//    }


    public static boolean contains(Set<CharSet> charSets, char c) {
        return charSets != null && charSets.stream().anyMatch(charSet -> charSet.contains(c));
    }


    //TODO:2
//    private Set<CharSet> getDirectTranslation(NState NState) {
//        Set<CharSet> charSets = new HashSet<>();
//        for (NState s : NStates) {
//            if (s.getTarget() == NState) {
//                charSets.addAll(s.getTransition());
//            }
//
//            if (s.getEpsilon().contains(NState)) {
//                charSets.addAll(getDirectTranslation(s));
//            }
//        }
//        return charSets;
//    }

    //TODO:2
    public Set<CharSet> getDirectTranslation(NState targetState) {
        Set<CharSet> charSets = new HashSet<>();
        Stack<NState> stack = new Stack<>();
        Set<NState> visited = new HashSet<>();

        // 初始化栈，推入初始状态
        stack.push(targetState);

        // 模拟递归的循环
        while (!stack.isEmpty()) {
            NState currentState = stack.pop();

            if (visited.contains(currentState)) {
                continue;
            }
            visited.add(currentState);

            for (NState s : NStates) {
                if (s.getTarget() == currentState) {
                    charSets.addAll(s.getTransition());
                }

                if (s.getEpsilon().contains(currentState)) {
                    stack.push(s);
                }
            }
        }

        return charSets;
    }


    //TODO:3
    static public void getAheadCharSet(Set<NState> NStates, DState d) {
        int priorityMax = 0;
        Set<CharSet> charSets = new HashSet<>();
        for (NState nState : NStates) {
            if (nState.getType() == StateType.AHEAD || nState.getType() == StateType.AHEAD_FINAL) {
                if (nState.getPriority() >= priorityMax) {
                    priorityMax = nState.getPriority();
                }
            }
        }

        for (NState nState : NStates) {
            if (nState.getType() == StateType.AHEAD || nState.getType() == StateType.AHEAD_FINAL) {
                if (nState.getPriority() == priorityMax) {
                    charSets.addAll(nState.getAheadCharSet());
                }
            }
        }
        charSets = charSets.isEmpty() ? null : charSets;
        d.setPriority(priorityMax);
        d.setAheadCharSet(charSets);
    }

    //TODO:3
//    public static Set<CharSet> getAheadCharSet(Set<NState> NStates) {
//        Set<CharSet> charSets = new HashSet<>();
//        Stack<NState> stack = new Stack<>();
//
//        // 初始化栈，推入所有初始状态
//        for (NState nState : NStates) {
//            stack.push(nState);
//        }
//
//        // 模拟递归的循环
//        while (!stack.isEmpty()) {
//            NState currentState = stack.pop();
//
//            if (currentState.getType() == StateType.AHEAD || currentState.getType() == StateType.AHEAD_FINAL) {
//                charSets.addAll(currentState.getAheadCharSet());
//            }
//        }
//
//        return charSets.isEmpty() ? null : charSets;
//    }


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
        NFA nfa = new NFA("a[^b]");
        System.out.println(nfa.match("ab"));
    }
}
