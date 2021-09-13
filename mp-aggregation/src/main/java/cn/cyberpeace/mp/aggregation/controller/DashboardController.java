package cn.cyberpeace.mp.aggregation.controller;

import cn.cyberpeace.mp.aggregation.constant.ControllerConstant;
import cn.cyberpeace.mp.aggregation.dto.DailyUserActivityResponse;
import cn.cyberpeace.mp.aggregation.dto.UserResourceResponse;
import cn.cyberpeace.mp.aggregation.dto.UserSummarySceneResponse;
import cn.cyberpeace.mp.aggregation.dto.resource.ResourceStatisticsResponse;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping("/daily_user_activity")
    public ServiceResult<DailyUserActivityResponse> getDailyUserActivity() {


        return null;
    }


    @GetMapping("/get_resource_statistics")
    public ServiceResult<ResourceStatisticsResponse> getResourceStatistics() {


        return null;
    }


    @GetMapping("/get_system_cluster_used")
    public ServiceResult getSystemClusterUsed() {


        return null;
    }

    @GetMapping("/user_summary_scene")
    public ServiceResult<UserSummarySceneResponse> userSummary() {


        return null;
    }


    @GetMapping("/user_resource")
    public ServiceResult<UserResourceResponse> userResource() {


        return null;
    }

}
