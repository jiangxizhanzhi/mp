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
