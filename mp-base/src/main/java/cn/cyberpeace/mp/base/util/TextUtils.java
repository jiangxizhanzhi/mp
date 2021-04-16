package cn.cyberpeace.mp.base.util;

import java.util.UUID;

public class TextUtils {
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-","").toLowerCase();
    }
}
