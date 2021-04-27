package cn.cyberpeace.mp.base.util;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {
    private static final Pattern linePattern = Pattern.compile("_([a-z])");

    private static final Pattern humpPattern = Pattern.compile("\\B(\\p{Upper})(\\p{Lower}*)");

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-","").toLowerCase();
    }

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

    public static String[] lineToHump(String[] strs) {
        if (strs == null) {
            return null;
        }

        String[] result = new String[strs.length];
        for (int i = 0; i < strs.length; i++) {
            result[i] = lineToHump(strs[i]);
        }
        return result;
    }

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

    public static String[] humpToLine(String[] strs) {
        if (strs == null) {
            return null;
        }

        String[] result = new String[strs.length];
        for (int i = 0; i < strs.length; i++) {
            result[i] = humpToLine(strs[i]);
        }
        return result;
    }
}

