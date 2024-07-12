 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneContextHolder.java.
 // File size 931.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.context;

/**
 * @ClassName SceneContextHolder
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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
