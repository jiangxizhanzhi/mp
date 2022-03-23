package cn.cyberpeace.mp.base.util.basic;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Pattern;

public class TextUtils {
    private static final Pattern linePattern = Pattern.compile("_([a-z])");

    private static final Pattern humpPattern = Pattern.compile("\\B(\\p{Upper})(\\p{Lower}*)");

    private static final Pattern chinesePattern = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");

    private static final Base64.Decoder base64Decoder = Base64.getDecoder();

    private static final Base64.Encoder base64Encoder = Base64.getEncoder();

    private TextUtils() {}

    public static String emptyNull(String str) {
        return StringUtils.isEmpty(str) ? null : str;
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", StringUtils.EMPTY).toLowerCase();
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

    public static boolean containChinese(String str) {
        return chinesePattern.matcher(str).find();
    }

    public static String encodeBase64(String str) {
        return base64Encoder.encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }

    public static String decodeBase64(String str) {
        return new String(base64Decoder.decode(str), StandardCharsets.UTF_8);
    }

}

