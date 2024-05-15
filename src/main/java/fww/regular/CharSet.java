package fww.regular;

import java.util.HashSet;
import java.util.Set;

public class CharSet {
    private final Set<Character> chars = new HashSet<>();

    public CharSet() {
        for (int i = 0; i < 128; i++) {
            chars.add((char) i);
        }
    }

    public CharSet(char[] chars) {
        for (char aChar : chars) {
            this.chars.add(aChar);
        }
    }

    public CharSet(char c) {
        chars.add(c);
    }

    public CharSet(char c1, char c2) {
        if(c1 > c2) {
            char temp = c1;
            c1 = c2;
            c2 = temp;
        }

        for (char i = c1; i <= c2; i++) {
            chars.add(i);
        }
    }


    public boolean contains(char c) {
        for (char aChar : chars) {
            if (aChar == c) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(CharSet charSet) {
        for (char aChar : charSet.chars) {
            if (!contains(aChar)) {
                return false;
            }
        }
        return true;
    }

    public void addChar(char c) {
        chars.add(c);
    }

    public void addCharSet(CharSet charSet) {
        chars.addAll(charSet.chars);
    }

    public void removeChar(char c) {
        chars.remove(c);
    }

    public void removeCharSet(CharSet charSet) {
        chars.removeAll(charSet.chars);
    }

    //并集
    public CharSet union(CharSet charSet) {
        if(charSet == null) {
            return this;
        }
        CharSet result = new CharSet();
        result.clear();
        result.chars.addAll(this.chars);
        result.chars.addAll(charSet.chars);
        return result;
    }

    //交集
    public CharSet intersection(CharSet charSet) {
        CharSet result = new CharSet();
        result.clear();
        for (char aChar : chars) {
            if (charSet.contains(aChar)) {
                result.addChar(aChar);
            }
        }
        return result;
    }

    //差集
    public CharSet minus(CharSet charSet) {
        CharSet result = new CharSet();
        result.clear();
        for (char aChar : chars) {
            if (!charSet.contains(aChar)) {
                result.addChar(aChar);
            }
        }
        return result;
    }

    public void clear() {
        chars.clear();
    }

    //补集
    public CharSet complement() {
        CharSet result = new CharSet();
        result.clear();
        for (int i = 0; i < 128; i++) {
            char c = (char) i;
            if (!contains(c)) {
                result.addChar(c);
            }
        }
        return result;
    }

    public int size() {
        return chars.size();
    }

    public boolean isEmpty() {
        return chars.isEmpty();
    }

    @Override
    public int hashCode() {
        return chars.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CharSet charSet = (CharSet) obj;
        return chars.equals(charSet.chars);
    }

    @Override
    public String toString() {
        return chars.toString();
    }
}
