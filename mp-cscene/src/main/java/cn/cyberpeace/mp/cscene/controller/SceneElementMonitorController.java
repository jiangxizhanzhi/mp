package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.element.ElementResourceUsage;
import cn.cyberpeace.mp.cscene.dto.event.ElementMonitorInfo;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.service.SceneElementMonitorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/22 10:16
 */
@Api(value = "场景节点监控", tags = "场景节点监控")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/monitor/scene/element")
public class SceneElementMonitorController {

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    @Autowired
    private SceneElementMonitorService sceneElementMonitorService;

    @ApiOperation(value = "查询节点监控信息")
    @GetMapping("/info")
    public ServiceResult<ElementMonitorInfo> getSceneElementMonitorInfo(@RequestParam String sceneId, @RequestParam String elementId) {
        log.info("sceneId:{}, elementId:{}", sceneId, elementId);

        var result = csceneInstanceService.getById(sceneId, null);
        ElementMonitorInfo elementMonitorInfo = sceneElementMonitorService.sceneInstanceBO2ElementMonitorInfo(result.getSource(), elementId);

        return ServiceResult.success(elementMonitorInfo);
    }


    @ApiOperation(value = "查询节点资源使用统计")
    @GetMapping("/resource_usage/{elementId}")
    public ServiceResult<ElementResourceUsage> getElementResourceUsage(@PathVariable String elementId) {
        log.info("elementId:{}", elementId);

        return null;
    }

}
