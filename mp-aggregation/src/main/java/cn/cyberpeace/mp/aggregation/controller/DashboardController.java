package cn.cyberpeace.mp.aggregation.controller;

import cn.cyberpeace.mp.aggregation.constant.ControllerConstant;
import cn.cyberpeace.mp.aggregation.dto.cluster.SystemClusterUsed;
import cn.cyberpeace.mp.aggregation.dto.dashboard.UserOnlineReportMonitorBo;
import cn.cyberpeace.mp.aggregation.response.DailyUserActivityResponse;
import cn.cyberpeace.mp.aggregation.response.ResourceStatisticsResponse;
import cn.cyberpeace.mp.aggregation.response.UserResourceResponse;
import cn.cyberpeace.mp.aggregation.response.UserSummarySceneResponse;
import cn.cyberpeace.mp.aggregation.service.DashboardService;
import cn.cyberpeace.mp.aggregation.service.MockService;
import cn.cyberpeace.mp.auth.request.user.UserOnlineRecordSummaryPageRequest;
import cn.cyberpeace.mp.auth.service.UserOnlineRecordService;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeSessionBO;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Api(value = "仪表盘统计接口", tags = "仪表盘统计接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/dashboard")
public class DashboardController {

    @Autowired
    private MockService mockService;

    @Autowired
    private UserOnlineRecordService userOnlineRecordService;

    private DashboardService dashboardService;


    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @ApiOperation("用户日活跃情况")
    @GetMapping("/daily_user_activity")
    public ServiceResult<DailyUserActivityResponse> getDailyUserActivity() {
        DailyUserActivityResponse response = dashboardService.dailyUserActivity();
        return ResultCommon.success(response);
    }

    @ApiOperation("资源统计")
    @GetMapping("/resource_statistics")
    public ServiceResult<ResourceStatisticsResponse> getResourceStatistics() {
        ResourceStatisticsResponse response = dashboardService.resourceStatistics();
        return ResultCommon.success(response);
    }


    @ApiOperation("资源使用情况")
    @GetMapping("/system_cluster_used")
    public ServiceResult<SystemClusterUsed> getSystemClusterUsed() {
        SystemClusterUsed response = mockService.mockSystemClusterUsed();
        return ResultCommon.success(response);
    }

    @ApiOperation("查询活动场景数")
    @GetMapping("/user_summary")
    public ServiceResult<UserSummarySceneResponse> userSummary() {
        UserSummarySceneResponse response = dashboardService.countActivityScene();
        return ResultCommon.success(response);
    }

    @ApiOperation(value = "用户在线记录", notes = "")
    @GetMapping("/user_online_record")
    public ListObjServiceResult<UserOnlineReportMonitorBo> getUserOnlineMonitorRecords(UserOnlineRecordSummaryPageRequest pageRequest) {
        pageRequest.ensure();
        PageInfo<UserOnlineReportMonitorBo> pageInfo = dashboardService.getUserOnlineMonitorRecords(pageRequest);
        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }

    @ApiOperation(value = "用户监控会话", notes = "")
    @ApiImplicitParam(name = "userId", value = "用户ID", dataTypeClass = String.class)
    @GetMapping("/user_node_session")
    public ServiceResult<List<NodeSessionBO>> userNodeSession(@RequestParam String userId) {
        return dashboardService.getUserNodeSessions(userId);
    }

    @ApiOperation(value = "用户资源")
    @GetMapping("/user_resource")
    public ServiceResult<UserResourceResponse> userResource() {
        UserResourceResponse userResourceResponse = dashboardService.countUserResourceResponse();
        return ResultCommon.success(userResourceResponse);
    }

}
