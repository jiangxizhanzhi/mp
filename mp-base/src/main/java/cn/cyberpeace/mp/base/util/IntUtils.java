package cn.cyberpeace.mp.base.util;

public class IntUtils {

    private IntUtils() {}

    public static Integer zeroNull(Integer value) {
        return (value == null || value.equals(0)) ? null : value;
    }

}
