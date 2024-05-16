package fww.regular;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CharSet {
    private final Set<Character> chars = new HashSet<>();

    public CharSet(boolean full) {
        if (full) {
            for (int i = 0; i < 128; i++) {
                chars.add((char) i);
            }
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

    //并集
    public CharSet union(CharSet charSet) {
        if (charSet == null || charSet.isEmpty()) {
            return this;
        }
        CharSet result = new CharSet(false);
        result.chars.addAll(this.chars);
        result.chars.addAll(charSet.chars);
        return result;
    }

    //交集
    public CharSet intersection(CharSet charSet) {
        CharSet result = new CharSet(false);
        for (char aChar : chars) {
            if (charSet.contains(aChar)) {
                result.addChar(aChar);
            }
        }
        return result;
    }

    //差集
    public CharSet minus(CharSet charSet) {
        CharSet result = new CharSet(false);
        for (char aChar : chars) {
            if (!charSet.contains(aChar)) {
                result.addChar(aChar);
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

    public static char[] charGroupToArray(String charGroupContent) {
        List<Character> charList = new ArrayList<>();

        for (int i = 0; i < charGroupContent.length(); i++) {
            char ch = charGroupContent.charAt(i);
            // 如果当前字符后面跟着'-'，则表示是一个字符范围
            if (i + 2 < charGroupContent.length() && charGroupContent.charAt(i + 1) == '-') {
                // 获取范围的起始和结束字符
                char end = charGroupContent.charAt(i + 2);
                // 将范围内的所有字符添加到列表中
                for (char c = ch; c <= end; c++) {
                    charList.add(c);
                }
                // 跳过范围的结束字符和'-'符号
                i += 2;
            } else {
                // 如果不是范围，直接添加字符
                charList.add(ch);
            }
        }

        // 将字符列表转换为字符数组
        char[] charArray = new char[charList.size()];
        for (int i = 0; i < charList.size(); i++) {
            charArray[i] = charList.get(i);
        }

        return charArray;
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
