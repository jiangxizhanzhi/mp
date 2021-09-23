package cn.cyberpeace.mp.base.context;

import java.util.Map;

/**
 * @Description 用于快速获取上下文信息的工具类
 * @Author maojun
 * @Date 2021/8/31
 */
public final class SysContextUtils {

    public static SysContext getSysContext() {
        return SysContextHolder.get();
    }

    public static String getCallChainId() {
        return SysContextHolder.get().getCallChainId();
    }

    public static void setCallChainId() {
        SysContextHolder.get().getCallChainId();
    }

    public static void setPassThroughMap(Map<String, Object> map) {
        SysContextHolder.get().setPassThroughMap(map);
    }

    public Map<String, Object> getPassThroughMap() {
        return SysContextHolder.get().getPassThroughMap();
    }
}
