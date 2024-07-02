package cn.cyberpeace.mp.base.util.mptool;

/**
 * @ClassName CharUtil
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CharPool
 */
public class CharUtil implements CharPool {

    /**
     * isFileSeparator
     *
     * @param c
     * @return boolean
     */
    public static boolean isFileSeparator(char c) {
        return SLASH == c || BACKSLASH == c;
    }

    /**
     * isBlankChar
     *
     * @param c
     * @return boolean
     */
    public static boolean isBlankChar(char c) {
        return isBlankChar((int) c);
    }

    /**
     * isBlankChar
     *
     * @param c
     * @return boolean
     */
    public static boolean isBlankChar(int c) {
        return Character.isWhitespace(c)
                || Character.isSpaceChar(c)
                || c == '\ufeff'
                || c == '\u202a'
                || c == '\u0000';
    }
}
