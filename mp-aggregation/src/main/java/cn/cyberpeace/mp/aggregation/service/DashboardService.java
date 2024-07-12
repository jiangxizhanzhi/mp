 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DashboardService.java.
 // File size 17581.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.service;

import cn.cyberpeace.mp.aggregation.SceneBusinessSummary;
import cn.cyberpeace.mp.aggregation.dto.dashboard.UserOnlineReportMonitorBo;
import cn.cyberpeace.mp.aggregation.response.DailyUserActivityResponse;
import cn.cyberpeace.mp.aggregation.response.ResourceStatisticsResponse;
import cn.cyberpeace.mp.aggregation.response.UserResourceResponse;
import cn.cyberpeace.mp.aggregation.response.UserSummarySceneResponse;
import cn.cyberpeace.mp.auth.bo.user.UserOnlineReportBo;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.request.user.UserOnlineRecordSummaryPageRequest;
import cn.cyberpeace.mp.auth.service.UserOnlineRecordService;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceCountActivitySceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceCountByReferenceTypeBO;
import cn.cyberpeace.mp.cscene.core.common.UserCommon;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.DeviceService;
import cn.cyberpeace.mp.devopscluster.service.DevopsTargetService;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeSessionBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.UserConnectionNumBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName DashboardService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Service
@Slf4j
public class DashboardService {

    private DeviceService deviceService;

    private DevopsTargetService devopsTargetService;

    private CsceneInstanceService csceneInstanceService;

    private CsceneSpecialService csceneSpecialService;

    private ScenePluginCloudSceneService scenePluginCloudSceneService;

    private CsceneInstanceMapper csceneInstanceMapper;

    private CloudPluginSceneElementMapper cloudPluginSceneElementMapper;

    @Autowired
    private UserOnlineRecordService userOnlineRecordService;

    /**
     * DashboardService
     *
     * @param deviceService
     * @param csceneInstanceService
     * @param csceneSpecialService
     * @param csceneInstanceMapper
     * @param cloudPluginSceneElementMapper
     * @param scenePluginCloudSceneService
     * @param devopsTargetService
     */
    @Autowired
    public DashboardService(DeviceService deviceService,
                            CsceneInstanceService csceneInstanceService,
                            CsceneSpecialService csceneSpecialService,
                            CsceneInstanceMapper csceneInstanceMapper,
                            CloudPluginSceneElementMapper cloudPluginSceneElementMapper,
                            ScenePluginCloudSceneService scenePluginCloudSceneService,
                            DevopsTargetService devopsTargetService) {
        this.deviceService = deviceService;
        this.csceneInstanceService = csceneInstanceService;
        this.csceneSpecialService = csceneSpecialService;
        this.csceneInstanceMapper = csceneInstanceMapper;
        this.cloudPluginSceneElementMapper = cloudPluginSceneElementMapper;
        this.scenePluginCloudSceneService = scenePluginCloudSceneService;
        this.devopsTargetService = devopsTargetService;
    }

    /**
     * 最近一周用户活动轨迹
     *
     * @return
     */
    public DailyUserActivityResponse dailyUserActivity() {

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<LocalDate> daylist = new ArrayList<>();
        LocalDate today = LocalDate.now();
        daylist.add(today);
        for (int i = 1; i < 7; i++) {
            LocalDate d = today.minusDays(i);
            daylist.add(d);
        }
        Collections.reverse(daylist);

        List<String> list1 = new ArrayList<>();
        for (LocalDate date : daylist) {
            String format = df.format(date);
            list1.add(format);
        }
        String[] xAxis = list1.toArray(new String[0]);
        List<Integer> result = new ArrayList<>();
        for (LocalDate localDate : daylist) {
            Integer cnt = userOnlineRecordService.getOnlinedUserCntByPeriod(localDate.atStartOfDay(), localDate.plusDays(1).atStartOfDay());
            Integer apply = cnt;
            result.add(apply);
        }
        Integer[] userCount = result.toArray(new Integer[0]);

        // 每日活跃场景数量 todo,性能可优化
        List<Integer> list = new ArrayList<>();
        for (LocalDate day : daylist) {
            Integer countActivitySceneByTime = csceneSpecialService.countActivitySceneByTime(day.atStartOfDay(), day.plusDays(1).atStartOfDay());
            list.add(countActivitySceneByTime);
        }
        Integer[] sceneCount = list.toArray(new Integer[0]);

        DailyUserActivityResponse response = DailyUserActivityResponse.builder().xAxis(xAxis).userCount(userCount).sceneCount(sceneCount).build();
        return response;
    }

