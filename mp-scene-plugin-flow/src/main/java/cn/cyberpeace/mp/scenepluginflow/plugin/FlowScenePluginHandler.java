package cn.cyberpeace.mp.scenepluginflow.plugin;

import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.plugin.ScenePluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneBO;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneElementBO;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneElementPortBO;
import cn.cyberpeace.mp.scenepluginflow.bo.base.Flow;
import cn.cyberpeace.mp.scenepluginflow.constant.PluginConstant;
import cn.cyberpeace.mp.scenepluginflow.plugin.common.SceneHandlerUtils;
import cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler.CreateHandler;
import cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler.DeleteHandler;
import cn.cyberpeace.mp.scenepluginflow.plugin.modelhandler.FlowScenePluginModelHandler;
import cn.cyberpeace.mp.scenepluginflow.plugin.params.FlowIdWithSourceIp;
import cn.cyberpeace.mp.scenepluginflow.plugin.params.FlowWithSourceIp;
import cn.cyberpeace.mp.scenepluginflow.util.SceneFlowCacheUtil;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@ClassFieldClasses({
        FlowPluginSceneBO.class,
        FlowPluginSceneElementBO.class,
        FlowPluginSceneElementPortBO.class,
        Object.class,
})
@Component
@PluginKey(value = PluginConstant.PLUGIN_KEY, priority = PluginConstant.PLUGIN_PRIORITY)
@Data
public class FlowScenePluginHandler extends ScenePluginHandler<FlowPluginSceneBO,
        FlowPluginSceneElementBO, FlowPluginSceneElementPortBO, Object> {

    private FlowScenePluginModelHandler pluginModelHandler;

    private SceneHandlerUtils handlerUtils;
    private CreateHandler createHandler;
    private DeleteHandler deleteHandler;

    @Override
    public void correctData() {
        if (getScene() == null) {
            setScene(ClassUtils.createInstance(getSceneBOClass()));
        }
    }

    @Override
    public void initBase() {
        super.initBase();
        pluginModelHandler = new FlowScenePluginModelHandler(this);
        handlerUtils = new SceneHandlerUtils(this);
        createHandler = new CreateHandler(this);
        deleteHandler = new DeleteHandler(this);
    }

    @Override
    public void reloadData() {
        setScene(null);
        getElementMap().clear();
        getElementPortMap().clear();

        pluginModelHandler.loadScene(pluginModelHandler.readScene());
        pluginModelHandler.loadElements(pluginModelHandler.readElements(getElementIds().toArray(new String[0])));
        pluginModelHandler.loadElementPorts(pluginModelHandler.readElementPorts(getElementPortIds()
                .toArray(new String[0])));
    }

    @Override
    public void saveScene() {
        pluginModelHandler.saveScene();
    }

    @Override
    public void deleteScene() {
        pluginModelHandler.deleteScene();
    }

    @Override
    public void createElements(String... elementIds) {
        pluginModelHandler.createElements(elementIds);
    }

    @Override
    public void updateElements(String... elementIds) {
        pluginModelHandler.updateElements(elementIds);
    }

    @Override
    public void deleteElements(String... elementIds) {
        pluginModelHandler.deleteElements(elementIds);
    }

    @Override
    public void createElementPorts(String... elementPortIds) {
        pluginModelHandler.createElementPorts(elementPortIds);
    }

    @Override
    public void updateElementPorts(String... elementPortIds) {
        pluginModelHandler.updateElementPorts(elementPortIds);
    }

    @Override
    public void deleteElementPorts(String... elementPortIds) {
        pluginModelHandler.deleteElementPorts(elementPortIds);
    }

    @Override
    public void create() {
        createHandler.create();
    }

    @Override
    public void delete() {
        deleteHandler.delete();
    }

    public boolean startFlow(String nodeId, String flowId, String sourceIp, boolean async) {
        var flowElement = getElement(nodeId);
        if (flowElement == null) {
            return false;
        }

        List<Flow> flows = SceneFlowCacheUtil.get(getSourceSceneId(), nodeId);
        flowElement.setFlows(flows);

        if (flows == null) {
            return false;
        }

        var flowMap = flows.stream().collect(Collectors.toMap(Flow::getId, flow -> flow));
        var flow = flowMap.getOrDefault(flowId, null);
        if (flow == null) {
            return false;
        }

        String finalSourceIp = sourceIp == null ? flow.getSourceIp() : sourceIp;
        var flag = handlerUtils.startFlow(flowElement, flow, finalSourceIp, () -> {
            asyncAckElementChanged(nodeId, true);
        }, async, () -> {
            asyncAckElementChanged(nodeId, true);
        });
        asyncAckElementChanged(nodeId, true);
        return flag;
    }

    public boolean stopFlow(String nodeId, String flowId) {
        var flowElement = getElement(nodeId);
        if (flowElement == null) {
            return false;
        }

        List<Flow> flows = SceneFlowCacheUtil.get(getSourceSceneId(), nodeId);
        flowElement.setFlows(flows);
        if (flows == null) {
            return false;
        }

        var flowMap = flows.stream().collect(Collectors.toMap(Flow::getId, flow -> flow));
        var flow = flowMap.getOrDefault(flowId, null);
        if (flow == null) {
            return false;
        }

        var flag = handlerUtils.stopFlow(flow);
        asyncAckElementChanged(nodeId, true);
        return flag;
    }

    public boolean startFlows(String nodeId, List<FlowIdWithSourceIp> flowId2Ips, boolean async, int runType) {
        var flowElement = getElement(nodeId);
        if (flowElement == null) {
            return false;
        }

        List<Flow> flows = SceneFlowCacheUtil.get(getSourceSceneId(), nodeId);
        flowElement.setFlows(flows);

        if (flows == null) {
            return false;
        }

        List<FlowWithSourceIp> flowWithSourceIps = new ArrayList<>();
        if (flowId2Ips == null || flowId2Ips.isEmpty()) {
            flows.forEach(flow -> {
                FlowWithSourceIp flowWithSourceIp = new FlowWithSourceIp();
                flowWithSourceIp.setFlow(flow);
                flowWithSourceIp.setSourceIp(flow.getSourceIp());
                flowWithSourceIps.add(flowWithSourceIp);
            });
        } else {
            var flowMap = flows.stream().collect(Collectors.toMap(Flow::getId, flow -> flow));
            flowId2Ips.forEach(flowId2Ip -> {
                FlowWithSourceIp flowWithSourceIp = new FlowWithSourceIp();
                flowWithSourceIp.setFlow(flowMap.getOrDefault(flowId2Ip.getFlowId(), null));
                flowWithSourceIp.setSourceIp(flowId2Ip.getSourceIp() == null ? flowWithSourceIp.getFlow().getSourceIp() : flowId2Ip.getSourceIp());
                flowWithSourceIps.add(flowWithSourceIp);
            });
        }

        if (flowWithSourceIps.isEmpty()) {
            return false;
        }

        var flag = handlerUtils.startFlows(flowElement, flowWithSourceIps, () -> {
                    asyncAckElementChanged(nodeId, true);
                }, async, () -> {
                    asyncAckElementChanged(nodeId, true);
                },
                runType);
        asyncAckElementChanged(nodeId, true);
        return flag;
    }

    public boolean stopFlows(String nodeId, List<String> flowIds) {
        var flowElement = getElement(nodeId);
        if (flowElement == null) {
            return false;
        }

        List<Flow> flows = SceneFlowCacheUtil.get(getSourceSceneId(), nodeId);
        flowElement.setFlows(flows);
        if (flows == null) {
            return false;
        }

        List<Flow> newFlows = new ArrayList<>();
        if (flowIds == null || flowIds.isEmpty()) {
            newFlows.addAll(flows);
        } else {
            var flowMap = flows.stream().collect(Collectors.toMap(Flow::getId, flow -> flow));
            flowIds.forEach(flowId -> {
                newFlows.add(flowMap.getOrDefault(flowId, null));
            });
        }

        if (newFlows.isEmpty()) {
            return false;
        }

        var flag = handlerUtils.stopFlows(newFlows);
        asyncAckElementChanged(nodeId, true);
        return flag;
    }

    public Flow updateFlowParams(String nodeId, String flowId, String sourceIp) {
        var flowElement = getElement(nodeId);
        if (flowElement == null) {
            return null;
        }

        List<Flow> flows = SceneFlowCacheUtil.get(getSourceSceneId(), nodeId);
        flowElement.setFlows(flows);
        if (flows == null) {
            return null;
        }
        Flow flow = flows.stream().filter(f -> flowId.equals(f.getId())).findFirst().get();
        if (flow != null) {
            flow.setSourceIp(sourceIp);
            getSceneHandler().updateSceneElementWithPlugins(nodeId);
        }
        return flow;
    }
}
