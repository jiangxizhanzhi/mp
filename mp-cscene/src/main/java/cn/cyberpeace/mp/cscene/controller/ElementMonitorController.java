package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.dto.element.ConnectionState;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.element.ElementRTInfo;
import cn.cyberpeace.mp.cscene.dto.element.PortState;
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
//        ElementRTInfo rtInfo = sceneElementMonitorService.getElementRTInfo(baseRequest);
        // todo 实时状态
        return ServiceResult.success(mockElementRTInfo());
    }


    @ApiOperation(value = "查询节点进程状态")
    @GetMapping("/process_state")
    public ServiceResult<List<ProcessState>> getProcessesStates(BaseSceneElementRequest baseRequest) {
        log.info("{}", baseRequest);
        // todo 1201
        return ServiceResult.success(null);
    }

    @ApiOperation(value = "查询节点端口状态")
    @GetMapping("/port_state")
    public ServiceResult<List<PortState>> getPortStates(BaseSceneElementRequest baseRequest) {
        log.info("{}", baseRequest);
        // todo 1201
        return ServiceResult.success(null);
    }

    @ApiOperation(value = "查询节点网络连接状态")
    @GetMapping("/connection_state")
    public ServiceResult<List<ConnectionState>> getConnectionStates(BaseSceneElementRequest baseRequest) {
        log.info("{}", baseRequest);
        // todo 1201
        return ServiceResult.success(null);
    }


    private ElementRTInfo mockElementRTInfo() {
        ElementRTInfo rtInfo = new ElementRTInfo();
        rtInfo.setProcessCount(5);
        rtInfo.setUserCount(1);
        rtInfo.setPortsCount(10);
        rtInfo.setConnectionCount(11);
        rtInfo.setFileOpCount(5);
        rtInfo.setRegistryOpCount(5);
        rtInfo.setEnvOpCount(5);
        return rtInfo;
    }


}
