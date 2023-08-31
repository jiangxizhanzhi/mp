package cn.cyberpeace.mp.base.util.hutool;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.regex.Pattern;

public class NumberUtil {

    public static boolean equals(Number a, Number b) {
        return Objects.equals(a, b);
    }

    /**
     * 是否为数字，支持包括：
     * 	   1、10进制
     * 	   2、16进制数字（0x开头）
     * 	   3、科学计数法形式（1234E3）
     * 	   4、类型标识形式（123D）
     * 	   5、正负数标识形式（+123、-234）
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        // 判断是否为10进制数字
        if (Pattern.matches("^[+-]?\\d+$", str)) {
            return true;
        }

        // 判断是否为16进制数字
        if (Pattern.matches("^0[xX][\\da-fA-F]+$", str)) {
            return true;
        }

        // 判断是否为科学计数法形式
        if (Pattern.matches("^[+-]?\\d+(\\.\\d+)?([eE][+-]?\\d+)?$", str)) {
            return true;
        }

        // 判断是否为类型标识形式（字母D结尾）
        if (Pattern.matches("^[+-]?\\d+(\\.\\d+)?[dD]$", str)) {
            return true;
        }

        // 判断是否为正负数标识形式
        if (Pattern.matches("^[+-]\\d+(\\.\\d+)?$", str)) {
            return true;
        }

        return false;
    }

    public static BigDecimal round(double number, int scale) {
        BigDecimal bd = new BigDecimal(Double.toString(number));
        return bd.setScale(scale, RoundingMode.HALF_UP);
    }

}
