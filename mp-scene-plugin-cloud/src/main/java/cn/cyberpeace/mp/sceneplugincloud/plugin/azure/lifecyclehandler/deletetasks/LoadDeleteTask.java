package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler.deletetasks;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.DevopsLoadSourceModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.Load;
import cn.cyberpeace.mp.sceneplugincloud.repository.DevopsLoadSourceMapper;

/**
 * 载荷删除任务
 */
public class LoadDeleteTask implements Runnable {

    private Load load;

    private CloudPluginSceneElementBO element;

    public LoadDeleteTask(
            CloudPluginSceneElementBO element) {
        DevopsLoadSourceMapper devopsLoadSourceMapper = BeanCommon.getBean(DevopsLoadSourceMapper.class);
        DevopsLoadSourceModel synchronousDataSource = devopsLoadSourceMapper.getSynchronousDataSource();
        this.load = new Load(synchronousDataSource);
        this.element = element;
    }

    @Override
    public void run() {
        load.delete(element);
    }

}
