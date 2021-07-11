package cn.cyberpeace.mp.cscene.service.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;

import java.util.concurrent.Executors;


public class CsceneSnapshotDeleteTaskPool extends TaskPool {

    private static class CsceneSnapshotDeleteTaskPoolInstance {
        public static final CsceneSnapshotDeleteTaskPool INSTANCE = new CsceneSnapshotDeleteTaskPool();
    }

    private CsceneSnapshotDeleteTaskPool() {
        super(Executors.newSingleThreadExecutor());
    }

    public static CsceneSnapshotDeleteTaskPool getInstance() {
        return CsceneSnapshotDeleteTaskPoolInstance.INSTANCE;
    }

}
