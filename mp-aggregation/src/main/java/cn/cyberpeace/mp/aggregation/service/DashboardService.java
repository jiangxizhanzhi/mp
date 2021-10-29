package cn.cyberpeace.mp.aggregation.service;

import cn.cyberpeace.mp.aggregation.dto.DailyUserActivityResponse;
import cn.cyberpeace.mp.aggregation.dto.UserSummarySceneResponse;
import cn.cyberpeace.mp.aggregation.dto.resource.ResourceStatisticsResponse;
import cn.cyberpeace.mp.auth.service.UserOnlineRecordService;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceCountActivitySceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceCountByReferenceTypeBO;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devops.cluster.model.DeviceStaticsCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.service.DeviceService;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author wanggang
 */
@Service
@Slf4j
public class DashboardService {

    private DeviceService deviceService;

    private CsceneInstanceService csceneInstanceService;

    private CsceneSpecialService csceneSpecialService;

    @Autowired
    private UserOnlineRecordService userOnlineRecordService;

    @Autowired
    public DashboardService(DeviceService deviceService, CsceneInstanceService csceneInstanceService, CsceneSpecialService csceneSpecialService) {
        this.deviceService = deviceService;
        this.csceneInstanceService = csceneInstanceService;
        this.csceneSpecialService = csceneSpecialService;
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

        // todo 每日启动场景数量
        Integer[] sceneCount = {45, 5, 0, 24, 2, 1, 8, 16, 12, 0, 7};

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
        DeviceStaticsCloudPlatformModel model = deviceService.statics();
        if (Objects.nonNull(model)) {
            Map<String, Map<String, Integer>> targetStatics = model.getData();
            response.setTargetStatics(targetStatics);
        }
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
}
