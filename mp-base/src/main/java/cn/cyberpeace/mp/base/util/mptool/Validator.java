package cn.cyberpeace.mp.base.util.mptool;

import com.google.common.base.CharMatcher;

import java.util.regex.Pattern;

public class Validator {

    public static boolean isMatchRegex(Pattern pattern, String input) {
        return pattern.matcher(input).matches();
    }

    public static boolean isMatchRegex(Pattern pattern, CharSequence input) {
        return pattern.matcher(input).matches();
    }


    public static boolean isIpv4(CharSequence input) {
        String[] parts = CharMatcher.anyOf("0123456789.").retainFrom(input).split("\\.");
        if (parts.length != 4) {
            return false;
        }
        for (String part : parts) {
            try {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }
}
