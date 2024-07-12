 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name Validator.java.
 // File size 1072.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util.mptool;

import java.util.regex.Pattern;

/**
 * @ClassName Validator
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class Validator {

    public final static Pattern IPV4 = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");


    /**
     * isMatchRegex
     *
     * @param pattern
     * @param input
     * @return boolean
     */
    public static boolean isMatchRegex(Pattern pattern, CharSequence input) {
        if (input == null || pattern == null) {
            // 提供null的字符串为不匹配
            return false;
        }
        return pattern.matcher(input).matches();
    }


    /**
     * isIpv4
     *
     * @param input
     * @return boolean
     */
    public static boolean isIpv4(CharSequence input) {
        return IPV4.matcher(input).matches();
    }
}
