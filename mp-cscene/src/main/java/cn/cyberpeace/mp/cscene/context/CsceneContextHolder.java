 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneContextHolder.java.
 // File size 1626.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.context;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName CsceneContextHolder
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
public class CsceneContextHolder {

    private static final ThreadLocal<CsceneContext> contextHolder = new ThreadLocal<>();

    /**
     * CsceneContextHolder
     */
    private CsceneContextHolder() {}

    /**
     * resetContext
     */
    public static void resetContext() {
        contextHolder.remove();
    }

    /**
     * getContext
     *
     * @return
     */
    public static CsceneContext getContext() {
        CsceneContext context = contextHolder.get();
        if (context == null) {
            context = new CsceneContext();
            contextHolder.set(context);
        }
        return context;
    }

    /**
     * getCallAction
     *
     * @return
     */
    public static String getCallAction() {
        return getContext().getCallAction();
    }

    /**
     * setCallAction
     *
     * @param action
     */
    public static void setCallAction(String action) {
        getContext().setCallAction(action);
    }

    /**
     * getCsceneInstanceId
     *
     * @return
     */
    public static String getCsceneInstanceId() {
        return getContext().getCsceneInstanceId();
    }

    /**
     * setCsceneInstanceId
     *
     * @param csceneInstanceId
     */
    public static void setCsceneInstanceId(String csceneInstanceId) {
        getContext().setCsceneInstanceId(csceneInstanceId);
    }

}
