 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DeleteTaskPool.java.
 // File size 960.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;

/**
 * @ClassName DeleteTaskPool
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TaskPool
 */
public class DeleteTaskPool extends TaskPool {

    private static class DeleteTaskPoolInstance {
        public static final DeleteTaskPool INSTANCE = new DeleteTaskPool();
    }

    /**
     * DeleteTaskPool
     */
    private DeleteTaskPool() {
        super(CollectionSingleton.collectionAppConfig.getDeletePoolSize(),
                CollectionSingleton.collectionAppConfig.getDeleteHoldSize(), 60L);
    }

    /**
     * getInstance
     *
     * @return
     */
    public static DeleteTaskPool getInstance() {
        return DeleteTaskPoolInstance.INSTANCE;
    }

}
