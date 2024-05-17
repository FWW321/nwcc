package fww.regular;

import java.util.*;

public class DState {
    private int[] id;

    private StateType stateType;

    private Map<CharSet, DState> transition = new HashMap<>();

    private Set<CharSet> aheadCharSet;

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
        if (this == o) return true;
        if (!(o instanceof DState dState)) return false;
        return Arrays.equals(id, dState.id);
    }
}
