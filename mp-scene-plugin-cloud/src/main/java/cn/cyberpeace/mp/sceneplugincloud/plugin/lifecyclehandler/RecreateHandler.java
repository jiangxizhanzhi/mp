package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.auth.model.MicrosoftAzureUserModel;
import cn.cyberpeace.mp.auth.repository.MicrosoftAzureLoginMapper;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.scene.bo.AlertRuleBO;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.AlertRuleModel;
import cn.cyberpeace.mp.scene.model.InstanceModel;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import cn.cyberpeace.mp.scene.repository.AlertRuleMapper;
import cn.cyberpeace.mp.scene.repository.InstanceMapper;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.DevopsLoadSourceModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureContainer;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureVirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.Load;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualPort;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.RecreateTaskPool;
import cn.cyberpeace.mp.sceneplugincloud.repository.DevopsLoadSourceMapper;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import com.azure.resourcemanager.monitor.fluent.models.DataCollectionRuleResourceInner;
import com.alibaba.fastjson.JSONObject;
import com.azure.resourcemanager.network.models.PublicIpAddress;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/25
 */
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
        public static final EventProcess RECREATE_LOAD = new EventProcess("recreate_load", "sceneplugincloud.event.recreate_load");
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
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.LOAD)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.RECREATE, Arrays.asList(ElementProcess.RECREATE_LOAD));
            runElementProgress(nodeId, SceneElementModel.Event.RECREATE, ElementProcess.RECREATE_LOAD, () -> {
                closeAccessModes(element);
                DevopsLoadSourceMapper devopsLoadSourceMapper = BeanCommon.getBean(DevopsLoadSourceMapper.class);
                DevopsLoadSourceModel synchronousDataSource = devopsLoadSourceMapper.getSynchronousDataSource();
                Load load = new Load(synchronousDataSource);
                load.recreate(element);
            });
        } else {
            try {
                this.deleteHandler.deleteElementProcess(element);
            } catch (Exception e) {
                log.error("faild to execute delete process when recreate element,nodeId is {}", nodeId, e);
            }
            this.createHandler.createElementProcess(element);
            getPluginHandler().updateElements(element.getSrcElement().getId());
        }
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
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.LOAD)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.RECREATE, Arrays.asList(ElementProcess.RECREATE_LOAD));
        }
    }

    private static class DeleteInRecreateHandler extends DeleteHandler {

        private String azureManagerUrl;

        private MicrosoftAzureLoginMapper sceneElementPluginConfigMapper;

        public DeleteInRecreateHandler(CloudScenePluginHandler pluginHandler) {
            super(pluginHandler);
            this.setEvent(SceneElementModel.Event.RECREATE);
            this.azureManagerUrl = BeanCommon.getBean(SituationAwareConfig.class).getAzureManagerUrl();
            this.sceneElementPluginConfigMapper = BeanCommon.getBean(MicrosoftAzureLoginMapper.class);
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
            //通知云平台需要统计环境总速率的端口
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
            Integer cloudType = getPluginHandler().getSceneHandler().getScene().getCloudType();
            if (cloudType != BaseSceneModel.CloudType.DEFAULT) {
                if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
                    runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.DELETE_VIRTUAL_MACHINE,
                            () -> {
                                closeAccessModes(cloudElement);

                                if (cloudType == BaseSceneModel.CloudType.PUBLIC) {
                                    AzureVirtualMachine azureVirtualMachine = new AzureVirtualMachine(IAzureResourceManager.getInstance(cloudType));
                                    azureVirtualMachine.delete(cloudElement);
                                } else {
                                    JSONObject jsonObject = new JSONObject();
                                    //云类型(1: 微软公有云 2:微软私有云 3:微软混合云)
                                    jsonObject.put("cloudType", cloudType);
                                    MicrosoftAzureUserModel localAzurer = sceneElementPluginConfigMapper.getLocalAzurer(jsonObject);

                                    String url = azureManagerUrl + "/cloud/vm_delete";
                                    Map<String, Object> formMap = new HashMap();
                                    formMap.put("azureUser", localAzurer);
                                    formMap.put("vmId", cloudElement.getResourceId());
                                    //请求头设置
                                    HashMap<String, String> headersMap = new HashMap<>();
                                    String body = JsonUtils.stringify(formMap);
                                    try {
                                        HttpUtils.sendHttpPost(url, body, headersMap);
                                    } catch (IOException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }

                                this.disableConnectUrl(getPluginHandler().getSourceSceneId(), cloudElement.getSrcElement().getId());
                            });
                } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
                    runElementProgress(cloudElement.getSrcElement().getId(), RecreateHandler.ElementProcess.RECREATE_CONTAINER,
                            () -> {
                                closeAccessModes(cloudElement);
                                AzureContainer azureContainer = new AzureContainer(IAzureResourceManager.getInstance(cloudType));
                                azureContainer.delete(cloudElement.getResourceId());
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
                } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.LOAD)) {
                    runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.DELETE_LOAD,
                            () -> {
                                closeAccessModes(cloudElement);
                                DevopsLoadSourceMapper devopsLoadSourceMapper = BeanCommon.getBean(DevopsLoadSourceMapper.class);
                                DevopsLoadSourceModel synchronousDataSource = devopsLoadSourceMapper.getSynchronousDataSource();
                                Load load = new Load(synchronousDataSource);
                                load.delete(cloudElement);
                                this.disableConnectUrl(getPluginHandler().getSourceSceneId(), cloudElement.getSrcElement().getId());
                            });
                }
            } else {
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
    }

    private static class CreateInRecreateHandler extends CreateHandler {
        private String groupNamePrefix;
        private String azureManagerUrl;

        private MicrosoftAzureLoginMapper sceneElementPluginConfigMapper;
        public CreateInRecreateHandler(CloudScenePluginHandler pluginHandler) {
            super(pluginHandler);
            this.setEvent(SceneElementModel.Event.RECREATE);
            this.groupNamePrefix = BeanCommon.getBean(SituationAwareConfig.class).getGroupNamePrefix();
            this.azureManagerUrl = BeanCommon.getBean(SituationAwareConfig.class).getAzureManagerUrl();
            this.sceneElementPluginConfigMapper = BeanCommon.getBean(MicrosoftAzureLoginMapper.class);
        }

        @Override
        public void createElementProcess(CloudPluginSceneElementBO cloudElement) {
            if (Boolean.TRUE.equals(cloudElement.getExternal())) {
                return;
            }

            AlertRuleMapper alertRuleMapper = BeanCommon.getBean(AlertRuleMapper.class);
            String eid = getPluginHandler().getSrcScene().getEid();
            InstanceMapper instanceMapper = BeanCommon.getBean(InstanceMapper.class);
            HumanCondition instanceHumanCondition = new HumanCondition()
                    .filterEqual(InstanceModel::getScene, eid);
            InstanceModel instanceModel = instanceMapper.first(instanceHumanCondition);
//            TODO debug id是否一致
            HumanCondition alertRuleHumanCondition = new HumanCondition()
                    .filterEqual(AlertRuleModel::getSceneId, instanceModel.getCscene())
                    .filterContains(AlertRuleModel::getAlarmConditions, cloudElement.getSrcElement().getId());
            List<AlertRuleModel> alertRuleModelList = alertRuleMapper.list(alertRuleHumanCondition);
            List<AlertRuleBO> alertRuleBOList = alertRuleModelList.stream().map(alertRuleModel -> {
                        AlertRuleBO alertRuleBO = new AlertRuleBO();
                        BeanUtils.copyProperties(alertRuleModel, alertRuleBO);
                        alertRuleBO.parseHosts();
                        alertRuleBO.parseAlertConditions();
                        return alertRuleBO;
                    })
                    .collect(Collectors.toList());

            Integer virtualType = cloudElement.getVirtualType();
            if (virtualType == null) {
                log.warn("Element({}) {} has no virtual type", cloudElement.getSrcElement().getEid(),
                        cloudElement.getSrcElement().getId());
                return;
            }
            Integer cloudType = getPluginHandler().getSceneHandler().getScene().getCloudType();
            if (cloudType != BaseSceneModel.CloudType.DEFAULT) {
                JSONObject jsonObject = new JSONObject();
                //云类型(1: 微软公有云 2:微软私有云 3:微软混合云)
                jsonObject.put("cloudType", cloudType);
                MicrosoftAzureUserModel localAzurer = sceneElementPluginConfigMapper.getLocalAzurer(jsonObject);
                IAzureResourceManager resourceService = IAzureResourceManager.getInstance(cloudType);
                AzureVirtualMachine azureVirtualMachine = new AzureVirtualMachine(resourceService);
                String groupName = getPluginHandler().getSourceSceneId();
                groupName = StringUtils.isNotBlank(groupNamePrefix)?groupNamePrefix +"-"+ groupName:groupName;
                String dataCollectionRulesId;
                if(alertRuleBOList.size()>0){
                    DataCollectionRuleResourceInner byResourceGroup = resourceService
                            .getMonitorManager()
                            .serviceClient()
                            .getDataCollectionRules()
                            .getByResourceGroup(groupName, "MSVMI-" + groupName);
                    dataCollectionRulesId = byResourceGroup != null ? byResourceGroup.id() : "";
                } else {
                    dataCollectionRulesId = "";
                }
                if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
                    String finalGroupName = groupName;
                    runElementProgress(cloudElement.getSrcElement().getId(), RecreateHandler.ElementProcess.RECREATE_VIRTUAL_MACHINE,
                            () -> {
                                if (cloudElement.getSrcElement() != null && CollectionUtils.isNotEmpty(cloudElement.getSrcElement().getPorts())) {
                                    cloudElement.getSrcElement().getPorts().forEach(p -> {
                                        var connectNetwork = getHandlerUtils().getNetworkByPort(p);
                                        // 未分配ip并且连接的网络不是外部网络 标识端口未使用
                                        if (connectNetwork == null || StringUtils.isEmpty(p.getIp()) && !HandlerUtils.isExternalNetwork(connectNetwork.getResourceId())) {
                                            p.setUsing(false);
                                        }
                                    });
                                }

                                List<VirtualNic> virtualNics = this.genVirutalNics(cloudElement);
                                azureVirtualMachine.create(getPluginHandler(), cloudElement, virtualNics, combineInitCommand(cloudElement), alertRuleBOList, dataCollectionRulesId, finalGroupName, azureManagerUrl, localAzurer);
                                openAccessModes(cloudElement);
                            });
                } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
                    String finalGroupName1 = groupName;
                    runElementProgress(cloudElement.getSrcElement().getId(), RecreateHandler.ElementProcess.RECREATE_CONTAINER,
                            () -> {
                                if (cloudElement.getSrcElement() != null && CollectionUtils.isNotEmpty(cloudElement.getSrcElement().getPorts())) {
                                    cloudElement.getSrcElement().getPorts().forEach(p -> {
                                        var connectNetwork = getHandlerUtils().getNetworkByPort(p);
                                        // 未分配ip并且连接的网络不是外部网络 标识端口未使用
                                        if (StringUtils.isEmpty(p.getIp()) && !HandlerUtils.isExternalNetwork(connectNetwork.getResourceId())) {
                                            p.setUsing(false);
                                        }
                                    });
                                }
                                new AzureContainer(IAzureResourceManager.getInstance(cloudType))
                                        .create(getPluginHandler(), cloudElement, this.genVirutalNics(cloudElement),alertRuleBOList,dataCollectionRulesId, finalGroupName1);
                                openAccessModes(cloudElement);
                            });
                } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.LOAD)) {
                    String finalGroupName2 = groupName;
                    runElementProgress(cloudElement.getSrcElement().getId(), RecreateHandler.ElementProcess.RECREATE_LOAD,
                            () -> {
                                if (cloudElement.getSrcElement() != null && CollectionUtils.isNotEmpty(cloudElement.getSrcElement().getPorts())) {
                                    cloudElement.getSrcElement().getPorts().forEach(p -> {
                                        var connectNetwork = getHandlerUtils().getNetworkByPort(p);
                                        // 未分配ip并且连接的网络不是外部网络 标识端口未使用
                                        if (StringUtils.isEmpty(p.getIp()) && !HandlerUtils.isExternalNetwork(connectNetwork.getResourceId())) {
                                            p.setUsing(false);
                                        }
                                    });
                                }
                                DevopsLoadSourceMapper devopsLoadSourceMapper = BeanCommon.getBean(DevopsLoadSourceMapper.class);
                                DevopsLoadSourceModel synchronousDataSource = devopsLoadSourceMapper.getSynchronousDataSource();
                                new Load(synchronousDataSource).create(getPluginHandler(), cloudElement, finalGroupName2);

                            });
                }
            } else {

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
                                        var connectNetwork = getHandlerUtils().getNetworkByPort(p);
                                        // 未分配ip并且连接的网络不是外部网络 标识端口未使用
                                        if (StringUtils.isEmpty(p.getIp()) && !HandlerUtils.isExternalNetwork(connectNetwork.getResourceId())) {
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
                                        var connectNetwork = getHandlerUtils().getNetworkByPort(p);
                                        // 未分配ip并且连接的网络不是外部网络 标识端口未使用
                                        if (StringUtils.isEmpty(p.getIp()) && !HandlerUtils.isExternalNetwork(connectNetwork.getResourceId())) {
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
                            });
                }
            }
            //通知云平台需要统计环境总速率的端口
            if (Boolean.TRUE.equals(getPluginHandler().getScene().getEnableRate())) {
                List<String> portIds = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(cloudElement.getSrcElement().getPorts())) {
                    for (var p : cloudElement.getSrcElement().getPorts()) {
                        if (StringUtils.isNotEmpty(getPluginHandler().getPort(p.getId()).getResourceId())) {
                            portIds.add(getPluginHandler().getPort(p.getId()).getResourceId());
                        }
                        ;
                    }
                }
                super.vpClient.syncFlowPortInfo(portIds, "add");
            }
        }
    }
}
