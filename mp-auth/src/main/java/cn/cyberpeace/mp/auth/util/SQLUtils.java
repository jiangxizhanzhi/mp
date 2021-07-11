package cn.cyberpeace.mp.auth.util;


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
