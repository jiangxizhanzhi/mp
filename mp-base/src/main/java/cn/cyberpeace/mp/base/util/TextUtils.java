package cn.cyberpeace.mp.base.util;

import java.util.UUID;
import java.util.regex.Pattern;

public class TextUtils {
    private TextUtils() {}

    private static final Pattern linePattern = Pattern.compile("_([a-z])");

    private static final Pattern humpPattern = Pattern.compile("\\B(\\p{Upper})(\\p{Lower}*)");

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-","").toLowerCase();
    }

    public static String lineToHump(String str) {
        if (str == null) {
            return null;
        }

        var sb = new StringBuilder();
        var matcher = linePattern.matcher(str);
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String[] lineToHump(String[] strs) {
        if (strs == null) {
            return new String[0];
        }

        var result = new String[strs.length];
        for (var i = 0; i < strs.length; i++) {
            result[i] = lineToHump(strs[i]);
        }
        return result;
    }

    public static String humpToLine(String str) {
        if (str == null) {
            return null;
        }

        var sb = new StringBuilder();
        var matcher = humpPattern.matcher(str);
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String[] humpToLine(String[] strs) {
        if (strs == null) {
            return new String[0];
        }

        var result = new String[strs.length];
        for (var i = 0; i < strs.length; i++) {
            result[i] = humpToLine(strs[i]);
        }
        return result;
    }
}

