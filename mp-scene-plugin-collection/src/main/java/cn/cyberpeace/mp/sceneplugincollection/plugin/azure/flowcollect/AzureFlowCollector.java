package cn.cyberpeace.mp.sceneplugincollection.plugin.azure.flowcollect;

import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureContainer;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.flowcollect.FlowCollector;
import com.azure.resourcemanager.containerinstance.models.ContainerGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class AzureFlowCollector extends FlowCollector {


    /**
     * 索引前缀
     */
    private static String indexPrefix = "mp_";

    private static String ARKIMENETWORKNAME = "oj_ext";

    private IAzureResourceManager resourceService;

    private CollectionScenePluginHandler pluginHandler;

    private String netWorkId;

    public AzureFlowCollector(FlowCollector flowCollector, IAzureResourceManager resourceService, CollectionScenePluginHandler pluginHandler, String netWorkId) {
        super(flowCollector.getNetworkTrafficGroupService(), flowCollector.getNetworkTrafficService());
        this.resourceService = resourceService;
        this.pluginHandler = pluginHandler;
        this.netWorkId = netWorkId;
    }

    public String createCollector() {
        String containerGroupName = "mp-" + System.currentTimeMillis();

//        创建akime容器
        String groupName = pluginHandler.getSourceSceneId();
        String imageName = "samples/arkime:1.0.0";

        //设置环境变量
        //获取环境变量
        HashMap<String, String> environment = super.getArkimeEnvironment(containerGroupName);

        ContainerGroup container = new AzureContainer(resourceService)
                .creatContainer(false, imageName, null, netWorkId, "containerSub", groupName,
                        "arkime", "arkime", environment,
                        4, 4);

        return container.id();
    }

}
