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

    public void clearTransition() {
        transition.clear();
    }

    public Set<CharSet> getTransition() {
        return transition;
    }

    public void addTransition(CharSet charSet, State target) {
        transition.add(charSet);
        this.target = target;
    }

    public void addTransition(CharSet charSet) {
        transition.add(charSet);
    }

    public void addTransition(char c, State target) {
        transition.add(new CharSet(c));
        this.target = target;
    }

    public void addEpsilon(State state) {
        epsilon.add(state);
    }

    public void addEpsilon(Set<State> states) {
        epsilon.addAll(states);
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

    public boolean contains(CharSet charSet) {
        for(CharSet set : transition) {
            if(set.contains(charSet)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(char[] chars) {
        for(char c : chars) {
            if(contains(c)) {
                return true;
            }
        }
        return false;
    }

    public State getTarget() {
        return target;
    }

    public Set<State> getTarget(CharSet charSet) {
        Set<State> states = new HashSet<>();
        for(CharSet set : transition) {
            if(set.contains(charSet)) {
                states.add(target);
            }
        }
        return states;
    }
}
