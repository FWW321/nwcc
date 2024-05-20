package fww.regular;

import fww.regular.Interface.FailedFunction;
import fww.regular.Interface.SuccessFunction;
import fww.regular.proxy.ActionProxy;

import java.util.*;

public class DState {
    private int[] id;

    private StateType stateType;

    private Map<CharSet, DState> transition = new HashMap<>();

    private Set<CharSet> aheadCharSet;

    private ActionProxy actionProxy;

    public void InitActionProxy(){
        actionProxy = new ActionProxy();
    }

    public ActionProxy getActionProxy(){
        return actionProxy;
    }

    public DState onSuccess(SuccessFunction successFunction) {
        actionProxy.onSuccess(successFunction);
        return this;
    }

    public DState onFailed(FailedFunction failedFunction) {
        actionProxy.onFailed(failedFunction);
        return this;
    }

    private DState(){

    }

    static public DState getDState(Set<DState> dStates){
        DState result = new DState();
        result.id = getDStatesId(dStates);
        result.stateType = getDStatesType(dStates);
        result.transition = getTransition(dStates);
        result.aheadCharSet = getAheadCharSet(dStates);
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

    public static StateType getDStatesType(Set<DState> dStates){
        if(isAhead(dStates) && isFinal(dStates)){
            return StateType.AHEAD_FINAL;
        } else if(isAhead(dStates)){
            return StateType.AHEAD;
        } else if(isFinal(dStates)){
            return StateType.FINAL;
        } else {
            return StateType.NORMAL;
        }
    }

    public static Map<CharSet, DState> getTransition(Set<DState> dStates){
        DState d = dStates.iterator().next();
        return d.transition;
    }

    public static Set<CharSet> getAheadCharSet(Set<DState> dStates){
        Set<CharSet> result = new HashSet<>();
        for(DState dState : dStates){
            if(dState.isAhead()){
                result.addAll(dState.getAheadCharSet());
            }
        }
        if(result.isEmpty()){
            return null;
        }
        return result;
    }

    private int[] getId() {
        return id;
    }

    private int getIdSize(){
        return id.length;
    }

    public DState(Set<NState> nStates) {
        id = new int[nStates.size()];
        int i = 0;
        for(NState nState : nStates) {
            id[i++] = nState.getId();
        }
        if(NFA.isAhead(nStates) && NFA.isFinal(nStates)) {
            stateType = StateType.AHEAD_FINAL;
        } else if(NFA.isAhead(nStates)) {
            stateType = StateType.AHEAD;
        } else if(NFA.isFinal(nStates)) {
            stateType = StateType.FINAL;
        } else {
            stateType = StateType.NORMAL;
        }
        aheadCharSet = NFA.getAheadCharSet(nStates);
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
