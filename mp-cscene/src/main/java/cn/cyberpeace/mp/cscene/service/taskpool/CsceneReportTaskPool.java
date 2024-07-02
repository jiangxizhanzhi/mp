package cn.cyberpeace.mp.cscene.service.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;

import java.util.concurrent.Executors;

/**
 * @ClassName CsceneReportTaskPool
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TaskPool
 */
public class CsceneReportTaskPool extends TaskPool {

    private static class CsceneReportTaskPoolInstance {
        public static final CsceneReportTaskPool INSTANCE = new CsceneReportTaskPool();
    }

    /**
     * CsceneReportTaskPool
     */
    private CsceneReportTaskPool() {
        super(Executors.newCachedThreadPool());
    }

    /**
     * getInstance
     *
     * @return
     */
    public static CsceneReportTaskPool getInstance() {
        return CsceneReportTaskPoolInstance.INSTANCE;
    }

}
