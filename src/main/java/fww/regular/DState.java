package fww.regular;

import fww.regular.proxy.ActionProxy;

import java.util.*;

public class DState {
    private int[] id;

    private StateType stateType;

    private Map<CharSet, DState> transition = new HashMap<>();

    private Set<CharSet> aheadCharSet;

    private ActionProxy actionProxy;

    private int priority = 0;

    public ActionProxy getActionProxy(){
        return actionProxy;
    }

    public int getPriority() {
        return priority;
    }

    private DState(){

    }

    static public DState getDState(Set<DState> dStates){
        DState result = new DState();
        result.id = getDStatesId(dStates);
        getDStatesType(dStates, result);
        result.transition = getTransition(dStates);
        getAheadCharSet(dStates, result);
        return result;
    }

    public static int[] getDStatesId(Set<DState> dStates){
        int size = 0;
        for(DState dState : dStates){
            size += dState.getIdSize();
        }
        int[] result = new int[size];
        int i = 0;
        for(DState dState : dStates){
            for(int j = 0; j < dState.getIdSize(); j++){
                result[i++] = dState.getId()[j];
            }
        }
        return result;
    }

    static public boolean isFinal(Set<DState> dStates){
        for(DState dState : dStates){
            if(dState.isFinal()){
                return true;
            }
        }
        return false;
    }

    static public boolean isAhead(Set<DState> dStates){
        for(DState dState : dStates){
            if(dState.isAhead()){
                return true;
            }
        }
        return false;
    }

    public static void getDStatesType(Set<DState> dStates, DState d){
        if(isAhead(dStates) && isFinal(dStates)){
            d.stateType = StateType.AHEAD_FINAL;
            getActionD(dStates, d);
        } else if(isAhead(dStates)){
            d.stateType = StateType.AHEAD;
        } else if(isFinal(dStates)){
            d.stateType = StateType.FINAL;
            getActionD(dStates, d);
        } else {
            d.stateType = StateType.NORMAL;
        }
    }

    public static void getActionD(Set<DState> dStates, DState d){
        int priorityMax = 0;
        ActionProxy actionProxyMax = null;
        for(DState dState : dStates){
            if(dState.isFinal()){
                if(dState.getPriority() >= priorityMax){
                    priorityMax = dState.getPriority();
                    actionProxyMax = dState.getActionProxy();
                }
            }
        }
        d.priority = priorityMax;
        d.actionProxy = actionProxyMax;
    }

    public static Map<CharSet, DState> getTransition(Set<DState> dStates){
        DState d = dStates.iterator().next();
        return d.transition;
    }

    public static void getAheadCharSet(Set<DState> dStates, DState d){
        int priorityMax = 0;
        Set<CharSet> result = new HashSet<>();
        for(DState dState : dStates){
            if(dState.isAhead()){
//                result.addAll(dState.getAheadCharSet());
                if(dState.getPriority() > priorityMax){
                    priorityMax = d.getPriority();
                }
            }
        }
        for(DState dState : dStates){
            if(dState.isAhead()){
                if(dState.getPriority() == priorityMax){
                    result.addAll(dState.getAheadCharSet());
                }
            }
        }
        result = result.isEmpty() ? null : result;
        d.setAheadCharSet(result);
    }

    private int[] getId() {
        return id;
    }

    private int getIdSize(){
        return id.length;
    }

    private void getAction(Set<NState> nStates){
        if(!NFA.isFinal(nStates)){
            return;
        }
        int priorityMax = 0;
        ActionProxy actionProxyMax = null;
        for(NState nState : nStates){
            if(NState.isFinal(nState)){
                if(nState.getPriority() >= priorityMax){
                    priorityMax = nState.getPriority();
                    actionProxyMax = nState.getActionProxy();
                }
            }
        }
        this.actionProxy = actionProxyMax;
        this.priority = priorityMax;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public DState(Set<NState> nStates) {
        id = new int[nStates.size()];
        int i = 0;
        for(NState nState : nStates) {
            id[i++] = nState.getId();
        }
        if(NFA.isAhead(nStates) && NFA.isFinal(nStates)) {
            stateType = StateType.AHEAD_FINAL;
            getAction(nStates);
        } else if(NFA.isAhead(nStates)) {
            stateType = StateType.AHEAD;
        } else if(NFA.isFinal(nStates)) {
            stateType = StateType.FINAL;
            getAction(nStates);
        } else {
            stateType = StateType.NORMAL;
        }
        NFA.getAheadCharSet(nStates, this);
        System.out.println("--------this charset");
        System.out.println(this.aheadCharSet);
        System.out.println("--------this charset");
    }

    public void setAheadCharSet(Set<CharSet> aheadCharSet) {
        this.aheadCharSet = aheadCharSet;
    }

    public void addTransition(CharSet charSet, DState dState) {
        System.out.println("addTransition: " + charSet + " " + dState);
        transition.put(charSet, dState);
    }

    public boolean isFinal() {
        return stateType == StateType.FINAL || stateType == StateType.AHEAD_FINAL;
    }

    public boolean isAhead() {
        return stateType == StateType.AHEAD || stateType == StateType.AHEAD_FINAL;
    }

    public DState getTarge(CharSet charSet) {
        return transition.get(charSet);
    }

    public DState getTarge(char c) {
        for(CharSet charSet : transition.keySet()) {
            if(charSet.contains(c)) {
                return transition.get(charSet);
            }
        }
        return null;
    }

    public Set<CharSet> getAheadCharSet() {
        return aheadCharSet;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(id);
    }

    @Override
    public boolean equals(Object o) {
        // 如果对象引用相同，直接返回 true
        if (this == o) return true;

        // 如果传入对象为空或者类型不匹配，返回 false
        if (o == null || getClass() != o.getClass()) return false;

        // 类型匹配后，将传入对象转换为 DState
        DState dState = (DState) o;

        // 比较两个对象的 id 数组是否相等
        return Arrays.equals(id, dState.id);
    }
}
