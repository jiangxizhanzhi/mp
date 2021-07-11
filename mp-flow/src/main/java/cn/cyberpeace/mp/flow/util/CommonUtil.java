package cn.cyberpeace.mp.flow.util;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class CommonUtil {

    
    public static String getHostUrl(String host) {
        return "http://" + host + "/api/v1/task";
    }


    
    public static String getResourcetUrl(String host) {
        return "http://" + host + "/api/v1/resource";
    }


}
