package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler.createtasks;

import cn.cyberpeace.mp.scene.bo.AlertRuleBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureContainer;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;

import java.util.List;

/**
 * Azure 容器创建任务
 */
public class AzureContainerCreateTask implements Runnable {

    private AzureContainer azureContainer;

    private CloudScenePluginHandler pluginHandler;

    private CloudPluginSceneElementBO element;

    private List<VirtualNic> virtualNics;

    private List<AlertRuleBO> alertRuleBOList;

    private String dataCollectionRulesId;


    public AzureContainerCreateTask(IAzureResourceManager resourceService,
                                    CloudScenePluginHandler pluginHandler,
                                    CloudPluginSceneElementBO element,
                                    List<VirtualNic> virtualNics,
                                    List<AlertRuleBO> alertRuleBOList,
                                    String dataCollectionRulesId) {
        this.azureContainer = new AzureContainer(resourceService);
        this.pluginHandler = pluginHandler;
        this.element = element;
        this.virtualNics = virtualNics;
        this.alertRuleBOList = alertRuleBOList;
        this.dataCollectionRulesId = dataCollectionRulesId;
    }

    @Override
    public void run() {
        azureContainer.create(this.pluginHandler, element, virtualNics, alertRuleBOList, dataCollectionRulesId);
    }

}