    /**
     * 仪表盘-资源统计
     *
     * @return
     */
    public ResourceStatisticsResponse resourceStatistics() {
        ResourceStatisticsResponse response = new ResourceStatisticsResponse();
        Map<String, Map<String, Object>> targetStatics = new HashMap<>();
        Map<String, Object> all = devopsTargetService.countTarget();
        // npc暂时只有1个
        all.put("npc", 1);
        targetStatics.put("all", all);
        int npc = all.get("npc") == null ? 0 : Integer.parseInt(all.get("npc").toString());
        int dynamips = all.get("dynamips") == null ? 0 : Integer.parseInt(all.get("dynamips").toString());
        all.put("other", npc + dynamips);
        targetStatics.put("entity", devopsTargetService.countEntityTarget());
        targetStatics.put("virtual", devopsTargetService.countVmTarget());
        response.setTargetStatics(targetStatics);
        List<CsceneInstanceCountByReferenceTypeBO> sceneStatics = csceneInstanceService.countGroupByReferenceType(Arrays.asList(SceneModel.Event.DELETE));
        response.setSceneStatics(sceneStatics);
        return response;
    }

    /**
     * 统计活动场景总数和场景占比
     *
     * @return
     */
    public UserSummarySceneResponse countActivityScene() {
        CsceneInstanceCountActivitySceneBO bo = csceneSpecialService.countActivityScene();

        UserSummarySceneResponse response = new UserSummarySceneResponse();
        response.setRunningSceneCount(bo.getActivitySceneTotal());
        response.setSceneConfigCount(csceneSpecialService.countSceneConfig());
        BigDecimal runningScenePercent = BigDecimal.ZERO;
        if (NumberUtils.INTEGER_ZERO.equals(bo.getTotal()) || Objects.isNull(bo.getActivitySceneTotal())) {
            runningScenePercent = BigDecimal.ZERO;
        } else {
            runningScenePercent = BigDecimal.valueOf(bo.getActivitySceneTotal()).divide(BigDecimal.valueOf(bo.getTotal()), 2, RoundingMode.DOWN).multiply(BigDecimal.valueOf(100));
        }
        response.setRunningScenePercent(runningScenePercent);

        List<CsceneInstanceCountByReferenceTypeBO> instanceCount = csceneInstanceService.countGroupByReferenceType(Arrays.asList(SceneModel.Event.DELETE));
        if (CollectionUtils.isNotEmpty(instanceCount)) {
            int total = 0;
            for (CsceneInstanceCountByReferenceTypeBO cnt : instanceCount) {
                total += (cnt.getTotal() == null ? 0 : cnt.getTotal());
            }
            response.setRunningSceneCount(total);
        }

        return response;
    }

    /**
     * countUserResourceResponse
     *
     * @return
     */
    public UserResourceResponse countUserResourceResponse() {
        UserResourceResponse userResource = UserResourceResponse
                .builder()
                .containerCount(0)
                .vmCount(0)
                .entityCount(0)
                .sceneCount(0)
                .dynamips(0)
                .build();
        UserModel user = UserCommon.getCurrentUser();
        if (user == null) {
            return userResource;
        }
        HumanCondition condition = new HumanCondition()
                .filterEqual(CsceneInstanceModel::getDeleted, false)
                .filterEqual(CsceneInstanceModel::getCreateUser, user.getId());
        List<CsceneInstanceModel> csceneInstances;
        try {
            csceneInstances = csceneInstanceMapper.list(condition);
        } catch (Exception e) {
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        }
        if (csceneInstances.isEmpty()) {
            return userResource;
        }

        userResource.setSceneCount(csceneInstances.size());
        List<String> sceneIds = new ArrayList<>();
        for (CsceneInstanceModel csceneInstance : csceneInstances) {
            if (!StringUtils.isEmpty(csceneInstance.getScene())) {
                String scene = csceneInstance.getScene();
                sceneIds.add(scene);
            }
        }
        String fieldName = BeanUtils.getBeanGetterFieldName(CloudPluginSceneElementModel::getVirtualType);
        Map<Object, Long> result = scenePluginCloudSceneService
                .groupCountElement(sceneIds, null, List.of(fieldName)).getSource().get(fieldName);
        userResource.setVmCount(result.getOrDefault(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE, 0L).intValue());
        userResource.setContainerCount(result.getOrDefault(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER, 0L).intValue());
        userResource.setEntityCount(result.getOrDefault(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE, 0L).intValue());
        userResource.setDynamips(result.getOrDefault(BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS, 0L).intValue());
        return userResource;
    }

    /**
     * groupCountResourceByRole
     *
     * @return
     */
    public ServiceResult<SceneBusinessSummary> groupCountResourceByRole() {
        HumanCondition condition = new HumanCondition()
                .filterEqual(CsceneInstanceModel::getDeleted, false);
        List<CsceneInstanceModel> csceneInstances = csceneInstanceMapper.list(condition);
        if (csceneInstances.isEmpty()) {
            return new ServiceResult<>(new SceneBusinessSummary());
        }
        List<String> sceneIds = new ArrayList<>();
        for (CsceneInstanceModel csceneInstance : csceneInstances) {
            if (!StringUtils.isEmpty(csceneInstance.getScene())) {
                String scene = csceneInstance.getScene();
                sceneIds.add(scene);
            }
        }
        String fieldName = BeanUtils.getBeanGetterFieldName(CloudPluginSceneElementModel::getRole);
        Map<Object, Long> result = scenePluginCloudSceneService
                .groupCountElement(sceneIds, null, List.of(fieldName)).getSource().get(fieldName);
        SceneBusinessSummary sceneBusinessSummary = new SceneBusinessSummary();
        sceneBusinessSummary.setFirewallCount(this.groupSummary(SceneBusinessSummary.firewallType(), result));
        sceneBusinessSummary.setNpcCount(this.groupSummary(SceneBusinessSummary.npcType(), result));
        sceneBusinessSummary.setSwitchCount(this.groupSummary(SceneBusinessSummary.switchType(), result));
        sceneBusinessSummary.setRouterCount(this.groupSummary(SceneBusinessSummary.routerType(), result));
        // todo 快速统计出各类虚拟资源的数量
        sceneBusinessSummary.setVmCount(this.countByVirtualTypeAndRole(sceneIds,
                List.of(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE),
                List.of(BaseCloudPluginSceneElementModel.Role.VIRTUAL_TERMINAL)));
        sceneBusinessSummary.setContainerCount(this.countByVirtualTypeAndRole(sceneIds,
                List.of(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER),
                List.of(BaseCloudPluginSceneElementModel.Role.VIRTUAL_TERMINAL)));
        return new ServiceResult<>(sceneBusinessSummary);
    }

