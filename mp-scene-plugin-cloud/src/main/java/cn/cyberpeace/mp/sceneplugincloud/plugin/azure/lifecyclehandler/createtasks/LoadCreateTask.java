package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler.createtasks;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.Load;

/**
 * 载荷创建任务
 */
public class LoadCreateTask implements Runnable {

    private Load load;

    private CloudPluginSceneElementBO element;

    public LoadCreateTask(
            CloudPluginSceneElementBO element) {
        this.load = new Load();
        this.element = element;
    }

    @Override
    public void run() {
        load.create(element);
    }

}
