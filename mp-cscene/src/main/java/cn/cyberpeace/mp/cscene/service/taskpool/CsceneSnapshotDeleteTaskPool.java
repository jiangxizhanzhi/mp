package cn.cyberpeace.mp.cscene.service.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;

import java.util.concurrent.Executors;

/**
 * 删除场景模板后删除场景快照
 */
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
