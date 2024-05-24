package fww.regular;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class MacthHelper {
    private int line = 1;
    private DState current;
    private Set<Integer> aheadSits = new HashSet<>();
    private int finalSit = -2;
    private Stack<Character> characterStack = new Stack<>();
    private Stack<DState> aheadStack = new Stack<>();

    public void setLine(int line) {
        this.line = line;
    }

    public void setCurrent(DState current) {
        this.current = current;
    }

    public void setAheadSit(int aheadSit) {
        this.aheadSits.add(aheadSit);
    }

    public void setFinalSit(int finalSit) {
        this.finalSit = finalSit;
    }

    public void pushCharacter(char c) {
        characterStack.push(c);
    }

    public void pushAhead(DState dState) {
        aheadStack.push(dState);
    }

    public Character popCharacter() {
        return characterStack.pop();
    }

    public DState popAhead() {
        return aheadStack.pop();
    }

    public int getLine() {
        return line;
    }

    public DState getCurrent() {
        return current;
    }

    public boolean containsAheadSit(int aheadSit) {
        return aheadSits.contains(aheadSit);
    }
}
