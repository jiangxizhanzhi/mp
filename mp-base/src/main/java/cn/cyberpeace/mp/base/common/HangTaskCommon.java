 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HangTaskCommon.java.
 // File size 809.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import org.springframework.stereotype.Component;

@Component
public class HangTaskCommon {

    public static HangTaskCommonPool taskCommonPool;

    private HangTaskCommon(HangTaskCommonPool taskCommonPool) {
        HangTaskCommon.taskCommonPool = taskCommonPool;
    }

    public static void hangTask(Runnable task) {
        taskCommonPool.executeTask(task);
    }

    @Component
    public static class HangTaskCommonPool extends TaskPool {
        public HangTaskCommonPool(BaseAppConfig baseAppConfig) {
            super(baseAppConfig.getHangTaskPoolSize(), baseAppConfig.getHangTaskPoolSize() + 1, 60L);
        }
    }
}