    /**
     * 整合在线报告和节点监控
     *
     * @param pageRequest
     * @return
     */
    public PageInfo<UserOnlineReportMonitorBo> getUserOnlineMonitorRecords(UserOnlineRecordSummaryPageRequest pageRequest) {
        PageInfo<UserOnlineReportBo> pageInfo = userOnlineRecordService.recordSummaryPage(pageRequest);
        List<UserOnlineReportBo> userOnlineReportBoList = pageInfo.getList();

        //List<String> onlinedUserIds = userOnlineReportDtoList.stream().filter(dto -> Objects.equals(dto.getIsOnline(), 1)).map(UserOnlineReportDto::getUser).collect(Collectors.toList());
        List<String> onlinedUserIds = new ArrayList<>();
        for (UserOnlineReportBo onlineReportBo : userOnlineReportBoList) {
            String user = onlineReportBo.getUser();
            onlinedUserIds.add(user);
        }
        List<UserConnectionNumBO> userConnectionNumBOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(onlinedUserIds)) {
            ServiceResult<List<UserConnectionNumBO>> result = csceneSpecialService.countUserConnections(onlinedUserIds);
            userConnectionNumBOList = result.getSource();
        }

        List<UserOnlineReportMonitorBo> monitorDtoList = new ArrayList<>();
        for (UserOnlineReportBo userOnlineReportBo : userOnlineReportBoList) {
            UserOnlineReportMonitorBo monitorDto = new UserOnlineReportMonitorBo();
            BeanUtils.copyProperties(userOnlineReportBo, monitorDto);

            monitorDto.setNodeSessionCnt(0);
            monitorDto.setHasNodeSession(false);

            if (CollectionUtils.isNotEmpty(userConnectionNumBOList)) {
                Optional<UserConnectionNumBO> boOptional = Optional.empty();
                for (UserConnectionNumBO bo : userConnectionNumBOList) {
                    if (Objects.equals(bo.getUser(), userOnlineReportBo.getUser())) {
                        boOptional = Optional.of(bo);
                        break;
                    }
                }
                if (boOptional.isPresent() && boOptional.get().getConnectionNum() > 0) {
                    monitorDto.setNodeSessionCnt(boOptional.get().getConnectionNum());
                    monitorDto.setHasNodeSession(true);
                }
            }

            monitorDtoList.add(monitorDto);
        }

        PageInfo<UserOnlineReportMonitorBo> newPageInfo = new PageInfo();
        BeanUtils.copyProperties(pageInfo, newPageInfo);
        newPageInfo.setList(monitorDtoList);
        return newPageInfo;
    }

    /**
     * 查询用户会话
     *
     * @param userId
     * @return
     */
    public ServiceResult<List<NodeSessionBO>> getUserNodeSessions(String userId) {
        ServiceResult<List<NodeSessionBO>> serviceResult = csceneSpecialService.getUserNodeSessions(userId, true, false);
        return serviceResult.asResult();
    }

    /**
     * countByVirtualTypeAndRole
     *
     * @param sceneIds
     * @param virtualTypes
     * @param roles
     * @return int
     */
    private int countByVirtualTypeAndRole(List<String> sceneIds, List<Integer> virtualTypes, List<Integer> roles) {
        HumanCondition condition = new HumanCondition()
                .filterIn(CloudPluginSceneElementModel::getScene, sceneIds.toArray())
                .filterIn(CloudPluginSceneElementModel::getVirtualType, virtualTypes.toArray())
                .filterIn(CloudPluginSceneElementModel::getRole, roles.toArray());
        List<CloudPluginSceneElementModel> cloudElements = cloudPluginSceneElementMapper.list(condition);
        return cloudElements.size();
    }

    /**
     * groupSummary
     *
     * @param roleType
     * @param result
     * @return int
     */
    private int groupSummary(List<Integer> roleType, Map<Object, Long> result) {
        int sum = 0;
        for (Integer role : roleType) {
            if (result.containsKey(role)) {
                sum += result.get(role);
            }
        }
        return sum;
    }

}
