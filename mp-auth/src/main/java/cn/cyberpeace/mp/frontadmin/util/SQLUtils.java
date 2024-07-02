package cn.cyberpeace.mp.frontadmin.util;

/**
 * @ClassName SQLUtils
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/10/13 09:15
 * @Version 1.0.0
 */
public class SQLUtils {

    /**
     * escapeLike
     *
     * @param value
     * @return
     */
    public static String escapeLike(String value) {
        if (value != null) {
            return value
                    .replaceAll("_", "\\\\_")
                    .replaceAll("%", "\\\\%");
        }
        return null;
    }


}
