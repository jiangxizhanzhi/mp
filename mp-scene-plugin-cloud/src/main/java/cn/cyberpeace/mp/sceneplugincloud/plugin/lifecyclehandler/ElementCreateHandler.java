package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.RecreateTaskPool;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/25
 */
@Slf4j
public class ElementCreateHandler extends HandlerCommon {

    public static class ElementProcess {
        public static final EventProcess START_VIRTUAL_ROUTER = new EventProcess("start_virtual_router", "sceneplugincloud.event.start_virtual_router");
        public static final EventProcess START_VIRTUAL_FIREWALL = new EventProcess("start_virtual_firewall", "sceneplugincloud.event.start_virtual_firewall");
        public static final EventProcess START_VIRTUAL_MACHINE = new EventProcess("start_virtual_machine", "sceneplugincloud.event.start_virtual_machine");
        public static final EventProcess START_CONTAINER = new EventProcess("start_container", "sceneplugincloud.event.start_container");
        public static final EventProcess START_ENTITY_NODE = new EventProcess("start_entity_node", "sceneplugincloud.event.start_entity_node");
    }

    private CreateHandler createHandler;

    public ElementCreateHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, RecreateTaskPool.getInstance(), event);
        this.createHandler = new CreateHandler(pluginHandler);
    }

    public void createElement(CloudPluginSceneElementBO element) {
        String nodeId = element.getSrcElement().getId();
        Integer virtualType = element.getVirtualType();
        if (virtualType == null) {
            log.warn("Element({}) {} has no virtual type", element.getSrcElement().getEid(),
                    element.getSrcElement().getId());
            return;
        }
        this.prepareElementProcess(nodeId, element.getVirtualType());
        this.createHandler.createElementProcess(element);
        getPluginHandler().updateElements(element.getSrcElement().getId());
    }

    private void prepareElementProcess(String nodeId, Integer virtualType) {
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.CREATE, Arrays.asList(ElementProcess.START_VIRTUAL_ROUTER));
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_FIREWALL)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.CREATE, Arrays.asList(ElementProcess.START_VIRTUAL_FIREWALL));
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.CREATE, Arrays.asList(ElementProcess.START_VIRTUAL_MACHINE));
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.CREATE, Arrays.asList(ElementProcess.START_CONTAINER));
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.CREATE, Arrays.asList(ElementProcess.START_ENTITY_NODE));
        }
    }
}
