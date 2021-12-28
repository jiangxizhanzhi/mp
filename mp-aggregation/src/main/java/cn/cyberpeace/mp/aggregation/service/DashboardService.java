package cn.cyberpeace.mp.aggregation.service;

import cn.cyberpeace.mp.aggregation.dto.DailyUserActivityResponse;
import cn.cyberpeace.mp.aggregation.dto.UserResourceResponse;
import cn.cyberpeace.mp.aggregation.dto.UserSummarySceneResponse;
import cn.cyberpeace.mp.aggregation.dto.resource.ResourceStatisticsResponse;
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
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.DeviceService;
import cn.cyberpeace.mp.devops.cluster.service.DevopsTargetService;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
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
 * @author wanggang
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

    @Autowired
    private UserOnlineRecordService userOnlineRecordService;

    @Autowired
    public DashboardService(DeviceService deviceService,
                            CsceneInstanceService csceneInstanceService,
                            CsceneSpecialService csceneSpecialService,
                            CsceneInstanceMapper csceneInstanceMapper,
                            ScenePluginCloudSceneService scenePluginCloudSceneService,
                            DevopsTargetService devopsTargetService) {
        this.deviceService = deviceService;
        this.csceneInstanceService = csceneInstanceService;
        this.csceneSpecialService = csceneSpecialService;
        this.csceneInstanceMapper = csceneInstanceMapper;
        this.scenePluginCloudSceneService = scenePluginCloudSceneService;
        this.devopsTargetService = devopsTargetService;
    }

    /**
     * 最近一周用户活动轨迹
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
        CollectionUtil.reverse(daylist);

        String[] xAxis = daylist.stream().map(day -> df.format(day)).toArray(String[]::new);
        Integer[] userCount = daylist.stream().map(day -> {
            Integer cnt = userOnlineRecordService.getOnlinedUserCntByPeriod(day.atStartOfDay(), day.plusDays(1).atStartOfDay());
            return cnt;
        }).toArray(Integer[]::new);

        // 每日活跃场景数量 todo,性能可优化
        Integer[] sceneCount = daylist.stream().map(day -> csceneSpecialService.countActivitySceneByTime(day.atStartOfDay(), day.plusDays(1).atStartOfDay())).toArray(Integer[]::new);

        DailyUserActivityResponse response = DailyUserActivityResponse.builder().xAxis(xAxis).userCount(userCount).sceneCount(sceneCount).build();
        return response;
    }

    /**
     * 仪表盘-资源统计
     * @return
     */
    public ResourceStatisticsResponse resourceStatistics() {
        ResourceStatisticsResponse response = new ResourceStatisticsResponse();
        Map<String, Map<String, Object>> targetStatics = new HashMap<>();
        targetStatics.put("all",devopsTargetService.countTarget());
        targetStatics.put("entity",devopsTargetService.countEntityTarget());
        targetStatics.put("virtual",devopsTargetService.countVmTarget());
        response.setTargetStatics(targetStatics);
        List<CsceneInstanceCountByReferenceTypeBO> sceneStatics = csceneInstanceService.countGroupByReferenceType(Arrays.asList(SceneModel.Event.DELETE));
        response.setSceneStatics(sceneStatics);
        return response;
    }

    /**
     * 统计活动场景总数和场景占比
     * @return
     */
    public UserSummarySceneResponse countActivityScene() {
        CsceneInstanceCountActivitySceneBO bo = csceneSpecialService.countActivityScene();

        UserSummarySceneResponse response = new UserSummarySceneResponse();
        response.setRunningSceneCount(bo.getActivitySceneTotal());
        BigDecimal runningScenePercent = BigDecimal.ZERO;
        if (NumberUtils.INTEGER_ZERO.equals(bo.getTotal()) || Objects.isNull(bo.getActivitySceneTotal())) {
            runningScenePercent = BigDecimal.ZERO;
        } else {
            runningScenePercent = BigDecimal.valueOf(bo.getActivitySceneTotal()).divide(BigDecimal.valueOf(bo.getTotal()), 2, RoundingMode.DOWN).multiply(BigDecimal.valueOf(100));
        }
        response.setRunningScenePercent(runningScenePercent);
        return response;
    }

    public ServiceResult<UserResourceResponse> countUserResourceResponse() {
        var userResource = UserResourceResponse
                .builder()
                .containerCount(0)
                .vmCount(0)
                .entityCount(0)
                .sceneCount(0)
                .build();
        var user = UserCommon.getCurrentUser();
        if (user == null) {
            return new ServiceResult<>(userResource);
        }
        var condition = new HumanCondition()
                .filterEqual(CsceneInstanceModel::getDeleted, false)
                .filterEqual(CsceneInstanceModel::getType, CsceneInstanceModel.Type.APP)
                .filterEqual(CsceneInstanceModel::getCreateUser, user.getId());
        List<CsceneInstanceModel> csceneInstances;
        try {
            csceneInstances = csceneInstanceMapper.list(condition);
        } catch (Exception e) {
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        }
        if (csceneInstances.isEmpty()) {
            return new ServiceResult<>(userResource);
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
        return new ServiceResult<>(userResource);
    }

}
