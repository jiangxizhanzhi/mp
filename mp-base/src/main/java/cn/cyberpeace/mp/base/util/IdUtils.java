package cn.cyberpeace.mp.base.util;

import java.util.UUID;

public class IdUtils {

    private IdUtils() {}


    public static String globalId(){
        return UUID.randomUUID().toString();
    }

}
