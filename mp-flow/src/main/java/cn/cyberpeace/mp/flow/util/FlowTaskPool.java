 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowTaskPool.java.
 // File size 552.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.util;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;

/**
 * @Description: 流量任务线程池
 * @Author qiaokang
 * @Date：2023/7/11 16:19
 */
public class FlowTaskPool extends TaskPool {
    private static class FlowTaskPoolInstance {
        public static final FlowTaskPool INSTANCE = new FlowTaskPool();
    }

    private FlowTaskPool() {
        super(5, 5000, 60L);
    }

    public static FlowTaskPool getInstance() {
        return FlowTaskPoolInstance.INSTANCE;
    }
}
