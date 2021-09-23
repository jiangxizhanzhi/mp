package cn.cyberpeace.mp.base.context;

/**
 * @Description 用于快速获取上下文信息的工具类
 * @Author maojun
 * @Date 2021/8/31
 */
public final class SysContextUtils {

    public static SysContext getSysContext() {
        return SysContextHolder.get();
    }

    public static String getExecuteId() {
        return SysContextHolder.get().getExecuteId();
    }

    public static void put(String key, Object value) {
        SysContextHolder.get().put(key, value);
    }

    public static Object get(String key) {
        return SysContextHolder.get().get(key);
    }
}
