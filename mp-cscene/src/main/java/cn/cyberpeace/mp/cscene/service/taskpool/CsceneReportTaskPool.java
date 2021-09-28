package cn.cyberpeace.mp.cscene.service.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;

import java.util.concurrent.Executors;

public class CsceneReportTaskPool extends TaskPool {

    private static class CsceneReportTaskPoolInstance {
        public static final CsceneReportTaskPool INSTANCE = new CsceneReportTaskPool();
    }

    private CsceneReportTaskPool() {
        super(Executors.newCachedThreadPool());
    }

    public static CsceneReportTaskPool getInstance() {
        return CsceneReportTaskPoolInstance.INSTANCE;
    }

}
