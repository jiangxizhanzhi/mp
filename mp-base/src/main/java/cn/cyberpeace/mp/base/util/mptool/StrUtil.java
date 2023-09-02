package cn.cyberpeace.mp.base.util.mptool;

import com.google.common.base.CharMatcher;

public class StrUtil {


    /**
     * 给定字符串中的字母是否全部为大写，判断依据如下：
     * 1. 大写字母包括A-Z
     * 2. 其它非字母的Unicode符都算作大写
     *
     * @param str
     * @return
     */
    public static boolean isUpperCase(CharSequence str) {
        if (null == str) {
            return false;
        }
        final int len = str.length();
        for (int i = 0; i < len; i++) {
            if (Character.isLowerCase(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String[] split(String str, String separatorChar) {
        return str.split(separatorChar);
    }

}
