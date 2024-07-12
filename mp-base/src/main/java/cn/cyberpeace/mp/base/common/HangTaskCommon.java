 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HangTaskCommon.java.
 // File size 1169.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import org.springframework.stereotype.Component;

/**
 * @ClassName HangTaskCommon
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Component
public class HangTaskCommon {

    public static HangTaskCommonPool taskCommonPool;

    /**
     * HangTaskCommon
     *
     * @param taskCommonPool
     */
    private HangTaskCommon(HangTaskCommonPool taskCommonPool) {
        HangTaskCommon.taskCommonPool = taskCommonPool;
    }

    /**
     * hangTask
     *
     * @param task
     */
    public static void hangTask(Runnable task) {
        taskCommonPool.executeTask(task);
    }

    @Component
    public static class HangTaskCommonPool extends TaskPool {
        /**
         * HangTaskCommonPool
         *
         * @param baseAppConfig
         */
        public HangTaskCommonPool(BaseAppConfig baseAppConfig) {
            super(baseAppConfig.getHangTaskPoolSize(), baseAppConfig.getHangTaskPoolSize() + 1, 60L);
        }
    }
}
