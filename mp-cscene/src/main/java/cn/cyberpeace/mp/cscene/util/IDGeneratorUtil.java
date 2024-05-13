package cn.cyberpeace.mp.cscene.util;

import java.util.UUID;

public class IDGeneratorUtil {

    private IDGeneratorUtil(){}

    public static String getID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
