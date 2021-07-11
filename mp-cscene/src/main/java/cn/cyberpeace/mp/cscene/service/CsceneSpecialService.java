package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Count;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Sum;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceCountActivitySceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceStat;
import cn.cyberpeace.mp.cscene.bo.CsceneNodeInfoBO;
import cn.cyberpeace.mp.cscene.bo.CsceneStat;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneInstanceStat;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneStat;
import cn.cyberpeace.mp.cscene.constant.CsceneBizConstant;
import cn.cyberpeace.mp.cscene.core.common.UserCommon;
import cn.cyberpeace.mp.cscene.dto.scene.CsceneInstanceInfo;
import cn.cyberpeace.mp.cscene.dto.scene.SceneStateInfo;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceCountActivitySceneModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.model.CsceneSnapshotModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneSnapshotMapper;
import cn.cyberpeace.mp.cscene.service.param.CsceneInstanceStatQueryParams;
import cn.cyberpeace.mp.cscene.util.NodeLimitUtils;
import cn.cyberpeace.mp.cscene.util.SceneStatusUtil;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.model.SceneConfigElementModel;
import cn.cyberpeace.mp.scene.model.SceneConfigModel;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.scene.repository.SceneMapper;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeSessionBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.UserConnectionNumBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import cn.cyberpeace.mp.sceneplugincloud.util.resourcename.IResourceName;
import cn.cyberpeace.mp.sceneplugincloud.util.resourcename.RsourceNameParams;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CsceneSpecialService {

    @Autowired
    private CsceneMapper csceneMapper;

    @Autowired
    private SceneMapper sceneMapper;

    @Autowired
    private SceneElementMapper sceneElementMapper;

    @Autowired
    private CsceneInstanceMapper csceneInstanceMapper;

    @Autowired
    private CsceneSnapshotMapper csceneSnapshotMapper;

    @Resource
    private ScenePluginCloudSceneService cloudSceneService;

    @Data
    public static class CsceneElementVirtualStat {
        private String csceneId;
        private Integer virtualType;
        private Integer virtualTypeCount;
    }

    @Data
    public static class CsceneElementStat {
        private String csceneId;
        private Integer elementType;
        private Integer elementTypeCount;
    }

    @Data
    public static class CsceneTestInstanceStat {
        private String csceneId;
        private Integer testInstanceCount;
    }

    @Data
    public static class CsceneInstanceCountStat {
        private String csceneId;
        private Integer instanceCount;
    }

    
    public ServiceResult<List<CsceneStat>> statCscenes(String[] ids) {
        if (ids == null || ids.length == 0) {
            return new ServiceResult<>(new ArrayList<>());
        }

        Map<String, CsceneStat> statMap = new HashMap<>();
        for (String id : ids) {
            var csceneStat = new CsceneStat(id, 0, 0, 0, 0, 0, 0, 0);
            statMap.put(id, csceneStat);
        }

        var elementStatCondition = new HumanCondition()
                .link(CsceneModel.class, SceneConfigElementModel.class)
                .filterLink(CsceneModel.class, CsceneModel::getSceneConfig,
                        SceneConfigElementModel.class, SceneConfigElementModel::getScene)
                .currentLink(CsceneModel.class)
                .filterIn(CsceneModel::getId, ids)
                .groupBy(CsceneModel::getId)
                .selectAs(CsceneModel::getId, CsceneElementStat::getCsceneId)
                .currentLink(SceneConfigElementModel.class)
                .groupBy(SceneConfigElementModel::getType)
                .selectAs(SceneConfigElementModel::getType, CsceneElementStat::getElementType)
                .selectAs(Count.f(SceneConfigElementModel::getType), CsceneElementStat::getElementTypeCount);
        List<CsceneElementStat> csceneElementStats = csceneMapper.list(elementStatCondition, CsceneElementStat.class);
        csceneElementStats.forEach(elementStat -> {
            var stat = statMap.get(elementStat.getCsceneId());
            var elementType = elementStat.getElementType();
            if (elementType.equals(BaseSceneElementModel.Type.SWITCH)) {
                stat.setSwitchCount(elementStat.getElementTypeCount());
            } else if (elementType.equals(BaseSceneElementModel.Type.ROUTER)) {
                stat.setRouterCount(elementStat.getElementTypeCount());
            } else if (elementType.equals(BaseSceneElementModel.Type.FIREWALL)) {
                stat.setFirewallCount(elementStat.getElementTypeCount());
            } else if (elementType.equals(BaseSceneElementModel.Type.TERMINAL)) {
                stat.setTerminalCount(elementStat.getElementTypeCount());
            } else if (elementType.equals(BaseSceneElementModel.Type.NPC)) {
                stat.setNpcCount(elementStat.getElementTypeCount());
            }
        });


        var virtualStatCondition = new HumanCondition()
                .link(CsceneModel.class, CloudPluginSceneConfigElementModel.class)
                .filterLink(CsceneModel.class, CsceneModel::getSceneConfig,
                        CloudPluginSceneConfigElementModel.class, CloudPluginSceneConfigElementModel::getScene)
                .currentLink(CsceneModel.class)
                .filterIn(CsceneModel::getId, ids)
                .groupBy(CsceneModel::getId)
                .selectAs(CsceneModel::getId, CsceneElementStat::getCsceneId)
                .currentLink(CloudPluginSceneConfigElementModel.class)
                .groupBy(CloudPluginSceneConfigElementModel::getVirtualType)
                .selectAs(CloudPluginSceneConfigElementModel::getVirtualType, CsceneElementVirtualStat::getVirtualType)
                .selectAs(Count.f(CloudPluginSceneConfigElementModel::getVirtualType), CsceneElementVirtualStat::getVirtualTypeCount);
        List<CsceneElementVirtualStat> csceneElementVirtualStats = csceneMapper.list(virtualStatCondition, CsceneElementVirtualStat.class);
        csceneElementVirtualStats.forEach(elementStat -> {
            var stat = statMap.get(elementStat.getCsceneId());
            var virtualType = elementStat.getVirtualType();
            if (NodeLimitUtils.isLimitedNode(virtualType)) {
                stat.setResourceOccupiedNums(stat.getResourceOccupiedNums() + elementStat.getVirtualTypeCount());
            }
        });

        var testSceneStatCondition = new HumanCondition()
                .link(CsceneInstanceModel.class, SceneModel.class)
                .filterLink(CsceneInstanceModel.class, CsceneInstanceModel::getScene,
                        SceneModel.class, SceneModel::getId)
                .currentLink(CsceneInstanceModel.class)
                .filterEqual(CsceneInstanceModel::getType, CsceneInstanceModel.Type.TEST)
                .filterIn(CsceneInstanceModel::getCscene, ids)
                .groupBy(CsceneInstanceModel::getCscene)
                .selectAs(CsceneInstanceModel::getCscene, CsceneTestInstanceStat::getCsceneId)
                .selectAs(Count.f(CsceneInstanceModel::getCscene), CsceneTestInstanceStat::getTestInstanceCount)
                .currentLink(SceneModel.class)
                .filterEqual(SceneModel::getDeleted, false);
        List<CsceneTestInstanceStat> csceneTestInstanceStats = csceneInstanceMapper.list(testSceneStatCondition,
                CsceneTestInstanceStat.class);
        csceneTestInstanceStats.forEach(testInstanceStat -> {
            var stat = statMap.get(testInstanceStat.getCsceneId());
            stat.setTestSceneCount(testInstanceStat.getTestInstanceCount());
        });

        return new ServiceResult<>(new ArrayList<>(statMap.values()));
    }

    
    public ListObjServiceResult<CsceneInstanceStat> listCsceneInstanceStats(CsceneInstanceStatQueryParams queryParams) {
        var humanCondition = new HumanCondition()
                .link(CsceneInstanceModel.class, SceneModel.class)
                .filterLink(CsceneInstanceModel.class, CsceneInstanceModel::getScene, SceneModel.class,
                        SceneModel::getId)
                .pagination(queryParams.getPage(), queryParams.getPageSize())
                .currentLink(CsceneInstanceModel.class)
                .groupBy(CsceneInstanceModel::getId)
                .selectAs("pk", "pk")
                .selectAs(CsceneInstanceModel::getId, CsceneInstanceStat::getId)
                .selectAs(CsceneInstanceModel::getType, CsceneInstanceStat::getType)
                .selectAs(CsceneInstanceModel::getReferenceType, CsceneInstanceStat::getReferenceType)
                .selectAs(CsceneInstanceModel::getReferenceMetaData, CsceneInstanceStat::getReferenceMetaData)
                .selectAs(CsceneInstanceModel::getCreateUsername, CsceneInstanceStat::getCreateUsername)
                .selectAs(CsceneInstanceModel::getCreateTime, CsceneInstanceStat::getCreateTime)
                .selectAs(CsceneInstanceModel::getUpdateTime, CsceneInstanceStat::getUpdateTime)
                .selectAs(CsceneInstanceModel::getSceneTtl, CsceneInstanceStat::getSceneTtl)
                .selectAs(CsceneInstanceModel::getSceneAttr, CsceneInstanceStat::getSceneAttr)
                .selectAs(CsceneInstanceModel::getSceneTtl, CsceneInstanceStat::getSceneTtl)
                .selectAs(CsceneInstanceModel::getName, CsceneInstanceStat::getName)
                .selectAs(CsceneInstanceModel::getDeleted, CsceneInstanceStat::getDeleted)
                .currentLink(SceneModel.class)
                .selectAs(SceneModel::getEvent, CsceneInstanceStat::getEvent)
                .selectAs(SceneModel::getEventStatus, CsceneInstanceStat::getEventStatus)
                .selectAs(SceneModel::getProcessMessage, CsceneInstanceStat::getEventMessage)
                .selectAs(SceneModel::getCreateConsumeSeconds, CsceneInstanceStat::getCreateConsumeSeconds);

        humanCondition.currentLink(CsceneInstanceModel.class);
        var ids = queryParams.getIds();
        if (ids != null && ids.length > 0) {
            humanCondition.filterIn(CsceneInstanceModel::getId, ids);
        }
        var type = queryParams.getType();
        if (type != null) {
            humanCondition.filterEqual(CsceneInstanceModel::getType, type);
        }
        var referenceTypes = queryParams.getReferenceTypes();
        if (referenceTypes != null && referenceTypes.length > 0) {
            humanCondition.filterIn(CsceneInstanceModel::getReferenceType, referenceTypes);
        }
        var valid = queryParams.getValid();
        if (Boolean.TRUE.equals(valid)) {
            humanCondition.filterEqual(CsceneInstanceModel::getDeleted, false);
        }
        var self = queryParams.getSelf();
        if (Boolean.TRUE.equals(self)) {
            var currentUser = UserCommon.getCurrentUser();
            if (currentUser != null) {
                humanCondition.filterEqual(CsceneInstanceModel::getCreateUser, currentUser.getId());
            }
        }
        var search = queryParams.getSearch();
        if (!StringUtils.isEmpty(search)) {
            humanCondition.filterIContains(CsceneInstanceModel::getName, search);
        }

        humanCondition.currentLink(SceneModel.class);
        var error = queryParams.getError();
        if (Boolean.TRUE.equals(error)) {
            humanCondition.filterEqual(SceneModel::getEventStatus, EventStatus.ABNORMAL);
        }
        var over = queryParams.getOver();
        if (Boolean.TRUE.equals(over)) {
            humanCondition.filterEqual(SceneModel::getEvent, SceneModel.Event.DELETE);
        }
        var active = queryParams.getActive();
        if (Boolean.TRUE.equals(active)) {
            humanCondition.filterNotEqual(SceneModel::getEvent, SceneModel.Event.DELETE);
        }

        var sceneStatus = queryParams.getSceneStatus();
        if (sceneStatus != null) {
            if (sceneStatus.equals(CsceneInstanceStatQueryParams.SceneStatus.CREATING)) {
                humanCondition.filterOrStart()
                        .orAndStart()
                        .andEqual(SceneModel::getEvent, SceneModel.Event.INITIATE)
                        .andNotEqual(SceneModel::getEventStatus, EventStatus.ABNORMAL)
                        .orAndEnd()
                        .orAndStart()
                        .andEqual(SceneModel::getEvent, SceneModel.Event.CREATE)
                        .andIn(SceneModel::getEventStatus, new Integer[]{
                                EventStatus.START, EventStatus.IN_PROGRESS
                        })
                        .orAndEnd()
                        .filterOrEnd();
            } else if (sceneStatus.equals(CsceneInstanceStatQueryParams.SceneStatus.QUEUE)) {
                humanCondition.filterEqual(SceneModel::getEvent, SceneModel.Event.DEFAULT);
            } else if (sceneStatus.equals(CsceneInstanceStatQueryParams.SceneStatus.RUNNING)) {
                humanCondition.filterOrStart()
                        .orAndStart()
                        .andEqual(SceneModel::getEvent, SceneModel.Event.CREATE)
                        .andEqual(SceneModel::getEventStatus, EventStatus.OVER)
                        .orAndEnd()
                        .orAndStart()
                        .andEqual(SceneModel::getEvent, SceneModel.Event.UNPAUSE)
                        .andEqual(SceneModel::getEventStatus, EventStatus.OVER)
                        .orAndEnd()
                        .orAndStart()
                        .andEqual(SceneModel::getEvent, SceneModel.Event.RESUME)
                        .andEqual(SceneModel::getEventStatus, EventStatus.OVER)
                        .orAndEnd()
                        .orAndStart()
                        .andIn(SceneModel::getEvent, new Integer[]{
                                SceneModel.Event.START, SceneModel.Event.STOP, SceneModel.Event.RESTART,
                                SceneModel.Event.SNAPSHOT, SceneModel.Event.MODIFY
                        })
                        .andNotEqual(SceneModel::getEventStatus, EventStatus.ABNORMAL)
                        .orAndEnd()
                        .filterOrEnd();
            } else if (sceneStatus.equals(CsceneInstanceStatQueryParams.SceneStatus.PAUSE)) {
                humanCondition.filterOrStart()
                        .orAndStart()
                        .andEqual(SceneModel::getEvent, SceneModel.Event.PAUSE)
                        .andNotEqual(SceneModel::getEventStatus, EventStatus.ABNORMAL)
                        .orAndEnd()
                        .orAndStart()
                        .andEqual(SceneModel::getEvent, SceneModel.Event.UNPAUSE)
                        .andIn(SceneModel::getEventStatus, new Integer[]{
                                EventStatus.START, EventStatus.IN_PROGRESS
                        })
                        .orAndEnd()
                        .filterOrEnd();
            } else if (sceneStatus.equals(CsceneInstanceStatQueryParams.SceneStatus.SUSPEND)) {
                humanCondition.filterOrStart()
                        .orAndStart()
                        .andEqual(SceneModel::getEvent, SceneModel.Event.SUSPEND)
                        .andNotEqual(SceneModel::getEventStatus, EventStatus.ABNORMAL)
                        .orAndEnd()
                        .orAndStart()
                        .andEqual(SceneModel::getEvent, SceneModel.Event.RESUME)
                        .andIn(SceneModel::getEventStatus, new Integer[]{
                                EventStatus.START, EventStatus.IN_PROGRESS
                        })
                        .orAndEnd()
                        .filterOrEnd();
            } else if (sceneStatus.equals(CsceneInstanceStatQueryParams.SceneStatus.ERROR)) {
                humanCondition.filterEqual(SceneModel::getEventStatus, EventStatus.ABNORMAL);
            } else if (sceneStatus.equals(CsceneInstanceStatQueryParams.SceneStatus.DELETED)) {
                humanCondition.filterEqual(SceneModel::getEvent, SceneModel.Event.DELETE)
                        .filterNotEqual(SceneModel::getEventStatus, EventStatus.ABNORMAL);
            }
        }

        List<CsceneInstanceStat> data;
        int total;
        try {
            if (queryParams.getOrders() == null || queryParams.getOrders().length == 0) {
                
                humanCondition.setCurrentLinkName(null);
                humanCondition.orderBy("-pk");
            } else {
                
                humanCondition.orderBy(queryParams.getOrders());
            }
            data = csceneInstanceMapper.list(humanCondition, CsceneInstanceStat.class);
            
            if (CollectionUtils.isNotEmpty(data)) {
                data.forEach(s -> {
                    if (StringUtils.isNotEmpty(s.getReferenceMetaData())) {
                        try {
                            Map<String, Object> result = JsonCommon.parse(s.getReferenceMetaData(), Map.class);
                            result.put("room_name", s.getName());
                            s.setReferenceMetaData(JsonCommon.stringify(result));
                        } catch (Exception e) {
                            log.error("name parse error,csceneId is {},referenceMetaData is {}", s.getId(), s.getReferenceMetaData(), e);
                        }
                    }
                    if (SceneModel.Event.DELETE == s.getEvent() && EventStatus.OVER == s.getEventStatus()) {
                        
                        s.setSceneRunTime(s.getUpdateTime().toEpochSecond(ZoneOffset.UTC) - s.getCreateTime().toEpochSecond(ZoneOffset.UTC));
                    } else {
                        
                        s.setSceneRunTime(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - s.getCreateTime().toEpochSecond(ZoneOffset.UTC));
                    }
                    
                    var status = SceneStatusUtil.getSceneStatus(s.getDeleted(), s.getEvent(), s.getEventStatus());
                    s.setSceneStatus(status.getSceneStatus());
                    s.setSceneStatusDesc(status.getSceneStatusDesc());
                    if (Boolean.TRUE.equals(s.getDeleted()) && SceneModel.Event.DELETE != s.getEvent()) {
                        s.setEvent(SceneModel.Event.DELETE);
                        s.setEventStatus(EventStatus.START);
                    }
                });
            }
            total = csceneInstanceMapper.total(humanCondition);
        } catch (Exception e) {
            log.error("Service list error: {}", e.getMessage(), e);
            return new ListObjServiceResult<>(null, BaseErrorCode.ERROR, e.getMessage());
        }
        if (CollectionUtils.isNotEmpty(data)) {
            List<String> sceneIds = data.stream().map(d -> d.getId()).collect(Collectors.toList());

            var humanCondition2 = new HumanCondition()
                    .link(CsceneInstanceModel.class, SceneModel.class, SceneElementModel.class,
                            CloudPluginSceneElementModel.class)
                    .filterLink(CsceneInstanceModel.class, CsceneInstanceModel::getScene, SceneModel.class,
                            SceneModel::getId)
                    .filterLink(SceneModel.class, SceneModel::getId, SceneElementModel.class,
                            SceneElementModel::getScene)
                    .filterLink(SceneModel.class, SceneModel::getId, CloudPluginSceneElementModel.class,
                            CloudPluginSceneElementModel::getScene)
                    .filterLink(SceneElementModel.class, SceneElementModel::getNodeId, CloudPluginSceneElementModel.class,
                            CloudPluginSceneElementModel::getSceneElement)
                    .currentLink(CsceneInstanceModel.class)
                    .filterIn(CsceneInstanceModel::getId, sceneIds.toArray(new String[]{}))
                    .groupBy(CsceneInstanceModel::getId)
                    .selectAs("pk", "pk")
                    .selectAs(CsceneInstanceModel::getId, CsceneInstanceStat::getId)
                    .currentLink(SceneElementModel.class)
                    .selectAs(Sum.f(SceneElementModel::getCpu), CsceneInstanceStat::getCpu)
                    .selectAs(Sum.f(SceneElementModel::getRam), CsceneInstanceStat::getRam)
                    .selectAs(Sum.f(SceneElementModel::getDisk), CsceneInstanceStat::getDisk)
                    .currentLink(CloudPluginSceneElementModel.class)
                    .selectAs(Count.f(CloudPluginSceneElementModel::getVirtualType)
                                    .calcEqual(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE),
                            CsceneInstanceStat::getVmCount)
                    .selectAs(Count.f(CloudPluginSceneElementModel::getVirtualType)
                                    .calcEqual(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER),
                            CsceneInstanceStat::getContainerCount)
                    .selectAs(Count.f(CloudPluginSceneElementModel::getVirtualType)
                                    .calcEqual(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE),
                            CsceneInstanceStat::getEntityCount)
                    .selectAs(Count.f(CloudPluginSceneElementModel::getVirtualType)
                                    .calcEqual(BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS),
                            CsceneInstanceStat::getDynamipsCount);
            var dataInfos = csceneInstanceMapper.list(humanCondition2, CsceneInstanceStat.class);
            if (CollectionUtils.isNotEmpty(dataInfos)) {
                Map<String, CsceneInstanceStat> infos = new HashMap<>();
                for (var dataInfo : dataInfos) {
                    infos.put(dataInfo.getId(), dataInfo);
                }
                for (var d : data) {
                    var info = infos.get(d.getId());
                    if (info != null) {
                        d.setCpu(info.getCpu());
                        d.setRam(info.getRam());
                        d.setDisk(info.getDisk());
                        d.setVmCount(info.getVmCount());
                        d.setContainerCount(info.getContainerCount());
                        d.setEntityCount(info.getEntityCount());
                        d.setDynamipsCount(info.getDynamipsCount());
                        if (info.getContainerCount() != null) {
                            d.setCpu(d.getCpu() + info.getContainerCount());
                            d.setRam(d.getRam() + info.getContainerCount() * 512);
                            d.setDisk(d.getDisk() + info.getContainerCount() * 10);
                        }
                    } else {
                        d.setCpu(0);
                        d.setRam(0);
                        d.setDisk(0);
                        d.setVmCount(0);
                        d.setContainerCount(0);
                        d.setEntityCount(0);
                        d.setDynamipsCount(0);
                    }
                }
            }
        }

        return new ListObjServiceResult<>(new ListObjResult<>(data, total));
    }

    
    public ServiceResult<Map<String, List<DeviceReferCsceneStat>>> statDeviceReferCscenes(String[] imageNames) {
        var deviceReferCondition = new HumanCondition()
                .link(CsceneModel.class, SceneConfigModel.class, CloudPluginSceneConfigElementModel.class)
                .filterLink(CsceneModel.class, CsceneModel::getSceneConfig,
                        SceneConfigModel.class, SceneConfigModel::getId)
                .filterLink(SceneConfigModel.class, SceneConfigModel::getId,
                        CloudPluginSceneConfigElementModel.class, CloudPluginSceneConfigElementModel::getScene)
                .currentLink(CsceneModel.class)
                .filterEqual(CsceneModel::getDeleted, false)
                .selectAs(CsceneModel::getId, DeviceReferCsceneStat::getCsceneId)
                .selectAs(CsceneModel::getCreateUser, DeviceReferCsceneStat::getCreateUser)
                .selectAs(CsceneModel::getCreateUsername, DeviceReferCsceneStat::getCreateUsername)
                .currentLink(SceneConfigModel.class)
                .filterEqual(SceneConfigModel::getDeleted, false)
                .selectAs(SceneConfigModel::getName, DeviceReferCsceneStat::getName)
                .currentLink(CloudPluginSceneConfigElementModel.class)
                .filterIn(CloudPluginSceneConfigElementModel::getImage, imageNames)
                .selectAs(CloudPluginSceneConfigElementModel::getImage, DeviceReferCsceneStat::getReferenceImage)
                .distinct();

        List<DeviceReferCsceneStat> deviceReferCsceneStats = csceneMapper.list(deviceReferCondition,
                DeviceReferCsceneStat.class);

        Map<String, List<DeviceReferCsceneStat>> result = new HashMap<>();
        deviceReferCsceneStats.forEach(stat -> {
            var image = stat.getReferenceImage();
            var referList = result.getOrDefault(image, new ArrayList<>());
            referList.add(stat);
            result.put(image, referList);
        });

        return new ServiceResult<>(result);
    }

    
    public ServiceResult<Map<String, List<DeviceReferCsceneStat>>> statDeviceReferCscenesByPrefix(List<String> snapUuids) {
        var deviceReferCondition = new HumanCondition()
                .link(CsceneModel.class, SceneConfigModel.class, CloudPluginSceneConfigElementModel.class)
                .filterLink(CsceneModel.class, CsceneModel::getSceneConfig,
                        SceneConfigModel.class, SceneConfigModel::getId)
                .filterLink(SceneConfigModel.class, SceneConfigModel::getId,
                        CloudPluginSceneConfigElementModel.class, CloudPluginSceneConfigElementModel::getScene)
                .currentLink(CsceneModel.class)
                .filterEqual(CsceneModel::getDeleted, false)
                .selectAs(CsceneModel::getId, DeviceReferCsceneStat::getCsceneId)
                .selectAs(CsceneModel::getCreateUser, DeviceReferCsceneStat::getCreateUser)
                .selectAs(CsceneModel::getCreateUsername, DeviceReferCsceneStat::getCreateUsername)
                .currentLink(SceneConfigModel.class)
                .filterEqual(SceneConfigModel::getDeleted, false)
                .selectAs(SceneConfigModel::getName, DeviceReferCsceneStat::getName)
                .currentLink(CloudPluginSceneConfigElementModel.class);
        var orCondition = deviceReferCondition.filterOrStart();
        for (String snapUuid : snapUuids) {
            orCondition.orStartsWith(CloudPluginSceneConfigElementModel::getImage, snapUuid);
        }
        orCondition.filterOrEnd();
        deviceReferCondition.selectAs(CloudPluginSceneConfigElementModel::getImage, DeviceReferCsceneStat::getReferenceImage)
                .distinct();

        List<DeviceReferCsceneStat> deviceReferCsceneStats = csceneMapper.list(deviceReferCondition,
                DeviceReferCsceneStat.class);

        Map<String, List<DeviceReferCsceneStat>> result = new HashMap<>();
        deviceReferCsceneStats.forEach(stat -> {
            var image = stat.getReferenceImage();
            var referList = result.getOrDefault(image, new ArrayList<>());
            referList.add(stat);
            result.put(image, referList);
        });

        return new ServiceResult<>(result);
    }

    
    public ServiceResult<List<DeviceReferCsceneStat>> statVirtualUsbReferCscenes(String uuid) {
        var referCondition = new HumanCondition()
                .link(CsceneModel.class, SceneConfigModel.class, SceneConfigElementModel.class, CloudPluginSceneConfigElementModel.class)
                .filterLink(CsceneModel.class, CsceneModel::getSceneConfig,
                        SceneConfigModel.class, SceneConfigModel::getId)
                .filterLink(SceneConfigModel.class, SceneConfigModel::getId,
                        SceneConfigElementModel.class, SceneConfigElementModel::getScene)
                .filterLink(SceneConfigModel.class, SceneConfigModel::getId,
                        CloudPluginSceneConfigElementModel.class, CloudPluginSceneConfigElementModel::getScene)
                .filterLink(SceneConfigElementModel.class, SceneConfigElementModel::getNodeId,
                        CloudPluginSceneConfigElementModel.class, CloudPluginSceneConfigElementModel::getSceneElement)
                .currentLink(CsceneModel.class)
                .filterEqual(CsceneModel::getDeleted, false)
                .selectAs(CsceneModel::getId, DeviceReferCsceneStat::getCsceneId)
                .selectAs(CsceneModel::getCreateUser, DeviceReferCsceneStat::getCreateUser)
                .selectAs(CsceneModel::getCreateUsername, DeviceReferCsceneStat::getCreateUsername)
                .currentLink(SceneConfigModel.class)
                .filterEqual(SceneConfigModel::getDeleted, false)
                .selectAs(SceneConfigModel::getName, DeviceReferCsceneStat::getName)
                .currentLink(CloudPluginSceneConfigElementModel.class)
                .filterContains(CloudPluginSceneConfigElementModel::getVirtualUsbs, uuid)
                .distinct();
        List<DeviceReferCsceneStat> deviceReferCsceneStats = csceneInstanceMapper.list(referCondition,
                DeviceReferCsceneStat.class);
        return new ServiceResult<>(deviceReferCsceneStats);
    }

    
    public ServiceResult<List<DeviceReferCsceneStat>> statVirtualUsbUsedCscenes(String uuid) {
        var referCondition = new HumanCondition()
                .link(CsceneInstanceModel.class, SceneModel.class, CloudPluginSceneElementModel.class, SceneElementModel.class)
                .filterLink(CsceneInstanceModel.class, CsceneInstanceModel::getScene,
                        SceneModel.class, SceneModel::getId)
                .filterLink(SceneModel.class, SceneModel::getId,
                        SceneElementModel.class, SceneElementModel::getScene)
                .filterLink(SceneModel.class, SceneModel::getId,
                        CloudPluginSceneElementModel.class, CloudPluginSceneElementModel::getScene)
                .filterLink(SceneElementModel.class, SceneElementModel::getNodeId,
                        CloudPluginSceneElementModel.class, CloudPluginSceneElementModel::getSceneElement)
                .currentLink(CsceneInstanceModel.class)
                .filterEqual(CsceneInstanceModel::getDeleted, false)
                .selectAs(CsceneInstanceModel::getId, DeviceReferCsceneInstanceStat::getCsceneId)
                .selectAs(CsceneInstanceModel::getCreateUser, DeviceReferCsceneInstanceStat::getCreateUser)
                .selectAs(CsceneInstanceModel::getCreateUsername, DeviceReferCsceneInstanceStat::getCreateUsername)
                .currentLink(SceneModel.class)
                .filterEqual(SceneModel::getDeleted, false)
                .selectAs(SceneModel::getName, DeviceReferCsceneInstanceStat::getName)
                .currentLink(CloudPluginSceneElementModel.class)
                .filterContains(CloudPluginSceneElementModel::getVirtualUsbs, uuid)
                .distinct();
        List<DeviceReferCsceneStat> deviceReferCsceneStats = csceneInstanceMapper.list(referCondition,
                DeviceReferCsceneStat.class);
        return new ServiceResult<>(deviceReferCsceneStats);
    }

    
    public ServiceResult<List<DeviceReferCsceneStat>> statEntityUsbReferCscenes(String uuid) {
        var referCondition = new HumanCondition()
                .link(CsceneModel.class, SceneConfigModel.class, SceneConfigElementModel.class, CloudPluginSceneConfigElementModel.class)
                .filterLink(CsceneModel.class, CsceneModel::getSceneConfig,
                        SceneConfigModel.class, SceneConfigModel::getId)
                .filterLink(SceneConfigModel.class, SceneConfigModel::getId,
                        SceneConfigElementModel.class, SceneConfigElementModel::getScene)
                .filterLink(SceneConfigModel.class, SceneConfigModel::getId,
                        CloudPluginSceneConfigElementModel.class, CloudPluginSceneConfigElementModel::getScene)
                .filterLink(SceneConfigElementModel.class, SceneConfigElementModel::getNodeId,
                        CloudPluginSceneConfigElementModel.class, CloudPluginSceneConfigElementModel::getSceneElement)
                .currentLink(CsceneModel.class)
                .filterEqual(CsceneModel::getDeleted, false)
                .selectAs(CsceneModel::getId, DeviceReferCsceneStat::getCsceneId)
                .selectAs(CsceneModel::getCreateUser, DeviceReferCsceneStat::getCreateUser)
                .selectAs(CsceneModel::getCreateUsername, DeviceReferCsceneStat::getCreateUsername)
                .currentLink(SceneConfigModel.class)
                .filterEqual(SceneConfigModel::getDeleted, false)
                .selectAs(SceneConfigModel::getName, DeviceReferCsceneStat::getName)
                .currentLink(CloudPluginSceneConfigElementModel.class)
                .filterContains(CloudPluginSceneConfigElementModel::getEntityUsbs, uuid)
                .distinct();
        List<DeviceReferCsceneStat> deviceReferCsceneStats = csceneInstanceMapper.list(referCondition,
                DeviceReferCsceneStat.class);
        return new ServiceResult<>(deviceReferCsceneStats);
    }


    
    public ServiceResult<List<DeviceReferCsceneStat>> statEntityUsbUsedCscenes(String uuid) {
        var referCondition = new HumanCondition()
                .link(CsceneInstanceModel.class, SceneModel.class, CloudPluginSceneElementModel.class, SceneElementModel.class)
                .filterLink(CsceneInstanceModel.class, CsceneInstanceModel::getScene,
                        SceneModel.class, SceneModel::getId)
                .filterLink(SceneModel.class, SceneModel::getId,
                        SceneElementModel.class, SceneElementModel::getScene)
                .filterLink(SceneModel.class, SceneModel::getId,
                        CloudPluginSceneElementModel.class, CloudPluginSceneElementModel::getScene)
                .filterLink(SceneElementModel.class, SceneElementModel::getNodeId,
                        CloudPluginSceneElementModel.class, CloudPluginSceneElementModel::getSceneElement)
                .currentLink(CsceneInstanceModel.class)
                .filterEqual(CsceneInstanceModel::getDeleted, false)
                .selectAs(CsceneInstanceModel::getId, DeviceReferCsceneInstanceStat::getCsceneId)
                .selectAs(CsceneInstanceModel::getCreateUser, DeviceReferCsceneInstanceStat::getCreateUser)
                .selectAs(CsceneInstanceModel::getCreateUsername, DeviceReferCsceneInstanceStat::getCreateUsername)
                .currentLink(SceneModel.class)
                .filterEqual(SceneModel::getDeleted, false)
                .selectAs(SceneModel::getName, DeviceReferCsceneInstanceStat::getName)
                .currentLink(CloudPluginSceneElementModel.class)
                .filterContains(CloudPluginSceneElementModel::getEntityUsbs, uuid)
                .distinct();
        List<DeviceReferCsceneStat> deviceReferCsceneStats = csceneInstanceMapper.list(referCondition,
                DeviceReferCsceneStat.class);
        return new ServiceResult<>(deviceReferCsceneStats);
    }


    
    public ServiceResult<List<String>> statISOReferCscenes(String isoTmpdirUuid) {
        var isoReferCondition = new HumanCondition()
                .link(CsceneModel.class, SceneConfigModel.class, CloudPluginSceneConfigElementModel.class)
                .filterLink(CsceneModel.class, CsceneModel::getSceneConfig,
                        SceneConfigModel.class, SceneConfigModel::getId)
                .filterLink(SceneConfigModel.class, SceneConfigModel::getId,
                        CloudPluginSceneConfigElementModel.class, CloudPluginSceneConfigElementModel::getScene)
                .currentLink(CsceneModel.class)
                .filterEqual(CsceneModel::getDeleted, false)
                .currentLink(SceneConfigModel.class)
                .filterEqual(SceneConfigModel::getDeleted, false)
                .selectAs(SceneConfigModel::getName, DeviceReferCsceneStat::getName)
                .currentLink(CloudPluginSceneConfigElementModel.class)
                .filterEqual(CloudPluginSceneConfigElementModel::getIsoTmpdirUuid, isoTmpdirUuid)
                .distinct();
        List<DeviceReferCsceneStat> isoReferCsceneStats = csceneMapper.list(isoReferCondition,
                DeviceReferCsceneStat.class);
        if (CollectionUtils.isEmpty(isoReferCsceneStats)) {
            return new ServiceResult<>(new ArrayList<>());
        }
        var result = isoReferCsceneStats.stream().map(DeviceReferCsceneStat::getName).collect(Collectors.toList());
        return new ServiceResult<>(result);
    }

    
    public ServiceResult<Map<String, List<DeviceReferCsceneInstanceStat>>> statDeviceReferCsceneInstances(
            String[] imageNames) {
        var deviceReferCondition = new HumanCondition()
                .link(CsceneInstanceModel.class, SceneModel.class, CloudPluginSceneElementModel.class, SceneElementModel.class)
                .filterLink(CsceneInstanceModel.class, CsceneInstanceModel::getScene,
                        SceneModel.class, SceneModel::getId)
                .filterLink(SceneModel.class, SceneModel::getId,
                        SceneElementModel.class, SceneElementModel::getScene)
                .filterLink(SceneModel.class, SceneModel::getId,
                        CloudPluginSceneElementModel.class, CloudPluginSceneElementModel::getScene)
                .filterLink(SceneElementModel.class, SceneElementModel::getNodeId,
                        CloudPluginSceneElementModel.class, CloudPluginSceneElementModel::getSceneElement)
                .currentLink(CsceneInstanceModel.class)
                .filterEqual(CsceneInstanceModel::getDeleted, false)
                .selectAs(CsceneInstanceModel::getId, DeviceReferCsceneInstanceStat::getCsceneId)
                .selectAs(CsceneInstanceModel::getCreateUser, DeviceReferCsceneInstanceStat::getCreateUser)
                .selectAs(CsceneInstanceModel::getCreateUsername, DeviceReferCsceneInstanceStat::getCreateUsername)
                .currentLink(SceneModel.class)
                .filterEqual(SceneModel::getDeleted, false)
                .selectAs(SceneModel::getName, DeviceReferCsceneInstanceStat::getName)
                .selectAs(SceneModel::getEvent, DeviceReferCsceneInstanceStat::getEvent)
                .selectAs(SceneModel::getEventStatus, DeviceReferCsceneInstanceStat::getEventStatus)
                .currentLink(CloudPluginSceneElementModel.class)
                .filterIn(CloudPluginSceneElementModel::getImage, imageNames)
                .selectAs(CloudPluginSceneElementModel::getImage, DeviceReferCsceneInstanceStat::getReferenceImage)
                .distinct();

        List<DeviceReferCsceneInstanceStat> deviceReferCsceneStats = csceneInstanceMapper.list(deviceReferCondition,
                DeviceReferCsceneInstanceStat.class);

        Map<String, List<DeviceReferCsceneInstanceStat>> result = new HashMap<>();
        deviceReferCsceneStats.forEach(stat -> {
            var image = stat.getReferenceImage();
            var referList = result.getOrDefault(image, new ArrayList<>());
            referList.add(stat);
            result.put(image, referList);
        });

        return new ServiceResult<>(result);
    }

    
    public ServiceResult<Map<String, List<DeviceReferCsceneInstanceStat>>> statDeviceReferCsceneInstancesByPrefix(
            List<String> snapUuids) {
        var deviceReferCondition = new HumanCondition()
                .link(CsceneInstanceModel.class, SceneModel.class, CloudPluginSceneElementModel.class)
                .filterLink(CsceneInstanceModel.class, CsceneInstanceModel::getScene,
                        SceneModel.class, SceneModel::getId)
                .filterLink(SceneModel.class, SceneModel::getId,
                        CloudPluginSceneElementModel.class, CloudPluginSceneElementModel::getScene)
                .currentLink(CsceneInstanceModel.class)
                .filterEqual(CsceneInstanceModel::getDeleted, false)
                .selectAs(CsceneInstanceModel::getId, DeviceReferCsceneInstanceStat::getCsceneId)
                .selectAs(CsceneInstanceModel::getCreateUser, DeviceReferCsceneInstanceStat::getCreateUser)
                .selectAs(CsceneInstanceModel::getCreateUsername, DeviceReferCsceneInstanceStat::getCreateUsername)
                .currentLink(SceneModel.class)
                .filterEqual(SceneModel::getDeleted, false)
                .selectAs(SceneModel::getName, DeviceReferCsceneInstanceStat::getName)
                .selectAs(SceneModel::getEvent, DeviceReferCsceneInstanceStat::getEvent)
                .selectAs(SceneModel::getEventStatus, DeviceReferCsceneInstanceStat::getEventStatus)
                .currentLink(CloudPluginSceneElementModel.class);
        var orCondition = deviceReferCondition.filterOrStart();
        for (String snapUuid : snapUuids) {
            orCondition.orStartsWith(CloudPluginSceneElementModel::getImage, snapUuid);
        }
        orCondition.filterOrEnd();
        deviceReferCondition.selectAs(CloudPluginSceneElementModel::getImage, DeviceReferCsceneInstanceStat::getReferenceImage)
                .distinct();

        List<DeviceReferCsceneInstanceStat> deviceReferCsceneStats = csceneInstanceMapper.list(deviceReferCondition,
                DeviceReferCsceneInstanceStat.class);

        Map<String, List<DeviceReferCsceneInstanceStat>> result = new HashMap<>();
        deviceReferCsceneStats.forEach(stat -> {
            var image = stat.getReferenceImage();
            var referList = result.getOrDefault(image, new ArrayList<>());
            referList.add(stat);
            result.put(image, referList);
        });

        return new ServiceResult<>(result);
    }

    
    public CsceneInstanceCountActivitySceneBO countActivityScene() {
        CsceneInstanceCountActivitySceneModel model = csceneInstanceMapper.countActivityScene();
        return CsceneInstanceCountActivitySceneBO.builder().activitySceneTotal(model.getActivitySceneTotal()).total(model.getTotal()).build();
    }

    public int countSceneConfig() {
        return csceneMapper.total(new HumanCondition().filterEqual(CsceneModel::getDeleted, false));
    }

    
    public int countActivitySceneByTime(LocalDateTime startTime, LocalDateTime endTime) {
        Integer cnt = csceneInstanceMapper.countActivitySceneByTime(startTime, endTime);
        return Optional.ofNullable(cnt).orElse(0);
    }

    public void loadEvent(DeviceReferCsceneInstanceStat instanceStat, String csceneId) {
        CsceneInstanceModel latestCsceneInstance = csceneInstanceMapper.first(new HumanCondition()
                .filterEqual(CsceneInstanceModel::getCscene, csceneId)
                .filterEqual(CsceneInstanceModel::getDeleted, false)
                .orderBy(CsceneInstanceModel::getPk, true));
        if (latestCsceneInstance == null) {
            instanceStat.setEvent(CsceneBizConstant.Event.UNUSE);
            instanceStat.setEventStatus(CsceneBizConstant.EventStatus.UNUSE);
        } else {
            SceneModel model = sceneMapper.get(latestCsceneInstance.getScene());
            instanceStat.setEvent(model.getEvent());
            instanceStat.setEventStatus(model.getEventStatus());
            instanceStat.setCreateUser(latestCsceneInstance.getCreateUser());
            instanceStat.setCreateUsername(latestCsceneInstance.getCreateUsername());
        }
    }

    
    public ServiceResult<Map<String, Integer>> statSceneInstanceCount(String[] csceneIds, boolean includeFailedScene, boolean includeDeleteScene) {
        Map<String, Integer> result = new HashMap<>();
        for (String csceneId : csceneIds) {
            result.put(csceneId, 0);
        }

        var statCondition = new HumanCondition()
                .link(CsceneInstanceModel.class, SceneModel.class)
                .filterLink(CsceneInstanceModel.class, CsceneInstanceModel::getScene,
                        SceneModel.class, SceneModel::getId)
                .currentLink(CsceneInstanceModel.class)
                .filterIn(CsceneInstanceModel::getCscene, csceneIds)
                .groupBy(CsceneInstanceModel::getCscene)
                .selectAs(CsceneInstanceModel::getCscene, CsceneInstanceCountStat::getCsceneId)
                .selectAs(Count.f(CsceneInstanceModel::getCscene), CsceneInstanceCountStat::getInstanceCount);
        if (!includeDeleteScene) {
            statCondition.currentLink(SceneModel.class).filterEqual(SceneModel::getDeleted, false);
        }
        if (!includeFailedScene) {
            statCondition.currentLink(SceneModel.class)
                    .filterOrStart()
                    .orNotEqual(SceneModel::getEvent, SceneModel.Event.CREATE)
                    .orNotEqual(SceneModel::getEventStatus, EventStatus.ABNORMAL)
                    .filterOrEnd();
        }
        List<CsceneInstanceCountStat> csceneInstanceCountStats = csceneInstanceMapper.list(statCondition, CsceneInstanceCountStat.class);
        csceneInstanceCountStats.forEach(instanceStat -> {
            result.put(instanceStat.getCsceneId(), instanceStat.getInstanceCount());
        });
        return new ServiceResult<>(result);
    }

    
    public ServiceResult<Integer> statSceneInstanceCount(String csceneId, boolean includeFailedScene, boolean includeDeleteScene) {
        Map<String, Integer> result = this.statSceneInstanceCount(new String[]{ csceneId }, includeFailedScene, includeDeleteScene).getSource();
        return new ServiceResult<>(result.get(csceneId));
    }

    
    public ServiceResult<SceneStateInfo> getSceneStateInfo(String csceneId) {
        CsceneInstanceModel csceneInstanceModel = csceneInstanceMapper.get(csceneId);
        if (csceneInstanceModel == null) {
            log.error("error csceneId {}", csceneId);
            return new ServiceResult<>(new SceneStateInfo());
        }
        SceneModel sceneModel = sceneMapper.get(csceneInstanceModel.getScene());
        return new ServiceResult<>(new SceneStateInfo(sceneModel));
    }

    
    public ServiceResult<Map<String, CsceneNodeInfoBO>> getSceneInfoByNodeResourceName(List<String> resourceNames) {
        if (CollectionUtils.isEmpty(resourceNames)) {
            return new ServiceResult<>(new HashMap<>());
        }
        Map<String, List<String>> nodeEid2Name = new HashMap<>();
        Map<String, List<String>> sceneId2Name = new HashMap<>();
        this.parseNameInfos(resourceNames, nodeEid2Name, sceneId2Name);
        if (nodeEid2Name.isEmpty() && sceneId2Name.isEmpty()) {
            return new ServiceResult<>(new HashMap<>());
        }
        var elementInfos = this.getSceneInfoByElementEids(nodeEid2Name);
        var sceneInfos = this.getSceneInfoBySceneEids(sceneId2Name);
        var infos = CollectionUtils.union(elementInfos, sceneInfos);
        if (CollectionUtils.isEmpty(infos)) {
            return new ServiceResult<>(new HashMap<>());
        }
        Map<String, CsceneNodeInfoBO> result = new HashMap<>();
        infos.forEach(info -> {
            
            var status = SceneStatusUtil.getSceneStatus(info.getDeleted(), info.getEvent(), info.getEventStatus());
            info.setSceneStatus(status.getSceneStatus());
            info.setSceneStatusDesc(status.getSceneStatusDesc());
            result.put(info.getResourceName(), info);
        });
        return new ServiceResult<>(result);
    }

    private void parseNameInfos(List<String> resourceNames, Map<String, List<String>> nodeEid2Name, Map<String, List<String>> sceneId2Name) {
        var resourceNameUtilMap = BeanCommon.getBeanType(IResourceName.class);
        if (resourceNameUtilMap == null || resourceNameUtilMap.isEmpty()) {
            return;
        }
        for (String name : resourceNames) {
            resourceNameUtilMap.values().forEach(util -> {
                var parseResult = util.parse(name);
                if (parseResult.isSuccess()) {
                    RsourceNameParams params = parseResult.getResult();
                    if (params.getSceneId() != null) {
                        if (!sceneId2Name.containsKey(params.getSceneId())) {
                            sceneId2Name.put(params.getSceneId(), new ArrayList<>());
                        }
                        sceneId2Name.get(params.getSceneId()).add(name);
                    }
                    if (params.getNodeId() != null) {
                        if (!nodeEid2Name.containsKey(params.getNodeId())) {
                            nodeEid2Name.put(params.getNodeId(), new ArrayList<>());
                        }
                        nodeEid2Name.get(params.getNodeId()).add(name);
                    }
                }
            });
        }
    }

    private List<CsceneNodeInfoBO> getSceneInfoBySceneEids(Map<String, List<String>> sceneId2Name) {
        if (sceneId2Name == null || sceneId2Name.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> sceneIds = new ArrayList<>(sceneId2Name.keySet());
        var condition = new HumanCondition()
                .filterIn(CsceneInstanceModel::getScene, sceneIds.toArray())
                .selectAs(CsceneInstanceModel::getId, CsceneNodeInfoBO::getCsceneId)
                .selectAs(CsceneInstanceModel::getScene, CsceneNodeInfoBO::getInternalSceneEid)
                .selectAs(CsceneInstanceModel::getReferenceType, CsceneNodeInfoBO::getReferenceType)
                .selectAs(CsceneInstanceModel::getCreateUser, CsceneNodeInfoBO::getUserId)
                .selectAs(CsceneInstanceModel::getName, CsceneNodeInfoBO::getCsceneName)
                .selectAs(CsceneInstanceModel::getDeleted, CsceneNodeInfoBO::getDeleted);
        var result = csceneInstanceMapper.list(condition, CsceneNodeInfoBO.class);
        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<>();
        }
        return this.assembleInfos(result, sceneId2Name, CsceneNodeInfoBO::getInternalSceneEid);
    }

    private List<CsceneNodeInfoBO> getSceneInfoByElementEids(Map<String, List<String>> nodeEid2Name) {
        if (nodeEid2Name == null || nodeEid2Name.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> elementEids = new ArrayList<>(nodeEid2Name.keySet());
        var condition = new HumanCondition()
                .link(SceneElementModel.class, CsceneInstanceModel.class)
                .filterLink(SceneElementModel.class, SceneElementModel::getScene, CsceneInstanceModel.class, CsceneInstanceModel::getScene)
                .currentLink(SceneElementModel.class)
                .filterIn(SceneElementModel::getId, elementEids.toArray())
                .selectAs(SceneElementModel::getId, CsceneNodeInfoBO::getInternalNodeEid)
                .selectAs(SceneElementModel::getNodeId, CsceneNodeInfoBO::getNodeId)
                .selectAs(SceneElementModel::getName, CsceneNodeInfoBO::getNodeName)
                .currentLink(CsceneInstanceModel.class)
                .selectAs(CsceneInstanceModel::getId, CsceneNodeInfoBO::getCsceneId)
                .selectAs(CsceneInstanceModel::getScene, CsceneNodeInfoBO::getInternalSceneEid)
                .selectAs(CsceneInstanceModel::getReferenceType, CsceneNodeInfoBO::getReferenceType)
                .selectAs(CsceneInstanceModel::getCreateUser, CsceneNodeInfoBO::getUserId)
                .selectAs(CsceneInstanceModel::getName, CsceneNodeInfoBO::getCsceneName)
                .selectAs(CsceneInstanceModel::getDeleted, CsceneNodeInfoBO::getDeleted);
        var elementInfos = csceneInstanceMapper.list(condition, CsceneNodeInfoBO.class);
        if (CollectionUtils.isEmpty(elementInfos)) {
            return new ArrayList<>();
        }
        return this.assembleInfos(elementInfos, nodeEid2Name, CsceneNodeInfoBO::getInternalNodeEid);
    }

    private List<CsceneNodeInfoBO> assembleInfos(List<CsceneNodeInfoBO> infoBOS, Map<String, List<String>> nameMap, Function<CsceneNodeInfoBO, String> getFlag) {
        List<CsceneNodeInfoBO> result = new ArrayList<>();
        var sceneIds = infoBOS.stream()
                .map(p -> p.getInternalSceneEid())
                .filter(p -> StringUtils.isNotEmpty(p))
                .collect(Collectors.toList());
        Map<String, SceneModel> sceneMaps = new HashMap<>();
        if (CollectionUtils.isNotEmpty(sceneIds)) {
            var sceneModels = sceneMapper.list(new HumanCondition().filterIn(SceneModel::getId, sceneIds.toArray(new String[]{})));
            if (CollectionUtils.isNotEmpty(sceneModels)) {
                sceneModels.stream().forEach(s -> {
                    sceneMaps.put(s.getId(), s);
                });
            }
        }

        infoBOS.forEach(e -> {
            List<String> names = nameMap.get(getFlag.apply(e));
            if (CollectionUtils.isEmpty(names)) {
                return;
            }
            names.forEach(name -> {
                CsceneNodeInfoBO nodeInfoBO = new CsceneNodeInfoBO();
                BeanUtils.copyProperties(e, nodeInfoBO);
                nodeInfoBO.setResourceName(name);
                var scene = sceneMaps.get(nodeInfoBO.getInternalSceneEid());
                if (scene != null) {
                    nodeInfoBO.setEvent(scene.getEvent());
                    nodeInfoBO.setEventStatus(scene.getEventStatus());
                    nodeInfoBO.setProcessMessage(scene.getProcessMessage());
                }
                result.add(nodeInfoBO);
            });
        });
        return result;
    }

    public ServiceResult<Map<String, CsceneInstanceInfo>> getCsceneNameBySceneIds(List<String> sceneIds) {
        if (CollectionUtils.isEmpty(sceneIds)) {
            return new ServiceResult<>(new HashMap<>());
        }
        Map<String, CsceneInstanceInfo> result = new HashMap<>();
        var models = csceneInstanceMapper.list(new HumanCondition().filterIn(CsceneInstanceModel::getScene, sceneIds.toArray(new String[]{})));
        for (var model : models) {
            CsceneInstanceInfo info = new CsceneInstanceInfo();
            info.setName(model.getName());
            info.setCreateUser(model.getCreateUser());
            info.setId(model.getId());
            info.setCsceneId(model.getCscene());
            result.put(model.getScene(), info);
        }
        return new ServiceResult<>(result);
    }

    public ServiceResult<Set<String>> getDeletingSnapshots(List<String> snapshotResourceIds) {
        if (CollectionUtils.isEmpty(snapshotResourceIds)) {
            return new ServiceResult<>(new HashSet<>());
        }
        var list = csceneSnapshotMapper.list(new HumanCondition()
                .filterIn(CsceneSnapshotModel::getSnapshotResourceId, snapshotResourceIds.toArray(new String[]{}))
                .filterEqual(CsceneSnapshotModel::getDeleted, true)
        );
        if (CollectionUtils.isEmpty(list)) {
            return new ServiceResult<>(new HashSet<>());
        } else {
            Set<String> set = new HashSet<>();
            for (var c : list) {
                if (StringUtils.isNotEmpty(c.getSnapshotResourceId())) {
                    set.add(c.getSnapshotResourceId());
                }
            }
            return new ServiceResult<>(set);
        }
    }

    public ServiceResult<List<UserConnectionNumBO>> countUserConnections(List<String> users) {
        return cloudSceneService.countUserConnections(users);
    }

    public ServiceResult<List<NodeSessionBO>> getUserNodeSessions(String user, boolean monitor, boolean assistance) {
        var list = cloudSceneService.getUserNodeSessions(user, monitor, assistance).getSource();
        if (CollectionUtils.isEmpty(list)) {
            return new ServiceResult<>(new ArrayList<>());
        }
        Set<String> sceneIds = new HashSet<>();
        Set<String> sceneElementIds = new HashSet<>();
        list.forEach(l -> {
            sceneIds.add(l.getSceneId());
            sceneElementIds.add(l.getNodeId());
        });
        var nodes = sceneElementMapper.list(new HumanCondition()
                .filterIn(SceneElementModel::getScene, sceneIds.toArray())
                .filterIn(SceneElementModel::getNodeId, sceneElementIds.toArray()));
        if (CollectionUtils.isNotEmpty(nodes)) {
            Map<String, SceneElementModel> key2Model = new HashMap<>();
            nodes.forEach(m -> {
                key2Model.put(m.getScene() + "_" + m.getNodeId(), m);
            });
            for (var nodeSession : list) {
                String key = nodeSession.getSceneId() + "_" + nodeSession.getNodeId();
                if (key2Model.containsKey(key)) {
                    nodeSession.setNodeName(key2Model.get(key).getName());
                }
            }
        }
        var scenes = csceneInstanceMapper.list(new HumanCondition()
                .filterIn(CsceneInstanceModel::getScene, sceneIds.toArray())
        );
        if (CollectionUtils.isNotEmpty(scenes)) {
            Map<String, CsceneInstanceModel> key2Model = new HashMap<>();
            scenes.forEach(s -> {
                key2Model.put(s.getScene(), s);
            });
            for (var nodeSession : list) {
                var m = key2Model.get(nodeSession.getSceneId());
                if (m != null) {
                    nodeSession.setSceneId(m.getId());
                    nodeSession.setSceneName(m.getName());
                }
            }
        }
        return new ServiceResult<>(list);
    }

    public ServiceResult<List<CsceneInstanceModel>> getInstanceUseVlanId(Integer vlanId) {
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.link(CsceneInstanceModel.class, CloudPluginSceneElementModel.class)
                .filterLink(CsceneInstanceModel.class, CsceneInstanceModel::getScene, CloudPluginSceneElementModel.class, CloudPluginSceneElementModel::getScene)
                .currentLink(CsceneInstanceModel.class)
                .filterEqual(CsceneInstanceModel::getDeleted, false)
                .selectAs(CsceneInstanceModel::getName, CsceneInstanceModel::getName)
                .currentLink(CloudPluginSceneElementModel.class)
                .filterEqual(CloudPluginSceneElementModel::getVlanId, vlanId)
                .setLimit(1);
        CsceneInstanceMapper csceneInstanceMapper = BeanCommon.getBean(CsceneInstanceMapper.class);
        var result = csceneInstanceMapper.list(humanCondition);
        return new ServiceResult<>(result).asResult();
    }

}
