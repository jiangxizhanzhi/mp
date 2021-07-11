package cn.cyberpeace.mp.aggregation.service;

import cn.cyberpeace.mp.aggregation.SceneBusinessSummary;
import cn.cyberpeace.mp.aggregation.dto.dashboard.UserOnlineReportMonitorBo;
import cn.cyberpeace.mp.aggregation.response.DailyUserActivityResponse;
import cn.cyberpeace.mp.aggregation.response.ResourceStatisticsResponse;
import cn.cyberpeace.mp.aggregation.response.UserResourceResponse;
import cn.cyberpeace.mp.aggregation.response.UserSummarySceneResponse;
import cn.cyberpeace.mp.auth.bo.user.UserOnlineReportBo;
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
import cn.hutool.core.collection.CollectionUtil;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


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

    
    public DailyUserActivityResponse dailyUserActivity() {

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<LocalDate> daylist = new ArrayList<>();
        LocalDate today = LocalDate.now();
        daylist.add(today);
        for (int i = 1; i < 7; i++) {
            LocalDate d = today.minusDays(i);
            daylist.add(d);
        }
        CollectionUtil.reverse(daylist);

        String[] xAxis = daylist.stream().map(day -> df.format(day)).toArray(String[]::new);
        Integer[] userCount = daylist.stream().map(day -> {
            Integer cnt = userOnlineRecordService.getOnlinedUserCntByPeriod(day.atStartOfDay(), day.plusDays(1).atStartOfDay());
            return cnt;
        }).toArray(Integer[]::new);

        
        Integer[] sceneCount = daylist.stream().map(day -> csceneSpecialService.countActivitySceneByTime(day.atStartOfDay(), day.plusDays(1).atStartOfDay())).toArray(Integer[]::new);

        DailyUserActivityResponse response = DailyUserActivityResponse.builder().xAxis(xAxis).userCount(userCount).sceneCount(sceneCount).build();
        return response;
    }

    
    public ResourceStatisticsResponse resourceStatistics() {
        ResourceStatisticsResponse response = new ResourceStatisticsResponse();
        Map<String, Map<String, Object>> targetStatics = new HashMap<>();
        var all = devopsTargetService.countTarget();
        
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

        var instanceCount = csceneInstanceService.countGroupByReferenceType(Arrays.asList(SceneModel.Event.DELETE));
        if (CollectionUtils.isNotEmpty(instanceCount)) {
            int total = 0;
            for (var cnt : instanceCount) {
                total += (cnt.getTotal() == null ? 0 : cnt.getTotal());
            }
            response.setRunningSceneCount(total);
        }

        return response;
    }

    public UserResourceResponse countUserResourceResponse() {
        var userResource = UserResourceResponse
                .builder()
                .containerCount(0)
                .vmCount(0)
                .entityCount(0)
                .sceneCount(0)
                .dynamips(0)
                .build();
        var user = UserCommon.getCurrentUser();
        if (user == null) {
            return userResource;
        }
        var condition = new HumanCondition()
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
        List<String> sceneIds = csceneInstances.stream()
                .filter(csceneInstance -> !StringUtils.isEmpty(csceneInstance.getScene()))
                .map(csceneInstance -> csceneInstance.getScene())
                .collect(Collectors.toList());
        var fieldName = BeanUtils.getBeanGetterFieldName(CloudPluginSceneElementModel::getVirtualType);
        var result = scenePluginCloudSceneService
                .groupCountElement(sceneIds, null, List.of(fieldName)).getSource().get(fieldName);
        userResource.setVmCount(result.getOrDefault(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE, 0L).intValue());
        userResource.setContainerCount(result.getOrDefault(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER, 0L).intValue());
        userResource.setEntityCount(result.getOrDefault(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE, 0L).intValue());
        userResource.setDynamips(result.getOrDefault(BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS, 0L).intValue());
        return userResource;
    }

    public ServiceResult<SceneBusinessSummary> groupCountResourceByRole() {
        var condition = new HumanCondition()
                .filterEqual(CsceneInstanceModel::getDeleted, false);
        List<CsceneInstanceModel> csceneInstances = csceneInstanceMapper.list(condition);
        if (csceneInstances.isEmpty()) {
            return new ServiceResult<>(new SceneBusinessSummary());
        }
        List<String> sceneIds = csceneInstances.stream()
                .filter(csceneInstance -> !StringUtils.isEmpty(csceneInstance.getScene()))
                .map(csceneInstance -> csceneInstance.getScene())
                .collect(Collectors.toList());
        var fieldName = BeanUtils.getBeanGetterFieldName(CloudPluginSceneElementModel::getRole);
        var result = scenePluginCloudSceneService
                .groupCountElement(sceneIds, null, List.of(fieldName)).getSource().get(fieldName);
        SceneBusinessSummary sceneBusinessSummary = new SceneBusinessSummary();
        sceneBusinessSummary.setFirewallCount(this.groupSummary(SceneBusinessSummary.firewallType(), result));
        sceneBusinessSummary.setNpcCount(this.groupSummary(SceneBusinessSummary.npcType(), result));
        sceneBusinessSummary.setSwitchCount(this.groupSummary(SceneBusinessSummary.switchType(), result));
        sceneBusinessSummary.setRouterCount(this.groupSummary(SceneBusinessSummary.routerType(), result));
        
        sceneBusinessSummary.setVmCount(this.countByVirtualTypeAndRole(sceneIds,
                List.of(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE),
                List.of(BaseCloudPluginSceneElementModel.Role.VIRTUAL_TERMINAL)));
        sceneBusinessSummary.setContainerCount(this.countByVirtualTypeAndRole(sceneIds,
                List.of(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER),
                List.of(BaseCloudPluginSceneElementModel.Role.VIRTUAL_TERMINAL)));
        return new ServiceResult<>(sceneBusinessSummary);
    }

    
    public PageInfo<UserOnlineReportMonitorBo> getUserOnlineMonitorRecords(UserOnlineRecordSummaryPageRequest pageRequest) {
        PageInfo<UserOnlineReportBo> pageInfo = userOnlineRecordService.recordSummaryPage(pageRequest);
        List<UserOnlineReportBo> userOnlineReportBoList = pageInfo.getList();

        
        List<String> onlinedUserIds = userOnlineReportBoList.stream().map(UserOnlineReportBo::getUser).collect(Collectors.toList());
        List<UserConnectionNumBO> userConnectionNumBOList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(onlinedUserIds)) {
            ServiceResult<List<UserConnectionNumBO>> result = csceneSpecialService.countUserConnections(onlinedUserIds);
            userConnectionNumBOList = result.getSource();
        }

        List<UserOnlineReportMonitorBo> monitorDtoList = new ArrayList<>();
        for (UserOnlineReportBo userOnlineReportBo : userOnlineReportBoList) {
            UserOnlineReportMonitorBo monitorDto = new UserOnlineReportMonitorBo();
            BeanUtils.copyProperties(userOnlineReportBo, monitorDto);

            monitorDto.setNodeSessionCnt(0);
            monitorDto.setHasNodeSession(false);

            if (CollectionUtil.isNotEmpty(userConnectionNumBOList)) {
                Optional<UserConnectionNumBO> boOptional = userConnectionNumBOList.stream().filter(bo -> Objects.equals(bo.getUser(), userOnlineReportBo.getUser())).findFirst();
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

    
    public ServiceResult<List<NodeSessionBO>> getUserNodeSessions(String userId) {
        ServiceResult<List<NodeSessionBO>> serviceResult = csceneSpecialService.getUserNodeSessions(userId, true, false);
        return serviceResult.asResult();
    }

    private int countByVirtualTypeAndRole(List<String> sceneIds, List<Integer> virtualTypes, List<Integer> roles) {
        var condition = new HumanCondition()
                .filterIn(CloudPluginSceneElementModel::getScene, sceneIds.toArray())
                .filterIn(CloudPluginSceneElementModel::getVirtualType, virtualTypes.toArray())
                .filterIn(CloudPluginSceneElementModel::getRole, roles.toArray());
        List<CloudPluginSceneElementModel> cloudElements = cloudPluginSceneElementMapper.list(condition);
        return cloudElements.size();
    }

    private int groupSummary(List<Integer> roleType, Map<Object, Long> result) {
        int sum = 0;
        for (var role : roleType) {
            if (result.containsKey(role)) {
                sum += result.get(role);
            }
        }
        return sum;
    }

}
