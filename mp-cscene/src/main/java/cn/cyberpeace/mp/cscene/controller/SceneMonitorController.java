package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.scene.SceneMonitorInfo;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.service.SceneJsonMockService;
import cn.cyberpeace.mp.cscene.service.SceneMonitorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/22 10:15
 */
@Api(value = "场景监控", tags = "场景监控")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/monitor/scene")
public class SceneMonitorController {

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    @Autowired
    private SceneMonitorService sceneMonitorService;

    @Autowired
    private SceneJsonMockService sceneJsonMockService;

    @ApiOperation(value = "查询场景监控信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sceneId", value = "场景ID", dataTypeClass = String.class),
    })
    @GetMapping("/info/{sceneId}")
    public ServiceResult<SceneMonitorInfo> getSceneMonitorInfo(@PathVariable String sceneId) {
        //SceneMonitorInfo sceneMonitorInfo = sceneMonitorService.getSceneMonitorInfo(sceneId);

        var sceneMonitorInfo = sceneJsonMockService.getSceneMonitorInfo();
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        LocalDateTime createTime = today.minusDays(1).atTime(10, 18, 29);
        sceneMonitorInfo.setCreateTime(createTime);
        sceneMonitorInfo.setRunMinutes(Duration.between(createTime, now).toMinutes());

        return ResultCommon.success(sceneMonitorInfo);
    }


    @ApiOperation(value = "查询场景所有节点列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sceneId", value = "场景ID", dataTypeClass = String.class),
            @ApiImplicitParam(name = "realTimeHostState", value = "是否获取实时节点主机信息", dataTypeClass = Boolean.class)
    })
    @GetMapping("/all_elements/{sceneId}")
    public ServiceResult<List<ElementMonitorBasicInfo>> getSceneAllElements(@PathVariable String sceneId, @RequestParam(required = false) Boolean realTimeHostState) {
        //List<ElementMonitorBasicInfo> basicInfoList = sceneMonitorService.getAllSceneElements(sceneId, realTimeHostState);
        var basicInfoList = sceneJsonMockService.listElementMonitorBasicInfos();
        return ResultCommon.success(basicInfoList);
    }


    @ApiOperation(value = "查询场景所有IP列表")
    @GetMapping("/ips")
    public ServiceResult<List<String>> listIps(BaseSceneElementRequest baseRequest) {
        List<String> ips = sceneMonitorService.listIps(baseRequest);
        return ResultCommon.success(ips);
    }

    @ApiOperation(value = "查询场景所有录屏用户列表")
    @GetMapping("/recording/users")
    public ServiceResult<List<UserModel>> listRecordingUsers(BaseSceneElementRequest baseRequest) {
        List<UserModel> users = sceneMonitorService.listSceneRecordingUsers(baseRequest);
        return ResultCommon.success(users);
    }

    @ApiOperation(value = "查询场景top5节点状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sceneId", value = "场景ID", dataTypeClass = String.class),
            @ApiImplicitParam(name = "topCount", value = "数量", dataTypeClass = Integer.class)
    })
    @GetMapping("/elements/top_by_states/{sceneId}")
    public ServiceResult<Map<String, List<ElementMonitorBasicInfo>>> topByHoststates(@PathVariable String sceneId, @RequestParam(required = false) Integer topCount) {
        Map<String, List<ElementMonitorBasicInfo>> result = sceneMonitorService.topAggrByHoststates(sceneId, 5);

        return ResultCommon.success(result);
    }

//    @ApiOperation(value = "查询场景所有主机信息，不含实时内存，cpu等数据")
//    @GetMapping("/all_hosts/{sceneId}")
//    public ServiceResult<List<ElementMonitorBasicInfo>> getSceneAllHosts(@PathVariable String sceneId) {
//        List<ElementMonitorBasicInfo> basicInfoList = sceneMonitorService.getAllSceneElements(sceneId);
//        return ServiceResult.success(basicInfoList);
//    }


}
