package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler.createtasks;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.DevopsLoadSourceModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.Load;
import cn.cyberpeace.mp.sceneplugincloud.repository.DevopsLoadSourceMapper;

/**
 * 载荷创建任务
 */
public class LoadCreateTask implements Runnable {

    private Load load;

    private CloudPluginSceneElementBO element;

    private CloudScenePluginHandler pluginHandler;

    private String groupName;

    public LoadCreateTask(
            CloudScenePluginHandler pluginHandler, CloudPluginSceneElementBO element, String groupName) {
        this.pluginHandler = pluginHandler;
        DevopsLoadSourceMapper devopsLoadSourceMapper = BeanCommon.getBean(DevopsLoadSourceMapper.class);
        DevopsLoadSourceModel synchronousDataSource = devopsLoadSourceMapper.getSynchronousDataSource();
        this.load = new Load(synchronousDataSource);
        this.element = element;
        this.groupName = groupName;
    }

    @Override
    public void run() {
        load.create(pluginHandler, element, groupName);
    }

}
