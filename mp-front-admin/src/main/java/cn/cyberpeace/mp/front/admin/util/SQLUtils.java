package cn.cyberpeace.mp.front.admin.util;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/10/13 09:15
 */
public class SQLUtils {

    public static String escapeLike(String value) {
        if (value != null) {
            return value
                    .replaceAll("_", "\\\\_")
                    .replaceAll("%", "\\\\%");
        }
        return null;
    }


}
