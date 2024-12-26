package cn.cyberpeace.mp.sceneplugincollection.plugin.azure.lifecyclehandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSceneStage;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.CreateHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.DeleteHandler;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class AzureDeleteHandler extends DeleteHandler {

    public Integer cloudType;
    private String groupNamePrefix;
    private IAzureResourceManager resourceService;

    public AzureDeleteHandler(int cloudType, CollectionScenePluginHandler pluginHandler) {
        super(pluginHandler);
        this.cloudType = cloudType;
        this.resourceService = IAzureResourceManager.getInstance(cloudType);
        this.groupNamePrefix = BeanCommon.getBean(SituationAwareConfig.class).getGroupNamePrefix();
    }

    public void delete() {
        try {
            CloudScenePluginHandler pluginHandler = getPluginHandler().getSceneHandler().getPluginHandler(PluginConstant.PLUGIN_KEY);

            List<CloudPluginSceneElementBO> terminalExcludeNpcElements = pluginHandler.getTerminalExcludeNpcElements();
            if (terminalExcludeNpcElements.size() > 0) {
                super.delete();
            }
        } catch (Exception e) {
//            云场景启动失败立即删除云资源
            String groupName = getPluginHandler().getSourceSceneId();
            groupName = StringUtils.isNotBlank(groupNamePrefix) ? groupNamePrefix + "-" + groupName : groupName;
            resourceService.getManager().resourceGroups().deleteByName(groupName);
        }
    }
}
