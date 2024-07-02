package cn.cyberpeace.mp.cscene.service.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;

import java.util.concurrent.Executors;

/**
 * @ClassName CsceneSnapshotDeleteTaskPool
 * @Description 删除场景模板后删除场景快照
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TaskPool
 */
public class CsceneSnapshotDeleteTaskPool extends TaskPool {

    private static class CsceneSnapshotDeleteTaskPoolInstance {
        public static final CsceneSnapshotDeleteTaskPool INSTANCE = new CsceneSnapshotDeleteTaskPool();
    }

    /**
     * CsceneSnapshotDeleteTaskPool
     */
    private CsceneSnapshotDeleteTaskPool() {
        super(Executors.newSingleThreadExecutor());
    }

    /**
     * getInstance
     *
     * @return 删除场景模板后删除场景快照
     */
    public static CsceneSnapshotDeleteTaskPool getInstance() {
        return CsceneSnapshotDeleteTaskPoolInstance.INSTANCE;
    }

}
