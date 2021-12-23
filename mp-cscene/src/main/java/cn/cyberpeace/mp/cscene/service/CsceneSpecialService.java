package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Count;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Sum;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceCountActivitySceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceStat;
import cn.cyberpeace.mp.cscene.bo.CsceneStat;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneInstanceStat;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneStat;
import cn.cyberpeace.mp.cscene.core.common.UserCommon;
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
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CsceneSpecialService {

    @Autowired
    private CsceneMapper csceneMapper;

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
                .selectAs("pk","pk")
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
}
