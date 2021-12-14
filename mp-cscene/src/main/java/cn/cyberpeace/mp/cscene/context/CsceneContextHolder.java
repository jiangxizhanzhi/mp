package cn.cyberpeace.mp.cscene.context;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CsceneContextHolder {

    private static final ThreadLocal<CsceneContext> contextHolder = new ThreadLocal<>();

    private CsceneContextHolder() {}

    public static void resetContext() {
        contextHolder.remove();
    }

    public static CsceneContext getContext() {
        var context = contextHolder.get();
        if (context == null) {
            context = new CsceneContext();
            contextHolder.set(context);
        }
        return context;
    }

    public static String getCallAction() {
        return getContext().getCallAction();
    }

    public static void setCallAction(String action) {
        getContext().setCallAction(action);
    }

    public static String getCsceneInstanceId() {
        return getContext().getCsceneInstanceId();
    }

    public static void setCsceneInstanceId(String csceneInstanceId) {
        getContext().setCsceneInstanceId(csceneInstanceId);
    }

}
