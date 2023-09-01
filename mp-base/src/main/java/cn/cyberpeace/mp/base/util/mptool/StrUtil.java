package cn.cyberpeace.mp.base.util.mptool;

import com.google.common.base.CharMatcher;

public class StrUtil {


    /**
     * 给定字符串中的字母是否全部为大写
     *
     * @param fieldName
     * @return
     */
    public static boolean isUpperCase(String fieldName) {
        return CharMatcher.inRange('A', 'Z').matchesAllOf(fieldName);
    }

    public static String[] split(String str, String separatorChar) {
        return str.split(separatorChar);
    }

}
