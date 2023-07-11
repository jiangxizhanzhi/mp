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
