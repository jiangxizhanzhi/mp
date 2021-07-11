package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualPort;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.RecreateTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Slf4j
public class RecreateHandler extends HandlerCommon {

    private final VirtualPort vpClient = new VirtualPort();

    public static class ElementProcess {
        public static final EventProcess RECREATE_VIRTUAL_ROUTER = new EventProcess("recreate_virtual_router", "sceneplugincloud.event.recreate_virtual_router");
        public static final EventProcess RECREATE_VIRTUAL_FIREWALL = new EventProcess("recreate_virtual_firewall", "sceneplugincloud.event.recreate_virtual_firewall");
        public static final EventProcess RECREATE_VIRTUAL_MACHINE = new EventProcess("recreate_virtual_machine", "sceneplugincloud.event.recreate_virtual_machine");
        public static final EventProcess RECREATE_CONTAINER = new EventProcess("recreate_container", "sceneplugincloud.event.recreate_container");
        public static final EventProcess RECREATE_ENTITY_NODE = new EventProcess("recreate_entity_node", "sceneplugincloud.event.recreate_entity_node");
        public static final EventProcess RECREATE_DYNAMIPS = new EventProcess("recreate_dynamips", "sceneplugincloud.event.recreate_dynamips");
    }

    private DeleteInRecreateHandler deleteHandler;
    private CreateInRecreateHandler createHandler;

    public RecreateHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, RecreateTaskPool.getInstance(), event);
        this.createHandler = new CreateInRecreateHandler(pluginHandler);
        this.deleteHandler = new DeleteInRecreateHandler(pluginHandler);
    }

    public void recreateElement(CloudPluginSceneElementBO element) {
        String nodeId = element.getSrcElement().getId();
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
        getPluginHandler().updateElements(element.getSrcElement().getId());
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
            
            if (Boolean.TRUE.equals(getPluginHandler().getScene().getEnableRate())) {
                List<String> portIds = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(cloudElement.getSrcElement().getPorts())) {
                    for (var p : cloudElement.getSrcElement().getPorts()) {
                        if (StringUtils.isNotEmpty(getPluginHandler().getPort(p.getId()).getResourceId())) {
                            portIds.add(getPluginHandler().getPort(p.getId()).getResourceId());
                        }
                    }
                }
                super.vpClient.syncFlowPortInfo(portIds, "delete");
            }

            if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)) {
                runElementProgress(cloudElement.getSrcElement().getId(), RecreateHandler.ElementProcess.RECREATE_VIRTUAL_ROUTER,
                        () -> deleteVirtualRouter(cloudElement));
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_FIREWALL)) {
                runElementProgress(cloudElement.getSrcElement().getId(), RecreateHandler.ElementProcess.RECREATE_VIRTUAL_FIREWALL,
                        () -> deleteVirtualFirewall(cloudElement));
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
                runElementProgress(cloudElement.getSrcElement().getId(), RecreateHandler.ElementProcess.RECREATE_VIRTUAL_MACHINE,
                        () -> {
                            deleteVirtualMachine(cloudElement);
                            super.disableConnectUrl(getPluginHandler().getSourceSceneId(), cloudElement.getSrcElement().getId());
                        });
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
                runElementProgress(cloudElement.getSrcElement().getId(), RecreateHandler.ElementProcess.RECREATE_CONTAINER,
                        () -> {
                            deleteContainer(cloudElement);
                            super.disableConnectUrl(getPluginHandler().getSourceSceneId(), cloudElement.getSrcElement().getId());
                            if (CollectionUtils.isNotEmpty(cloudElement.getSrcElement().getPorts())) {
                                cloudElement.getSrcElement().getPorts().forEach(p -> {
                                    var pluginPort = getPluginHandler().getPort(p.getId());
                                    if (pluginPort != null && StringUtils.isNotEmpty(pluginPort.getResourceId())) {
                                        for (int i = 0; i < 5; i++) {
                                            try {
                                                Thread.sleep(1000);
                                                if (!super.containerClient.portExists(pluginPort.getResourceId())) {
                                                    break;
                                                }
                                            } catch (MpRuntimeException e) {
                                                break;
                                            } catch (Exception e) {
                                                log.error("faild to query port {}", pluginPort.getResourceId());
                                            }
                                        }
                                    }
                                });
                            }
                        });
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
                runElementProgress(cloudElement.getSrcElement().getId(), RecreateHandler.ElementProcess.RECREATE_ENTITY_NODE,
                        () -> {
                            deleteEntityNode(cloudElement);
                            super.disableConnectUrl(getPluginHandler().getSourceSceneId(), cloudElement.getSrcElement().getId());
                        });
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS)) {
                runElementProgress(cloudElement.getSrcElement().getId(), RecreateHandler.ElementProcess.RECREATE_DYNAMIPS,
                        () -> {
                            deleteVirtualDynamips(List.of(cloudElement.getResourceId()));
                            super.disableConnectUrl(getPluginHandler().getSourceSceneId(), cloudElement.getSrcElement().getId());
                        });
            }
        }
    }

    private static class CreateInRecreateHandler extends CreateHandler {
        public CreateInRecreateHandler(CloudScenePluginHandler pluginHandler) {
            super(pluginHandler);
            this.setEvent(SceneElementModel.Event.RECREATE);
        }

        @Override
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
                        () -> {
                            if (cloudElement.getSrcElement() != null && CollectionUtils.isNotEmpty(cloudElement.getSrcElement().getPorts())) {
                                cloudElement.getSrcElement().getPorts().forEach(p -> {
                                    if (StringUtils.isEmpty(p.getIp())) {
                                        p.setUsing(false);
                                    }
                                });
                            }
                            createVirtualMachine(cloudElement);
                        });
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
                runElementProgress(cloudElement.getSrcElement().getId(), RecreateHandler.ElementProcess.RECREATE_CONTAINER,
                        () -> {
                            if (cloudElement.getSrcElement() != null && CollectionUtils.isNotEmpty(cloudElement.getSrcElement().getPorts())) {
                                cloudElement.getSrcElement().getPorts().forEach(p -> {
                                    if (StringUtils.isEmpty(p.getIp())) {
                                        p.setUsing(false);
                                    }
                                });
                            }
                            createContainer(cloudElement);
                        });
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
                runElementProgress(cloudElement.getSrcElement().getId(), RecreateHandler.ElementProcess.RECREATE_ENTITY_NODE,
                        () -> createEntityNode(cloudElement));
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS)) {
                runElementProgress(cloudElement.getSrcElement().getId(), RecreateHandler.ElementProcess.RECREATE_DYNAMIPS,
                        () -> {
                            createVirtualDynamips(cloudElement);
                            configDynamic(List.of(cloudElement));
                        } );
            }

            
            if (Boolean.TRUE.equals(getPluginHandler().getScene().getEnableRate())) {
                List<String> portIds = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(cloudElement.getSrcElement().getPorts())) {
                    for (var p : cloudElement.getSrcElement().getPorts()) {
                        if (StringUtils.isNotEmpty(getPluginHandler().getPort(p.getId()).getResourceId())) {
                            portIds.add(getPluginHandler().getPort(p.getId()).getResourceId());
                        };
                    }
                }
                super.vpClient.syncFlowPortInfo(portIds, "add");
            }
        }
    }
}
