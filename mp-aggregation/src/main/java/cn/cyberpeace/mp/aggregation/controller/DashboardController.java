package cn.cyberpeace.mp.aggregation.controller;

import cn.cyberpeace.mp.aggregation.constant.ControllerConstant;
import cn.cyberpeace.mp.aggregation.dto.cluster.SystemClusterUsed;
import cn.cyberpeace.mp.aggregation.dto.dashboard.UserOnlineReportMonitorDto;
import cn.cyberpeace.mp.aggregation.response.DailyUserActivityResponse;
import cn.cyberpeace.mp.aggregation.response.ResourceStatisticsResponse;
import cn.cyberpeace.mp.aggregation.response.UserResourceResponse;
import cn.cyberpeace.mp.aggregation.response.UserSummarySceneResponse;
import cn.cyberpeace.mp.aggregation.service.DashboardService;
import cn.cyberpeace.mp.aggregation.service.MockService;
import cn.cyberpeace.mp.auth.request.user.UserOnlineRecordSummaryPageRequest;
import cn.cyberpeace.mp.auth.service.UserOnlineRecordService;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
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

import java.util.Arrays;
import java.util.List;

/**
 * @Description: Dashboard相关统计接口
 * @Author zhouxinghuai
 * @Date：2021/9/13 10:32
 */
@Api(value = "仪表盘统计接口", tags = "仪表盘统计接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/dashboard")
public class DashboardController {

    @Autowired
    private MockService mockService;

    @Autowired
    private UserOnlineRecordService userOnlineRecordService;

