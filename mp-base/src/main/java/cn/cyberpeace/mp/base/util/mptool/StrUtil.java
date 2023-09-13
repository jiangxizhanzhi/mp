package cn.cyberpeace.mp.base.util.mptool;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class StrUtil extends CharSequenceUtil implements StrPool {


    /**
     * 给定字符串中的字母是否全部为大写，判断依据如下：
     * 1. 大写字母包括A-Z
     * 2. 其它非字母的Unicode符都算作大写
     * <p>
     * Params:
     * str – 被检查的字符串
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

    /**
     切分字符串，如果分隔符不存在则返回原字符串
     Params:
     str – 被切分的字符串
     separator – 分隔符
     */
    public static String[] split(String str, String separatorChar) {
        return str.split(separatorChar);
    }

    public static List<String> split(CharSequence str, char separator) {
        List<String> result = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(str.toString(), String.valueOf(separator));

        while (tokenizer.hasMoreTokens()) {
            result.add(tokenizer.nextToken());
        }

        return result;
    }

    public static boolean containsAny(CharSequence str, char... testChars) {
        if (false == isEmpty(str)) {
            int len = str.length();
            for (int i = 0; i < len; i++) {
                if (ArrayUtil.contains(testChars, str.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }

}
