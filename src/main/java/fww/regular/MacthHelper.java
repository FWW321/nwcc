package fww.regular;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class MacthHelper {
    private int line = 1;
    private DState current;
    private Set<Integer> aheadSits = new HashSet<>();
    private int finalSit = -2;
    private int charSit = -1;
    private Stack<Character> characterStack = new Stack<>();
    private Stack<DState> aheadStack = new Stack<>();
    private DState start;
    private String result = "";

    public MacthHelper(DState start) {
        this.start = start;
        this.current = start;
    }

    public void setLine(int line) {
        this.line = line;
    }



    public void setCurrent(DState current) {
        this.current = current;
    }

    public void setAheadSit(int aheadSit) {
        this.aheadSits.add(aheadSit);
    }

    public void charSitAdd() {
        charSit++;
    }

    public int getFinalSit() {
        return finalSit;
    }

    public Stack<Character> getCharacterStack() {
        return characterStack;
    }

    public Stack<DState> getAheadStack() {
        return aheadStack;
    }

    public void setFinalSit() {
        this.finalSit = charSit;
    }

    public void pushCharacter(char c) {
        characterStack.push(c);
    }

    public void pushAhead(DState dState) {
        aheadStack.push(dState);
    }

    public void addAheadSits() {
        this.aheadSits.add(charSit);
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

    public void clear(){
        current = start;
        aheadSits.clear();
        finalSit = -2;
        charSit = -1;
        characterStack.clear();
        aheadStack.clear();
        aheadSits.clear();
        result= "";
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Set<Integer> getAheadSits() {
        return aheadSits;
    }

    public String getResult() {
        return result;
    }
}
