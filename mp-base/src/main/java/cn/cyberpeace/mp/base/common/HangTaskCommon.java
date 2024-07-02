package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import org.springframework.stereotype.Component;

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
