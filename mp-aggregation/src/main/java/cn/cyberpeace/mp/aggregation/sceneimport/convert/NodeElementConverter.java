package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NetElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NodeElementParseResult;
import cn.cyberpeace.mp.base.util.TextUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementPortBO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description 将解析出的node节点转成BO
 * @Author maojun
 * @Date 2021/10/12
 */
class NodeElementConverter implements IConverter {
    private List<NodeElementParseResult> parseResult;

    public NodeElementConverter(List<NodeElementParseResult> parseResult) {
        this.parseResult = parseResult;
    }

    public void convert2BO(ConvertDataContainer container) {
        if (CollectionUtils.isNotEmpty(this.parseResult)) {
            this.parseResult.stream().forEach(node -> this.createElementAndPort(node, container));
        }
    }

    private void createElementAndPort(NodeElementParseResult node, ConvertDataContainer container) {
        // 主表
        SceneConfigElementBO sceneConfigElementBO = new SceneConfigElementBO();
        container.addNodeElement(sceneConfigElementBO);

        sceneConfigElementBO.setId(node.getId());
        sceneConfigElementBO.setName(node.getName());
        if (CollectionUtils.isNotEmpty(node.getInitCommands())) {
            List<String> initCommands = node.getInitCommands().stream()
                    .map(command -> command.getO())
                    .collect(Collectors.toList());
            sceneConfigElementBO.setInitCommands(initCommands);
        }
        if (node.getId().startsWith("server")) {
            sceneConfigElementBO.setType(BaseSceneElementModel.Type.TERMINAL);
        } else {
            sceneConfigElementBO.setType(BaseSceneElementModel.Type.ROUTER);
        }
        List<SceneConfigElementPortBO> portBOS = new ArrayList<>();
        sceneConfigElementBO.setPorts(portBOS);

        // 插件表
        CloudPluginSceneConfigElementBO pluginElement = new CloudPluginSceneConfigElementBO();
        pluginElement.setImage(node.getImage());
        container.putPluginElement(sceneConfigElementBO.getId(), pluginElement);

        if (CollectionUtils.isEmpty(node.getNet())) {
            // 节点未连接任何网络
            container.addNoNetworkNodeElements(sceneConfigElementBO);
        } else {
            for (NetElementParseResult ymlNetParseResult : node.getNet()) {
                SceneConfigElementPortBO portBO = new SceneConfigElementPortBO();
                portBO.setIp(ymlNetParseResult.getIp());
                portBO.setId(TextUtils.uuid());
                portBOS.add(portBO);
                container.putNode2NetworkPorts(node.getId(), ymlNetParseResult.getId(), portBO);

                CloudPluginSceneConfigElementPortBO pluginPort = new CloudPluginSceneConfigElementPortBO();
                container.putPluginElementPort(portBO.getId(), pluginPort);
            }
        }
    }
}
