package cn.cyberpeace.mp.collection.context;

import lombok.Data;

/**
 * @Description
 * @Author maojun
 * @Date 2022/8/19
 */
public class ContextHolder {
    private static ThreadLocal<ContextBo> threadLocal = new ThreadLocal<>();

    public static void setContext(ContextBo context) {
        threadLocal.set(context);
    }

    public static ContextBo getContext() {
        return threadLocal.get();
    }

    public static void clear() {
        threadLocal.remove();
    }

    @Data
    public static class ContextBo {
        private String groupId;
    }
}
