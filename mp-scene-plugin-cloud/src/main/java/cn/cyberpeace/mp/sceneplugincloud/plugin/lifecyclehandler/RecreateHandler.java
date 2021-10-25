package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/25
 */
@Slf4j
public class RecreateHandler extends HandlerCommon {

    public static class ElementProcess {
        public static final EventProcess RECREATE_VIRTUAL_ROUTER = new EventProcess("recreate_virtual_router", "sceneplugincloud.event.recreate_virtual_router");
        public static final EventProcess RECREATE_VIRTUAL_FIREWALL = new EventProcess("recreate_virtual_firewall", "sceneplugincloud.event.recreate_virtual_firewall");
        public static final EventProcess RECREATE_VIRTUAL_MACHINE = new EventProcess("recreate_virtual_machine", "sceneplugincloud.event.recreate_virtual_machine");
        public static final EventProcess RECREATE_CONTAINER = new EventProcess("recreate_container", "sceneplugincloud.event.recreate_container");
        public static final EventProcess RECREATE_ENTITY_NODE = new EventProcess("recreate_entity_node", "sceneplugincloud.event.recreate_entity_node");
    }

    private DeleteInRecreateHandler deleteHandler;
    private CreateInRecreateHandler createHandler;

    public RecreateHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, CreateTaskPool.getInstance(), event);
        this.createHandler = new CreateInRecreateHandler(pluginHandler);
        this.deleteHandler = new DeleteInRecreateHandler(pluginHandler);
    }

    public void recreateElement(String nodeId) {
        CloudPluginSceneElementBO element = getPluginHandler().getElement(nodeId);
        if (element == null) {
            return;
        }
        Integer virtualType = element.getVirtualType();
        if (virtualType == null) {
            log.warn("Element({}) {} has no virtual type", element.getSrcElement().getEid(),
                    element.getSrcElement().getId());
            return;
        }
        this.prepareElementProcess(nodeId, element.getVirtualType());
        try {
            this.deleteHandler.deleteElementProcess(element);
        } catch (Exception e) {
            log.error("faild to execute delete process when recreate element,nodeId is {}", nodeId, e);
        }
        this.createHandler.createElementProcess(element);
    }

    private void prepareElementProcess(String nodeId, Integer virtualType) {
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.RECREATE, Arrays.asList(ElementProcess.RECREATE_VIRTUAL_ROUTER));
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_FIREWALL)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.RECREATE, Arrays.asList(ElementProcess.RECREATE_VIRTUAL_FIREWALL));
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.RECREATE, Arrays.asList(ElementProcess.RECREATE_VIRTUAL_MACHINE));
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.RECREATE, Arrays.asList(ElementProcess.RECREATE_CONTAINER));
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.RECREATE, Arrays.asList(ElementProcess.RECREATE_ENTITY_NODE));
        }
    }

    private static class DeleteInRecreateHandler extends DeleteHandler {
        public DeleteInRecreateHandler(CloudScenePluginHandler pluginHandler) {
            super(pluginHandler);
            this.setEvent(SceneElementModel.Event.RECREATE);
        }

        protected void deleteElementProcess(CloudPluginSceneElementBO cloudElement) {
            if (Boolean.TRUE.equals(cloudElement.getExternal())) {
                return;
            }
            Integer virtualType = cloudElement.getVirtualType();
            if (virtualType == null) {
                log.warn("Element({}) {} has no virtual type", cloudElement.getSrcElement().getEid(),
                        cloudElement.getSrcElement().getId());
                return;
            }

            if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)) {
                runElementProgress(cloudElement.getSrcElement().getId(), RecreateHandler.ElementProcess.RECREATE_VIRTUAL_ROUTER,
                        () -> deleteVirtualRouter(cloudElement));
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_FIREWALL)) {
                runElementProgress(cloudElement.getSrcElement().getId(), RecreateHandler.ElementProcess.RECREATE_VIRTUAL_FIREWALL,
                        () -> deleteVirtualFirewall(cloudElement));
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
                runElementProgress(cloudElement.getSrcElement().getId(), RecreateHandler.ElementProcess.RECREATE_VIRTUAL_MACHINE,
                        () -> deleteVirtualMachine(cloudElement));
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
                runElementProgress(cloudElement.getSrcElement().getId(), RecreateHandler.ElementProcess.RECREATE_CONTAINER,
                        () -> deleteContainer(cloudElement));
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
                runElementProgress(cloudElement.getSrcElement().getId(), RecreateHandler.ElementProcess.RECREATE_ENTITY_NODE,
                        () -> deleteEntityNode(cloudElement));
            }
        }
    }

    private static class CreateInRecreateHandler extends CreateHandler {
        public CreateInRecreateHandler(CloudScenePluginHandler pluginHandler) {
            super(pluginHandler);
            this.setEvent(SceneElementModel.Event.RECREATE);
        }

        public void createElementProcess(CloudPluginSceneElementBO cloudElement) {
            if (Boolean.TRUE.equals(cloudElement.getExternal())) {
                return;
            }

            Integer virtualType = cloudElement.getVirtualType();
            if (virtualType == null) {
                log.warn("Element({}) {} has no virtual type", cloudElement.getSrcElement().getEid(),
                        cloudElement.getSrcElement().getId());
                return;
            }

            if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)) {
                runElementProgress(cloudElement.getSrcElement().getId(), RecreateHandler.ElementProcess.RECREATE_VIRTUAL_ROUTER,
                        () -> createVirtualRouter(cloudElement));
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_FIREWALL)) {
                runElementProgress(cloudElement.getSrcElement().getId(), RecreateHandler.ElementProcess.RECREATE_VIRTUAL_FIREWALL,
                        () -> createVirtualFirewall(cloudElement));
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
                runElementProgress(cloudElement.getSrcElement().getId(), RecreateHandler.ElementProcess.RECREATE_VIRTUAL_MACHINE,
                        () -> createVirtualMachine(cloudElement));
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
                runElementProgress(cloudElement.getSrcElement().getId(), RecreateHandler.ElementProcess.RECREATE_CONTAINER,
                        () -> createContainer(cloudElement));
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
                runElementProgress(cloudElement.getSrcElement().getId(), RecreateHandler.ElementProcess.RECREATE_ENTITY_NODE,
                        () -> createEntityNode(cloudElement));
            }
        }
    }
}
