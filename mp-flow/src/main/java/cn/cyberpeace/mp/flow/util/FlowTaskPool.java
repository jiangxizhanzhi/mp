package cn.cyberpeace.mp.flow.util;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;

public class FlowTaskPool extends TaskPool {
    private static class FlowTaskPoolInstance {
        public static final FlowTaskPool INSTANCE = new FlowTaskPool();
    }

    private FlowTaskPool() {
        super(2, 1000, 60L);
    }

    public static FlowTaskPool getInstance() {
        return FlowTaskPoolInstance.INSTANCE;
    }
}