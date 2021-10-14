package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.base.util.JsonUtils;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortConnectionBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigTopologyBO;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementPortBO;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/12
 */
@Getter
class ConvertDataContainer {
    private static Logger logger = LoggerFactory.getLogger(ConvertDataContainer.class);

    private static String CLOUD_PLUGIN_KEY = cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY;

    // 存主表信息
    private SceneConfigBO sceneConfigBO;
    private List<SceneConfigElementBO> networkElements = new ArrayList<>();
    private List<SceneConfigElementBO> nodeElements = new ArrayList<>();

    // 存云平台插件信息
    private CloudPluginSceneConfigBO cloudPluginConfig;
    private Map<String, CloudPluginSceneConfigElementBO> cloudPluginElements = new HashMap<>();
    private Map<String, CloudPluginSceneConfigElementPortBO> cloudPluginPorts = new HashMap<>();

    // 用于连线
    private Map<String, Map<String, SceneConfigElementPortBO>> node2NetworkPorts = new HashMap<>();
    private List<SceneConfigElementPortConnectionBO> connections = new ArrayList<>();

    public void addNetworkElement(SceneConfigElementBO element) {
        this.networkElements.add(element);
    }

    public void addNodeElement(SceneConfigElementBO element) {
        this.nodeElements.add(element);
    }

    public void putNode2NetworkPorts(String nodeId, String networkId, SceneConfigElementPortBO sceneConfigElementPortBO) {
        if (!this.node2NetworkPorts.containsKey(nodeId)) {
            this.node2NetworkPorts.put(nodeId, new HashMap<>());
        }
        this.node2NetworkPorts.get(nodeId).put(networkId, sceneConfigElementPortBO);
    }

    public void addConnection(SceneConfigElementPortConnectionBO connection) {
        this.connections.add(connection);
    }

    public void putPluginElement(String elementId, BasePluginSceneElementBO pluginElement) {
        if (pluginElement instanceof CloudPluginSceneConfigElementBO) {
            this.cloudPluginElements.put(elementId, (CloudPluginSceneConfigElementBO) pluginElement);
        }
    }

    public void putPluginElementPort(String portId, BasePluginSceneElementPortBO pluginPort) {
        if (pluginPort instanceof CloudPluginSceneConfigElementPortBO) {
            this.cloudPluginPorts.put(portId, (CloudPluginSceneConfigElementPortBO) pluginPort);
        }
    }

    public CsceneBO toCsceneBO() {
        SceneConfigDataBO configDataBO = new SceneConfigDataBO();
        configDataBO.setTopology(this.getSceneConfigTopologyBO());
        configDataBO.setPlugins(this.getPlugins());

        CsceneBO csceneBO = new CsceneBO();
        csceneBO.setSceneConfig(configDataBO);
        return csceneBO;
    }

    public List<SceneConfigElementBO> getAllElements() {
        List<SceneConfigElementBO> allElements = new ArrayList<>();
        allElements.addAll(this.networkElements);
        allElements.addAll(this.nodeElements);
        return allElements;
    }

    public void setSceneConfigBO(SceneConfigBO sceneConfigBO) {
        this.sceneConfigBO = sceneConfigBO;
    }

    public void setPluginSceneConfigBO(CloudPluginSceneConfigBO pluginSceneConfigBO) {
        this.cloudPluginConfig = pluginSceneConfigBO;
    }

    private Map<String, ScenePluginDataBO> getPlugins() {
        Map<String, ScenePluginDataBO> plugins = new HashMap<>();
        plugins.put(CLOUD_PLUGIN_KEY, this.getCloudPlugin());
        return plugins;
    }

    private ScenePluginDataBO getCloudPlugin() {
        ScenePluginDataBO scenePluginDataBO = new ScenePluginDataBO();
        scenePluginDataBO.setScene(JsonUtils.beanToMap(this.cloudPluginConfig));

        Map<String, Map<String, Object>> mapPluginElements = new HashMap<>();
        for (Map.Entry<String, CloudPluginSceneConfigElementBO> entry : this.cloudPluginElements.entrySet()) {
            mapPluginElements.put(entry.getKey(), JsonUtils.beanToMap(entry.getValue()));
        }
        scenePluginDataBO.setElements(mapPluginElements);

        Map<String, Map<String, Object>> mapPluginPorts = new HashMap<>();
        for (Map.Entry<String, CloudPluginSceneConfigElementPortBO> entry : this.cloudPluginPorts.entrySet()) {
            mapPluginPorts.put(entry.getKey(), JsonUtils.beanToMap(entry.getValue()));
        }
        scenePluginDataBO.setElementPorts(mapPluginPorts);
        return scenePluginDataBO;
    }

    private SceneConfigTopologyBO getSceneConfigTopologyBO() {
        SceneConfigTopologyBO topology = new SceneConfigTopologyBO();
        topology.setConnections(this.connections);
        topology.setScene(this.sceneConfigBO);
        topology.setElements(this.getAllElements());
        return topology;
    }

}