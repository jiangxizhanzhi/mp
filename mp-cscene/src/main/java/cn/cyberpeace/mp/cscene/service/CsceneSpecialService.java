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
import cn.cyberpeace.mp.cscene.dto.scene.SceneStateInfo;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceCountActivitySceneModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneMapper;
import cn.cyberpeace.mp.cscene.service.param.CsceneInstanceStatQueryParams;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.model.SceneConfigElementModel;
import cn.cyberpeace.mp.scene.model.SceneConfigModel;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.scene.repository.SceneMapper;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.util.resourcename.IResourceName;
import cn.cyberpeace.mp.sceneplugincloud.util.resourcename.RsourceNameParams;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private CsceneInstanceMapper csceneInstanceMapper;

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

    /**
     * 统计场景模板中各类型节点数量，测试场景数量
     * @param ids 场景模板id列表
     * @return 统计结果
     */
    public ServiceResult<List<CsceneStat>> statCscenes(String[] ids) {
        if (ids == null || ids.length == 0) {
            return new ServiceResult<>(new ArrayList<>());
        }

        Map<String, CsceneStat> statMap = new HashMap<>();
        for (String id : ids) {
            var csceneStat = new CsceneStat(id, 0, 0, 0, 0, 0);
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

    /**
     * 场景实例数据
     * @param queryParams 查询参数
     * @return 统计结果
     */
    public ListObjServiceResult<CsceneInstanceStat> listCsceneInstanceStats(CsceneInstanceStatQueryParams queryParams) {
        var humanCondition = new HumanCondition()
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
                .currentLink(SceneModel.class)
                .selectAs(SceneModel::getEvent, CsceneInstanceStat::getEvent)
                .selectAs(SceneModel::getEventStatus, CsceneInstanceStat::getEventStatus)
                .selectAs(SceneModel::getProcessMessage, CsceneInstanceStat::getEventMessage)
                .selectAs(SceneModel::getCreateConsumeSeconds, CsceneInstanceStat::getCreateConsumeSeconds)
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
                        CsceneInstanceStat::getEntityCount);

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
                // 排序,默认使用时间倒序排列
                humanCondition.setCurrentLinkName(null);
                humanCondition.orderBy("-pk");
            } else {
                // todo 可根据需要去实现按cpu数量等排列 暂时只支持createTime、updateTime
                humanCondition.orderBy(queryParams.getOrders());
            }
            data = csceneInstanceMapper.list(humanCondition, CsceneInstanceStat.class);
            // 场景运行时长
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
                        // 已删除场景的运行时间=删除时刻 - 创建时刻
                        s.setSceneRunTime(s.getUpdateTime().toEpochSecond(ZoneOffset.UTC) - s.getCreateTime().toEpochSecond(ZoneOffset.UTC));
                    } else {
                        // 未删除场景的运行时间=当前时刻 - 创建时刻
                        s.setSceneRunTime(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - s.getCreateTime().toEpochSecond(ZoneOffset.UTC));
                    }
                });
            }
            total = csceneInstanceMapper.total(humanCondition);
        } catch (Exception e) {
            log.error("Service list error: {}", e.getMessage(), e);
            return new ListObjServiceResult<>(null, BaseErrorCode.ERROR, e.getMessage());
        }

        return new ListObjServiceResult<>(new ListObjResult<>(data, total));
    }

    /**
     * 统计靶标名称 对应 引用了靶标的场景模板
     * @param imageNames 靶标名称
     * @return 靶标名称 对应 引用了靶标的场景模板
     */
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

    /**
     * 统计靶标名称 对应 引用了靶标的场景实例
     * @param imageNames 靶标名称
     * @return 靶标名称 对应 引用了靶标的场景实例
     */
    public ServiceResult<Map<String, List<DeviceReferCsceneInstanceStat>>> statDeviceReferCsceneInstances(
            String[] imageNames) {
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

    /**
     * 统计活动场景总数和场景总数
     * @return
     */
    public CsceneInstanceCountActivitySceneBO countActivityScene() {
        CsceneInstanceCountActivitySceneModel model = csceneInstanceMapper.countActivityScene();
        return CsceneInstanceCountActivitySceneBO.builder().activitySceneTotal(model.getActivitySceneTotal()).total(model.getTotal()).build();
    }

    /**
     * 统计一段时间区间内活跃过的场景数(活跃数 = 区间内启动过的(不管有没有关闭) + 之前启动的而且未关闭的)
     * @param startTime
     * @param endTime
     * @return
     */
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

    /**
     * 根据场景模板查询启动的实例数量
     * @param csceneIds
     * @param includeFailedScene 是否包括启动失败的场景
     * @param includeDeleteScene 是否包括已删除的场景
     * @return
     */
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

    /**
     * 根据场景模板查询启动的实例数量
     * @param csceneId
     * @param includeFailedScene 是否包括启动失败的场景
     * @param includeDeleteScene 是否包括已删除的场景
     * @return
     */
    public ServiceResult<Integer> statSceneInstanceCount(String csceneId, boolean includeFailedScene, boolean includeDeleteScene) {
        Map<String, Integer> result = this.statSceneInstanceCount(new String[]{ csceneId }, includeFailedScene, includeDeleteScene).getSource();
        return new ServiceResult<>(result.get(csceneId));
    }

    /**
     * 获取实例运行的状态信息
     * @param csceneId
     * @return
     */
    public ServiceResult<SceneStateInfo> getSceneStateInfo(String csceneId) {
        CsceneInstanceModel csceneInstanceModel = csceneInstanceMapper.get(csceneId);
        if (csceneInstanceModel == null) {
            log.error("error csceneId {}", csceneId);
            return new ServiceResult<>(new SceneStateInfo());
        }
        SceneModel sceneModel = sceneMapper.get(csceneInstanceModel.getScene());
        return new ServiceResult<>(new SceneStateInfo(sceneModel));
    }

    /**
     * 根据资源名称反查节点和场景信息
     * @param resourceNames
     * @return
     */
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
        var result = infos.stream().collect(Collectors.toMap(CsceneNodeInfoBO::getResourceName, s -> s));
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
                .selectAs(CsceneInstanceModel::getName, CsceneNodeInfoBO::getCsceneName);
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
                .selectAs(CsceneInstanceModel::getName, CsceneNodeInfoBO::getCsceneName);
        var elementInfos = csceneInstanceMapper.list(condition, CsceneNodeInfoBO.class);
        if (CollectionUtils.isEmpty(elementInfos)) {
            return new ArrayList<>();
        }
        return this.assembleInfos(elementInfos, nodeEid2Name, CsceneNodeInfoBO::getInternalNodeEid);
    }

    private List<CsceneNodeInfoBO> assembleInfos(List<CsceneNodeInfoBO> infoBOS, Map<String, List<String>> nameMap, Function<CsceneNodeInfoBO, String> getFlag) {
        List<CsceneNodeInfoBO> result = new ArrayList<>();
        infoBOS.forEach(e -> {
            List<String> names = nameMap.get(getFlag.apply(e));
            if (CollectionUtils.isEmpty(names)) {
                return;
            }
            names.forEach(name -> {
                CsceneNodeInfoBO nodeInfoBO = new CsceneNodeInfoBO();
                BeanUtils.copyProperties(e, nodeInfoBO);
                nodeInfoBO.setResourceName(name);
                result.add(nodeInfoBO);
            });
        });
        return result;
    }

}
