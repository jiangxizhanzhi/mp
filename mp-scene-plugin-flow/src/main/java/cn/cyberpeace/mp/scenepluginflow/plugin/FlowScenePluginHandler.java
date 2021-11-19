package cn.cyberpeace.mp.scenepluginflow.plugin;

import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
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
import lombok.Data;
import org.springframework.stereotype.Component;

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

    public boolean startFlow(String nodeId, String flowId) {
        var flowElement = getElement(nodeId);
        if (flowElement == null) {
            return false;
        }

        var flows = flowElement.getFlows();
        if (flows == null) {
            return false;
        }

        var flowMap = flows.stream().collect(Collectors.toMap(Flow::getId, flow -> flow));
        var flow = flowMap.getOrDefault(flowId, null);
        if (flow == null) {
            return false;
        }

        var flag = handlerUtils.startFlow(flowElement, flow);
        ackElementChanged(nodeId, true);
        return flag;
    }

    public boolean stopFlow(String nodeId, String flowId) {
        var flowElement = getElement(nodeId);
        if (flowElement == null) {
            return false;
        }

        var flows = flowElement.getFlows();
        if (flows == null) {
            return false;
        }

        var flowMap = flows.stream().collect(Collectors.toMap(Flow::getId, flow -> flow));
        var flow = flowMap.getOrDefault(flowId, null);
        if (flow == null) {
            return false;
        }

        var flag = handlerUtils.stopFlow(flow);
        ackElementChanged(nodeId, true);
        return flag;
    }
}
