package cn.cyberpeace.mp.aggregation.controller;

import cn.cyberpeace.mp.aggregation.constant.ControllerConstant;
import cn.cyberpeace.mp.aggregation.dto.DailyUserActivityResponse;
import cn.cyberpeace.mp.aggregation.dto.UserResourceResponse;
import cn.cyberpeace.mp.aggregation.dto.UserSummarySceneResponse;
import cn.cyberpeace.mp.aggregation.dto.cluster.SystemClusterUsed;
import cn.cyberpeace.mp.aggregation.dto.resource.ResourceStatisticsResponse;
import cn.cyberpeace.mp.aggregation.service.DashboardService;
import cn.cyberpeace.mp.aggregation.service.MockService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: Dashboard相关统计接口
 * @Author zhouxinghuai
 * @Date：2021/9/13 10:32
 */
@Api(value = "Dashboard统计接口", tags = "Dashboard统计接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/dashboard")
public class DashboardController {

    @Autowired
    private MockService mockService;

    private DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/daily_user_activity")
    public ServiceResult<DailyUserActivityResponse> getDailyUserActivity() {
        DailyUserActivityResponse response = dashboardService.dailyUserActivity();
        return ServiceResult.success(response);
    }

    @ApiOperation("资源统计")
    @GetMapping("/resource_statistics")
    public ServiceResult<ResourceStatisticsResponse> getResourceStatistics() {
        ResourceStatisticsResponse response = dashboardService.resourceStatistics();
        return ServiceResult.success(response);
    }


    @GetMapping("/system_cluster_used")
    public ServiceResult<SystemClusterUsed> getSystemClusterUsed() {
        SystemClusterUsed response = mockService.mockSystemClusterUsed();
        return ServiceResult.success(response);
    }

    @ApiOperation("查询活动场景数")
    @GetMapping("/user_summary")
    public ServiceResult<UserSummarySceneResponse> userSummary() {
        UserSummarySceneResponse response = dashboardService.countActivityScene();
        return ServiceResult.success(response);
    }


    @GetMapping("/user_resource")
    public ServiceResult<UserResourceResponse> userResource() {
        return dashboardService.countUserResourceResponse().asResult();
    }

}
