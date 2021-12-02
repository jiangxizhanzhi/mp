package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.element.ElementRTInfo;
import cn.cyberpeace.mp.cscene.dto.element.ProcessState;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.service.SceneElementMonitorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/22 10:16
 */
@Api(value = "场景节点监控", tags = "场景节点监控")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/monitor/scene/element")
public class ElementMonitorController {

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    @Autowired
    private SceneElementMonitorService sceneElementMonitorService;

    /**
     * elementIds不能为空，如果为多个值，默认只取第一个
     *
     * @param baseRequest
     * @return
     */
    @ApiOperation(value = "查询节点基本信息")
    @GetMapping("/info")
    public ServiceResult<ElementMonitorBasicInfo> getElementMonitorInfo(BaseSceneElementRequest baseRequest) {
        ElementMonitorBasicInfo elementMonitorBasicInfo = sceneElementMonitorService.getElementMonitorInfo(baseRequest);

        return ServiceResult.success(elementMonitorBasicInfo);
    }


    /**
     * elementIds不能为空，如果为多个值，默认只取第一个
     *
     * @param baseRequest
     * @return
     */
    @ApiOperation(value = "查询节点实时状态")
    @GetMapping("/rtinfo")
    public ServiceResult<ElementRTInfo> getElementRTInfo(BaseSceneElementRequest baseRequest) {
        ElementRTInfo rtInfo = sceneElementMonitorService.getElementRTInfo(baseRequest);

        return ServiceResult.success(rtInfo);
    }


    @ApiOperation(value = "查询节点进程信息")
    @GetMapping("/process")
    public ServiceResult<List<ProcessState>> getProcessesStates(BaseSceneElementRequest baseRequest) {
        log.info("{}", baseRequest);
        // todo 1201
        return null;
    }

    @ApiOperation(value = "查询节点端口信息")
    @GetMapping("/ports")
    public ServiceResult getPortsStates(BaseSceneElementRequest baseRequest) {
        log.info("{}", baseRequest);
        // todo 1201
        return null;
    }

    @ApiOperation(value = "查询节点网络连接信息")
    @GetMapping("/connections")
    public ServiceResult getConnectionsStates(BaseSceneElementRequest baseRequest) {
        log.info("{}", baseRequest);
        // todo 1201
        return null;
    }

}
