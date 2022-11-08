package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;

public class DeleteTaskPool extends TaskPool {

    private static class DeleteTaskPoolInstance {
        public static final DeleteTaskPool INSTANCE = new DeleteTaskPool();
    }

    private DeleteTaskPool() {
        super(CollectionSingleton.collectionAppConfig.getDeletePoolSize(),
                CollectionSingleton.collectionAppConfig.getDeleteHoldSize(), 60L);
    }

    public static DeleteTaskPool getInstance() {
        return DeleteTaskPoolInstance.INSTANCE;
    }

}
