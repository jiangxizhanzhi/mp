package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.constant.CsceneBizConstant;
import cn.cyberpeace.mp.cscene.controller.request.SceneAllElementsQueryRequest;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.scene.SceneMonitorInfo;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.service.SceneMonitorService;
import cn.cyberpeace.mp.cscene.service.multirange.testrange.TestRangeApiProxy;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
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
    private CsceneInstanceMapper csceneInstanceMapper;

    @Autowired
    private SceneMonitorService sceneMonitorService;
    @Autowired
    private TestRangeApiProxy testRangeApiProxy;

    @ApiOperation(value = "查询场景监控信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sceneId", value = "场景ID", dataTypeClass = String.class),
    })
    @GetMapping("/info/{sceneId}")
    public ServiceResult<SceneMonitorInfo> getSceneMonitorInfo(@PathVariable String sceneId) {
        SceneMonitorInfo sceneMonitorInfo = sceneMonitorService.getSceneMonitorInfo(sceneId);
        return ResultCommon.success(sceneMonitorInfo);
    }

    private Map<String, List<ElementMonitorBasicInfo>> cache = new HashMap<>();

    @ApiOperation(value = "查询场景所有节点列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sceneId", value = "场景ID", dataTypeClass = String.class),
            @ApiImplicitParam(name = "realTimeHostState", value = "是否获取实时节点主机信息", dataTypeClass = Boolean.class)
    })
    @GetMapping("/all_elements/{sceneId}")
    public ServiceResult<List<ElementMonitorBasicInfo>> getSceneAllElements(@PathVariable String sceneId,
                                                                            @RequestParam(required = false) Boolean realTimeHostState,
                                                                            @RequestParam(required = false) String isQuerySubRangeNodes,
                                                                            SceneAllElementsQueryRequest request) {
        boolean canUseCache = true;
        if (realTimeHostState != null || isQuerySubRangeNodes != null) {
            canUseCache = false;
        }
        Map<String, Object> map = JsonCommon.beanToMap(request);
        for (Map.Entry<String, Object> m : map.entrySet()) {
            if (m.getValue() != null) {
                canUseCache = false;
            }
        }
        if (canUseCache && cache.containsKey(sceneId)) {
            var res = cache.get(sceneId);
            return ResultCommon.success(res);
        }

        SceneMonitorService.Info info = sceneMonitorService.getAllSceneElementsV2(sceneId, realTimeHostState, request);
        var basicInfoList = info.getList();

        if (Boolean.TRUE.equals(request.getIncludeDeleting())) {
            return ResultCommon.success(basicInfoList);
        }
        List<ElementMonitorBasicInfo> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(basicInfoList)) {
            for (var e : basicInfoList) {
                if (!e.getCurrentScenePartFlag()) {
                    continue;
                }
                if (e.getEvent() != null) {
                    if (e.getEvent() != SceneElementModel.Event.DELETE) {
                        result.add(e);
                    } else if (e.getEvent() == SceneElementModel.Event.DELETE && e.getEventStatus() == EventStatus.OVER) {
                        result.add(e);
                    }
                } else {
                    result.add(e);
                }
            }
        }
        if(StringUtils.isNotEmpty(isQuerySubRangeNodes)){
            if (canUseCache) {
                cache.put(sceneId, result);
            }
            return ResultCommon.success(result);
        }
        try {
            var testRangeResult = testRangeApiProxy.listAllElementBasicInfo(sceneId, request);
            var ip2Elements = info.getIp2Elements();
            for (var e : testRangeResult) {
                if (CollectionUtils.isEmpty(e.getPorts())) {
                    continue;
                }
                for (var p : e.getPorts()) {
                    if (p.getIp() != null && ip2Elements.containsKey(p.getIp())) {
                        var currentNode = ip2Elements.get(p.getIp());
                        e.setName(currentNode.getName());
                    }
                }
            }
            result.addAll(testRangeResult);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        if (canUseCache) {
            cache.put(sceneId, result);
        }
        return ResultCommon.success(result);
    }

    @ApiOperation(value = "按需查询场景节点列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sceneId", value = "场景ID", dataTypeClass = String.class),
            @ApiImplicitParam(name = "support_probe", value = "查询支持探针采集的节点", dataTypeClass = Boolean.class),
            @ApiImplicitParam(name = "support_flow", value = "查询支持流量采集的节点", dataTypeClass = Boolean.class)
    })
    @GetMapping("/list_elements/{sceneId}")
    public ServiceResult<List<ElementMonitorBasicInfo>> getSceneElements(@PathVariable String sceneId,
                                                                         @RequestParam(required = false, name = "support_probe") Boolean supportProbe,
                                                                         @RequestParam(required = false, name = "support_flow") Boolean supportFlow
    ) {
        List<ElementMonitorBasicInfo> basicInfoList = sceneMonitorService.getSceneElements(sceneId, supportProbe, supportFlow);
        return ResultCommon.success(basicInfoList);
    }

    @ApiOperation(value = "查询场景模板所有节点列表")
    @ApiImplicitParam(name = "id", value = "场景模板ID", dataTypeClass = String.class)
    @GetMapping("/config/all_elements/{id}")
    public ServiceResult<List<ElementMonitorBasicInfo>> getSceneAllElements(@PathVariable String id, SceneAllElementsQueryRequest request) {
        List<ElementMonitorBasicInfo> basicInfoList = sceneMonitorService.getAllSceneConfigElements(id, request);
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
