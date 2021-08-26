package cn.cyberpeace.mp.flow.util;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 通用工具类
 * @Autho qiaokang
 * @Date：2021/8/25 10:08
 */
@Slf4j
public class CommonUtil {

    /**
     * 获取流量发生器执行地址
     * @param host
     * @return
     */
    public static String getHostUrl(String host) {
        return "http://" + host + "/api/v1/task";
    }


}
