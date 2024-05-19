package fww.regular;

import java.util.HashSet;
import java.util.Set;

public class NState {
    private static int idCounter = 0;

    private final int id;

    private StateType type = StateType.NORMAL;

    private Set<CharSet> transition = new HashSet<>();

    private NState target;

    private Set<CharSet> aheadCharSet;

    Set<NState> epsilon = new HashSet<>();

    public NState() {
        id = idCounter++;
    }

    public int getId() {
        return id;
    }

    public void setType(StateType type) {
        this.type = type;
    }

    public StateType getType() {
        return type;
    }

    public void setTransition(Set<CharSet> transition) {
        this.transition = transition;
    }

    public Set<CharSet> getTransition() {
        return transition;
    }

    public void addTransition(CharSet charSet, NState target) {
        transition.add(charSet);
        this.target = target;
    }

    public void addEpsilon(NState NState) {
        epsilon.add(NState);
    }

    public Set<NState> getEpsilon() {
        return epsilon;
    }

//    public boolean contains(char c) {
//        for(CharSet charSet : transition) {
//            if(charSet.contains(c)) {
//                return true;
//            }
//        }
//        return false;
//    }

public boolean contains(char c) {
    return transition.stream().anyMatch(charSet -> charSet.contains(c));
}


    public NState getTarget() {
        return target;
    }

    public void AHEADInit(Set<CharSet> charSets) {
        aheadCharSet = charSets;
        type = StateType.AHEAD;
    }

    public Set<CharSet> getAheadCharSet() {
        if(aheadCharSet == null) {
            return new HashSet<>();
        }
        return aheadCharSet;
    }

    @Override
    public int hashCode() {
        // 基于唯一ID计算hashCode
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NState other = (NState) obj;
        return id == other.id;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
