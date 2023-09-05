package cn.cyberpeace.mp.base.util.mptool;

public class CharUtil implements CharPool {

    public static boolean isFileSeparator(char c) {
        return SLASH == c || BACKSLASH == c;
    }

    public static boolean isBlankChar(char c) {
        return isBlankChar((int) c);
    }

    public static boolean isBlankChar(int c) {
        return Character.isWhitespace(c)
                || Character.isSpaceChar(c)
                || c == '\ufeff'
                || c == '\u202a'
                || c == '\u0000';
    }
}