    @Autowired
    private PasswordUtils passwordUtils;

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
    public ListObjServiceResult<UserOnlineReportMonitorDto> getUserOnlineMonitorRecords(UserOnlineRecordSummaryPageRequest pageRequest) {
        pageRequest.ensure();
        PageInfo<UserOnlineReportMonitorDto> pageInfo = dashboardService.getUserOnlineMonitorRecords(pageRequest);
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

    @GetMapping("/testpwd")
    public void test() {
        List<String> pwds = Arrays.asList("UTVn6vM87V","dSmwvHckt8","HayNz476Kz","39BWEjXUNa","SZxnEFbzuH","vaztRXw9hK","t2HW9SAaUk","DCYZmsFXxf","uFPHUKDz87","6BMeEr2Zk2","ZQJ6Hh4jvL","SDzQC4jaMR","m9JawMeV8X","ZThGcSPFVc","46UCBhZW8R","YAK3naVFmJ","E8DGhTbCvn","Cg38KfcLkH","4CgUY2Bncc","GvCaxwkR5F","cSzNwdXer6","eL9tT6mAFy","2dHW7vtfmj","xftHzbmya9","DFs8c37bK6","ZDV5z3vbX8","6wzWEGPtmy","mBEanHWvM8","SUh97cueYh","9zXBAg3Ssf","JkhX4sFVKB","9tSBkd2yms","s8ZRughSA2","8JbLEB7Fwu","Y6V7fRwxQS","5xQdXuBUaa","HbNA3wRvEK","XAfM6GwHyt","nHEQL4Yz67","5h89WHVxfX","uVrRnYHUgK","SfUnZPerv2","kbZW6yte8t","e2VabZkYUD","gwX79tvPZu","uyvUxGke7k","PkTc5XtBU7","Gr9f7FnQwb","TGeXkUnt53","2hytDaNrdJ","XsfFdLhUyZ","yM2KcdPY8y","ERdzuFPG7H","FC7fB6u2KX","2GuHYsTrJf","rEPQLsjutM","RBPT82Qgtn","Hjsbw3umw4","hdCkK6XtB2","XFS4nPLMYA","aDwrv5TYdf","GctVrdyk4P","nfKb9uY4m2","QYabVZc3at","ZyUbAtfLXj","RZeJTP9DG3","QPrNsfUZxh","9g3Cr6XjQk","nh2a3fVKua","Q5BedzrcDh","C2satBAdrS","muwsJbUY8e","DdfGrgBQmX","kjLNCvhSFX","5dkymjPG2K","yzuYNLEDxF","zNYhegbCUC","FJRBca9KDn","LcZnGFdreT","3HUGjnMrxP","a5e7vSdsRV","rAj7dfTS88","uz8bJXtCm7","KnEysrdNtT","4gLe3hPyu7","UWMzFYTfuc","d4XQusgkTy","4ruTgBSmN8","fJMmLY9ZU6","4YXvHKwnBZ","LVTH6fNA9w","zerCadN6QW","6TFg4DnzAX","3fn7UJTjzx","AHmCnLhGzC","RTtb2rvKuW","fSgzNvZ3X5","fju5rcUNQL","9JXDeCkrY3","3vQ25uzWwz","J79PhALjw9","GnUSkHxgmC","z4V8xbSXr9","XxVuSCvhLz","Dj63JB2ASF","3XVa6YRKd3","F2gACR6zxM","WPn7umeV2B","YAkeK4ndvH","drJ4G3YMYT","M9dey7uYWa","j4gbd8fuye","F34VvATMHx","3U6Nmxbgcr","VUS7B3XxVE","jSCkn8uP7E","dYRe6zNkTd","ASULcNTyEa","HAbaJMw8aL","jcfxV73AvP","G72dPWvtEx","UfYh73kEwv","xv5z2DnbjH","GCWJRuGK3W","LzKvEJuPke","8Lf36JTt74","hks96v3Zns","wGNgsx6vVQ","au5tHc2UGY","c5QFCPuHJT","R9Ptbn4HAQ","sAbrDXJYQR","hvJKXEcftJ","uRvjn7md5t","6zC57eK2as","2rbWxC8Chv","DcsvQfZNX4","7fPeUjcAwK","MaCgH6NZWf","7Y6DmPKeJZ","fagE3rYAFr","uxjPGsY4Dx","Z3cBsa8fMW","HD89GuWQyG","ZfFkHungy3","CxaT7KjYM5","gkfPHCjArZ","trmDZBwGLu","2DZHmfyd6f","EnwJmvMQwS","cUFBRYM3mx","9NSuRZJ7xC","nXDG2VFhdz","3Wcf9ZAjY9","cfWDr3vNeQ","QTjGNm8A6s","zwQu4yBaLK","z2NwXmuyCd","CdnjAzYEsk","BPjTZ2LaVy","wHdcEkNAvc","jazL2Wf8CF","CRPV79TEA6","BKmynHkaUA","LM2NajyP9T","tRCumsZ6j3","7jzCvM9SHB","3aWrQmQYEu","RTUnDdbMaU","A2y6HwGv8t","uFnVhZ9E8R","vK6gFzPuRc","FGEC6Azny9","9MvkX4uVKe","dKHYBybCLv","BZ7C5kuEz6","hPVnXWmxEK","zbeHfmVF7C","sRaxUfcDbM","uBTJ9VXMh4","CB7KPvQrxJ","Ds4hSQrcWD","tvfb8B33yk","8V64bvJDsW","L3YUhdvCz3","bKfEmdrF3r","XDjrd2VtZa","3TB8NJESrU","r7yMNuZPWP","gmQjfD5v6S","WytCnNc2E9","K6rt2TuYSt","ec7DazStP4","uFR4wdVLDc","3GcYyQhsuC","KsSndN4Ygb","vQBUDaMn54","LxdP5Qm8cP","SyCfeaZgwF","GYWFKUxs8h","AQNY8wEesy","FR2BG98kbM","4Cxr5ERHQJ","aSyLQf26sz","gZGFMDeS7G","AgNxaLH4bM","Zb2DryFECx","D9GnUVYtL6","BN8PYLAMcg","cKe6NH89Gu","RkF4Jc5Enk","VNwTJKAckc","3w8gJMsRNH","g8CjNnGbts","Dr4A3g7mrG","k5fRLTnmED","HJbam4GSeU","728hZxPA76","aRBTm29YBU","QNEmTskF53","7dLJcy58Fj","GZcxjHTWsb","cuSvdDMJWs","Rf376FQHYC","J3RHNt5Ehn","nhxaVeNSTd","L8FebYv7Dd","YMCTRgt6x5","twJxuLaSKm","STGrhFytvk","5cLKP6JAvE","y4n98KdUgL","Y34B5yUFxA","624JyDn3jH","drcPHBjAaG","RrmH4hkjXL","9SmVcXdgvA","hCzZ6knD8v","VeJLb7KWXj","Y9kJmG8TJ4");
        for (int i = 0; i < pwds.size(); i++) {
            System.out.println("##testpwd:\t" + pwds.get(i) + "\t" + passwordUtils.encode(pwds.get(i)));
        }
    }

}
