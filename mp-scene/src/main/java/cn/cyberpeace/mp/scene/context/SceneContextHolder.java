package cn.cyberpeace.mp.scene.context;

public class SceneContextHolder {

    private static final ThreadLocal<SceneContext> contextHolder = new ThreadLocal<>();

    private SceneContextHolder() {}

    public static void resetContext() {
        contextHolder.remove();
    }

    public static SceneContext getContext() {
        SceneContext context = contextHolder.get();
        if (context == null) {
            context = new SceneContext();
            contextHolder.set(context);
        }
        return context;
    }



    public static String getCsceneInstanceId() {
        return getContext().getCsceneInstanceId();
    }

    public static void setCsceneInstanceId(String csceneInstanceId) {
        getContext().setCsceneInstanceId(csceneInstanceId);
    }
}
