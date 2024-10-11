package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler.createtasks;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.bo.AlertRuleBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureVirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;

import java.util.List;

/**
 * Azure 虚拟机创建任务
 */
public class AzureVirtualMachineCreateTask implements Runnable {

    private AzureVirtualMachine azureVirtualMachine;

    private CloudScenePluginHandler pluginHandler;

    private CloudPluginSceneElementBO element;

    private List<VirtualNic> virtualNics;

    private String userData;

    private List<AlertRuleBO> alertRuleBOList;

    private String dataCollectionRulesId;

    private String groupName;

    public AzureVirtualMachineCreateTask(IAzureResourceManager resourceService,
                                         CloudScenePluginHandler pluginHandler,
                                         CloudPluginSceneElementBO element,
                                         List<VirtualNic> virtualNics,
                                         String userData,
                                         List<AlertRuleBO> alertRuleBOList,
                                         String dataCollectionRulesId,
                                         String groupName) {
        this.azureVirtualMachine = new AzureVirtualMachine(resourceService);
        this.pluginHandler = pluginHandler;
        this.element = element;
        this.virtualNics = virtualNics;
        this.userData = TextUtils.emptyNull(userData);
        this.alertRuleBOList = alertRuleBOList;
        this.dataCollectionRulesId = dataCollectionRulesId;
        this.groupName = groupName;
    }

    @Override
    public void run() {
        this.azureVirtualMachine.create(this.pluginHandler, element, virtualNics, userData, alertRuleBOList, dataCollectionRulesId, groupName);
    }

}
