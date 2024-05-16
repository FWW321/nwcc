package fww.regular;

import java.util.HashSet;
import java.util.Set;

public class State {
    private StateType type = StateType.NORMAL;

    private Set<CharSet> transition = new HashSet<>();

    private State target;

    Set<State> epsilon = new HashSet<>();

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

    public void addTransition(CharSet charSet, State target) {
        transition.add(charSet);
        this.target = target;
    }

    public void addEpsilon(State state) {
        epsilon.add(state);
    }

    public Set<State> getEpsilon() {
        return epsilon;
    }

    public boolean contains(char c) {
        for(CharSet charSet : transition) {
            if(charSet.contains(c)) {
                return true;
            }
        }
        return false;
    }

    public State getTarget() {
        return target;
    }
}
