 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TextUtils.java.
 // File size 8399.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util.basic;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName TextUtils
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
public class TextUtils {
    private static final Pattern linePattern = Pattern.compile("_([a-z])");

    private static final Pattern humpPattern = Pattern.compile("\\B(\\p{Upper})(\\p{Lower}*)");

    private static final Pattern chinesePattern = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");

    private static final Base64.Decoder base64Decoder = Base64.getDecoder();

    private static final Base64.Encoder base64Encoder = Base64.getEncoder();

    /**
     * TextUtils
     */
    private TextUtils() {}

    /**
     * emptyNull
     *
     * @param str
     * @return
     */
    public static String emptyNull(String str) {
        return StringUtils.isEmpty(str) ? null : str;
    }

    /**
     * uuid
     *
     * @return
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", StringUtils.EMPTY).toLowerCase();
    }

    /**
     * coupleStrip
     *
     * @param str
     * @param stripChars
     * @return
     */
    public static String coupleStrip(String str, final String stripChars) {
        String result;
        int strLength = str.length();
        int stripCharsLength = stripChars.length();
        if (strLength >= stripCharsLength * 2 && str.startsWith(stripChars) && str.endsWith(stripChars)) {
            result = str.substring(stripCharsLength, strLength - stripCharsLength);
        } else {
            result = str;
        }
        return result;
    }

    /**
     * lineToHump
     *
     * @param str
     * @return
     */
    public static String lineToHump(String str) {
        if (str == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        Matcher matcher = linePattern.matcher(str);
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * lineToHump
     *
     * @param strs
     * @return
     */
    public static String[] lineToHump(String[] strs) {
        if (strs == null) {
            return new String[0];
        }

        String[] result = new String[strs.length];
        for (int i = 0; i < strs.length; i++) {
            result[i] = lineToHump(strs[i]);
        }
        return result;
    }

    /**
     * humpToLine
     *
     * @param str
     * @return
     */
    public static String humpToLine(String str) {
        if (str == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        Matcher matcher = humpPattern.matcher(str);
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * humpToLine
     *
     * @param strs
     * @return
     */
    public static String[] humpToLine(String[] strs) {
        if (strs == null) {
            return new String[0];
        }

        String[] result = new String[strs.length];
        for (int i = 0; i < strs.length; i++) {
            result[i] = humpToLine(strs[i]);
        }
        return result;
    }

}

