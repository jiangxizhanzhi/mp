package cn.cyberpeace.mp.collection.context;

/**
 * @Description
 * @Author maojun
 * @Date 2022/8/19
 */
public class ContextHolder {
    private static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void setGroupId(String groupId) {
        threadLocal.set(groupId);
    }

    public static String getGroupId() {
        return threadLocal.get();
    }

    public static void clear() {
        threadLocal.remove();
    }
}
