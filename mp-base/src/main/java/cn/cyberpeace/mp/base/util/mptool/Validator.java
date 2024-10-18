package cn.cyberpeace.mp.base.util.mptool;

import java.util.regex.Pattern;

public class Validator {

    public final static Pattern IPV4 = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");


    public static boolean isMatchRegex(Pattern pattern, CharSequence input) {
        if (input == null || pattern == null) {
            // 提供null的字符串为不匹配
            return false;
        }
        return pattern.matcher(input).matches();
    }


    public static boolean isIpv4(CharSequence input) {
        return IPV4.matcher(input).matches();
    }
}
