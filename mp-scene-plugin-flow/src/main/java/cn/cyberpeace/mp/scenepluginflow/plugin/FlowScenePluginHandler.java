package cn.cyberpeace.mp.scenepluginflow.plugin;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskFlowInfo;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskTaskInfo;
import cn.cyberpeace.mp.flow.bo.FlowTaskAttackConf;
import cn.cyberpeace.mp.flow.service.FlowPostService;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.ScenePluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneBO;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneElementBO;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneElementPortBO;
import cn.cyberpeace.mp.scenepluginflow.bo.base.Flow;
import cn.cyberpeace.mp.scenepluginflow.constant.PluginConstant;
import cn.cyberpeace.mp.scenepluginflow.plugin.common.SceneHandlerUtils;
import cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler.CreateHandler;
import cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler.DeleteHandler;
import cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler.InitiateHandler;
import cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler.PrepareHandler;
import cn.cyberpeace.mp.scenepluginflow.plugin.modelhandler.FlowScenePluginModelHandler;
import cn.cyberpeace.mp.scenepluginflow.plugin.params.FlowIdWithSourceIp;
import cn.cyberpeace.mp.scenepluginflow.plugin.params.FlowWithSourceIp;
import cn.cyberpeace.mp.scenepluginflow.util.SceneFlowCacheUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
@PluginKey(value = PluginConstant.PLUGIN_KEY, priority = PluginConstant.PLUGIN_PRIORITY)
@Data
public class FlowScenePluginHandler extends ScenePluginHandler<FlowPluginSceneBO,
        FlowPluginSceneElementBO, FlowPluginSceneElementPortBO, Object> {

    private FlowScenePluginModelHandler pluginModelHandler;

    private SceneHandlerUtils handlerUtils;
    private CreateHandler createHandler;
    private DeleteHandler deleteHandler;
    private PrepareHandler prepareHandler ;
    private InitiateHandler initiateHandler ;

    @Resource
    private FlowPostService postService;

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
        prepareHandler = new PrepareHandler(this);
        initiateHandler = new InitiateHandler(this);
        postService = BeanCommon.getBean(FlowPostService.class);
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
    public void prepare(boolean partialStart) {
        prepareHandler.prepare(partialStart);
    }

    public void initiate() throws Exception {
        initiateHandler.initiate();
    }

    @Override
    public void create() {
        createHandler.create();
    }

    @Override
    public void delete() {
        deleteHandler.delete();
    }

    @Override
    public void createElement(String nodeId) {
        FlowPluginSceneElementBO element = getElement(nodeId);
        if (element == null) {
            return;
        }
        String sceneId = getSourceSceneId();
        List<AddFlowTaskTaskInfo> details = new ArrayList<>();
        List<Flow> flows = getElement(nodeId).getFlows();
        if (CollectionUtils.isNotEmpty(flows)) {
            String nodeName = getSceneHandler().getElement(nodeId).getName();
            String nodeIp = "";
            String networkId = "";
            if (CollectionUtils.isNotEmpty(getSceneHandler().getElement(nodeId).getPorts())) {
                CloudScenePluginHandler cloudPluginHandler = getSceneHandler().getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
                for (SceneElementPortBO p : getSceneHandler().getElement(nodeId).getPorts()) {
                    nodeIp = p.getIp();
                    if (StringUtils.isNotEmpty(nodeIp)) {
                        CloudPluginSceneElementNetworkBO cloudPluginSceneElementNetworkBO = cloudPluginHandler.getHandlerUtils().getNetworkByPort(p);
                        networkId = cloudPluginSceneElementNetworkBO.getResourceId();
                        break;
                    }
                }
            }
            AddFlowTaskTaskInfo addFlowTaskTaskInfo = new AddFlowTaskTaskInfo();
            addFlowTaskTaskInfo.setNodeId(nodeId);
            addFlowTaskTaskInfo.setNodeName(nodeName);
            addFlowTaskTaskInfo.setNodeIp(nodeIp);
            addFlowTaskTaskInfo.setNetworkId(networkId);
            List<AddFlowTaskFlowInfo> flowInfos = new ArrayList<>();
            for (Flow flow : flows) {
                AddFlowTaskFlowInfo addFlowTaskFlowInfo = new AddFlowTaskFlowInfo();
                addFlowTaskFlowInfo.setFlowId(flow.getId());
                if (CollectionUtils.isNotEmpty(flow.getScriptParamModels())) {
                    List<FlowTaskAttackConf> attackConfs = new ArrayList<>();
                    for (Flow.FlowScriptParam flowScriptParam : flow.getScriptParamModels()) {
                        FlowTaskAttackConf flowTaskAttackConf = new FlowTaskAttackConf();
                        flowTaskAttackConf.setConfValue(flowScriptParam.getParamValue());
                        flowTaskAttackConf.setConfType(flowScriptParam.getParamType());
                        flowTaskAttackConf.setConfKey(flowScriptParam.getParamKey());
                        flowTaskAttackConf.setConfDesc(flowScriptParam.getParamDesc());
                        attackConfs.add(flowTaskAttackConf);
                    }
                    addFlowTaskFlowInfo.setAttackConfs(attackConfs);
                }
                flowInfos.add(addFlowTaskFlowInfo);
            }
            addFlowTaskTaskInfo.setFlowInfos(flowInfos);
            details.add(addFlowTaskTaskInfo);
        }
        if (CollectionUtils.isNotEmpty(details)) {
            getHandlerUtils().addFlowTask(details, sceneId, getScene().getExecutor());
        }
    }

    @Override
    public void deleteElement(String nodeId) {
        String sceneId = getSourceSceneId();
        getHandlerUtils().deleteByNodeIdAndSceneId(nodeId, sceneId);
    }

    public boolean startFlow(String nodeId, String flowId, String sourceIp, boolean async) {
        FlowPluginSceneElementBO flowElement = getElement(nodeId);
        if (flowElement == null) {
            return false;
        }

        List<Flow> flows = SceneFlowCacheUtil.get(getSourceSceneId(), nodeId);
        flowElement.setFlows(flows);

        if (flows == null) {
            return false;
        }

        Map<String, Flow> flowMap = new HashMap<>();
        for (Flow flow1 : flows) {
            if (flowMap.put(flow1.getId(), flow1) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        Flow flow = flowMap.getOrDefault(flowId, null);
        if (flow == null) {
            return false;
        }

        String finalSourceIp = sourceIp == null ? flow.getSourceIp() : sourceIp;
        boolean flag = handlerUtils.startFlow(flowElement, flow, finalSourceIp, async);
        log.info("-----------------node- " + nodeId);
        log.info("-----------------postService- " + postService);
        log.info("-----------------getSourceSceneId()- " + getSourceSceneId());
        postService.onStopTaskNode(getSourceSceneId(), nodeId);
        return flag;
    }

    public void onStopTaskNode(String nodeId){
        getSceneHandler().asyncAckElementChanged(nodeId, true, true);
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            getSceneHandler().updateSceneElementWithPlugins(nodeId);
        }
    }

    public boolean stopFlow(String nodeId, String flowId) {
        FlowPluginSceneElementBO flowElement = getElement(nodeId);
        if (flowElement == null) {
            return false;
        }

        List<Flow> flows = SceneFlowCacheUtil.get(getSourceSceneId(), nodeId);
        flowElement.setFlows(flows);
        if (flows == null) {
            return false;
        }

        Map<String, Flow> flowMap = new HashMap<>();
        for (Flow flow1 : flows) {
            if (flowMap.put(flow1.getId(), flow1) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        Flow flow = flowMap.getOrDefault(flowId, null);
        if (flow == null) {
            return false;
        }

        boolean flag = handlerUtils.stopFlow(flow);
        asyncAckElementChanged(nodeId, true);
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            getSceneHandler().updateSceneElementWithPlugins(nodeId);
        }
        return flag;
    }

    public boolean startFlows(String nodeId, List<FlowIdWithSourceIp> flowId2Ips, boolean async, int runType) {
        FlowPluginSceneElementBO flowElement = getElement(nodeId);
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
            Map<String, Flow> flowMap = new HashMap<>();
            for (Flow flow : flows) {
                if (flowMap.put(flow.getId(), flow) != null) {
                    throw new IllegalStateException("Duplicate key");
                }
            }
            flowId2Ips.forEach(flowId2Ip -> {
                FlowWithSourceIp flowWithSourceIp = new FlowWithSourceIp();
                flowWithSourceIp.setFlow(flowMap.getOrDefault(flowId2Ip.getFlowId(), null));
                flowWithSourceIp.setSourceIp(flowId2Ip.getSourceIp() == null ? Objects.nonNull(flowWithSourceIp.getFlow()) ? flowWithSourceIp.getFlow().getSourceIp() : null : flowId2Ip.getSourceIp());
                flowWithSourceIps.add(flowWithSourceIp);
            });
        }

        if (flowWithSourceIps.isEmpty()) {
            return false;
        }

        boolean flag = handlerUtils.startFlows(flowElement, flowWithSourceIps, async, () -> {
                    log.info("batch tasks run asyncCallback");
                    asyncAckElementChanged(nodeId, true);
                    if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
                        getSceneHandler().updateSceneElementWithPlugins(nodeId);
                    }
                },
                runType, nodeId);
        asyncAckElementChanged(nodeId, true);
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            getSceneHandler().updateSceneElementWithPlugins(nodeId);
        }
        return flag;
    }

    public boolean stopFlows(String nodeId, List<String> flowIds) {
        FlowPluginSceneElementBO flowElement = getElement(nodeId);
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
            Map<String, Flow> flowMap = new HashMap<>();
            for (Flow flow : flows) {
                if (flowMap.put(flow.getId(), flow) != null) {
                    throw new IllegalStateException("Duplicate key");
                }
            }
            flowIds.forEach(flowId -> {
                newFlows.add(flowMap.getOrDefault(flowId, null));
            });
        }

        if (newFlows.isEmpty()) {
            return false;
        }

        boolean flag = handlerUtils.stopFlows(newFlows);
        asyncAckElementChanged(nodeId, true);
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            getSceneHandler().updateSceneElementWithPlugins(nodeId);
        }
        return flag;
    }

    public Flow updateFlowParams(String nodeId, String flowId, String sourceIp) {
        FlowPluginSceneElementBO flowElement = getElement(nodeId);
        if (flowElement == null) {
            return null;
        }

        List<Flow> flows = SceneFlowCacheUtil.get(getSourceSceneId(), nodeId);
        flowElement.setFlows(flows);
        if (flows == null) {
            return null;
        }
        Optional<Flow> found = Optional.empty();
        for (Flow f : flows) {
            if (flowId.equals(f.getId())) {
                found = Optional.of(f);
                break;
            }
        }
        Flow flow = found.get();
        if (flow != null) {
            flow.setSourceIp(sourceIp);
            getSceneHandler().updateSceneElementWithPlugins(nodeId);
        }
        return flow;
    }

    @Override
    protected void initClass() {
        super.sceneBOClass = FlowPluginSceneBO.class;
        super.elementBOClass = FlowPluginSceneElementBO.class;
        super.elementPortBOClass = FlowPluginSceneElementPortBO.class;
        super.contextClass = Object.class;
    }

}
