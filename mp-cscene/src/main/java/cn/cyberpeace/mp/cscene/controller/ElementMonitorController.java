package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.dto.element.ConnectionState;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.element.ElementRTInfo;
import cn.cyberpeace.mp.cscene.dto.element.PortState;
import cn.cyberpeace.mp.cscene.dto.element.ProcessState;
import cn.cyberpeace.mp.cscene.dto.element.StatePageRequest;
import cn.cyberpeace.mp.cscene.dto.scene.SceneStateInfo;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.cscene.service.HostStateService;
import cn.cyberpeace.mp.cscene.service.SceneElementMonitorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/22 10:16
 */
@Api(value = "节点监控", tags = "节点监控")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/monitor/scene/element")
public class ElementMonitorController {

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    @Autowired
    private SceneElementMonitorService sceneElementMonitorService;

    @Autowired
    private HostStateService hostStateService;

    @Autowired
    private CsceneSpecialService csceneSpecialService;

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
        // todo 1201 mock data
        return ServiceResult.success(mockElementRTInfo());
    }


    @ApiOperation(value = "查询节点进程状态")
    @GetMapping("/process_state")
    public ListObjServiceResult<ProcessState> pageProcessesStates(StatePageRequest pageRequest) {
        log.info("{}", pageRequest);
        pageRequest.ensure();
        // todo 1201 mock data
        List<ProcessState> processStates = mockProcessStates(pageRequest.getSceneId());
        return new ListObjServiceResult(new ListObjResult(processStates, processStates.size()).asResult()).asResult();
    }

    @ApiOperation(value = "查询节点端口状态")
    @GetMapping("/port_state")
    public ListObjServiceResult<PortState> pagePortStates(StatePageRequest pageRequest) {
        log.info("{}", pageRequest);
        pageRequest.ensure();
        // todo 1201 mock data
        List<PortState> portStates = mockPortStates(pageRequest.getSceneId());
        return new ListObjServiceResult(new ListObjResult(portStates, portStates.size()).asResult()).asResult();
    }

    @ApiOperation(value = "查询节点网络连接状态")
    @GetMapping("/connection_state")
    public ListObjServiceResult<ConnectionState> pageConnectionStates(StatePageRequest pageRequest) {
        log.info("{}", pageRequest);
        pageRequest.ensure();
        // todo 1201 mock data
        List<ConnectionState> connectionStates = mockConnectionStates(pageRequest.getSceneId());
        return new ListObjServiceResult(new ListObjResult(connectionStates, connectionStates.size()).asResult()).asResult();
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

    private List<ProcessState> mockProcessStates(String csceneId) {
        List<ProcessState> processStates = new ArrayList<>();
        if (!checkCsceneIsRunning(csceneId)) {
            return processStates;
        }
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 10; i++) {
            ProcessState ps = new ProcessState();
            ps.setPid(String.valueOf(123 + i));
            ps.setCmd("cmd");
            ps.setState("Listen");
            ps.setMemory(1024L);
            ps.setCreateTime("2020-05-14 10:10:10");
            processStates.add(ps);
        }
        return processStates;
    }

    private List<PortState> mockPortStates(String csceneId) {
        List<PortState> portStates = new ArrayList<>();
        if (!checkCsceneIsRunning(csceneId)) {
            return portStates;
        }
        for (int i = 0; i < 10; i++) {
            PortState ps = new PortState();
            ps.setPid(String.valueOf(123 + i));
            ps.setPort("80");
            ps.setProtocol("tcp");
            portStates.add(ps);
        }
        return portStates;
    }

    private List<ConnectionState> mockConnectionStates(String csceneId) {
        List<ConnectionState> connectionStates = new ArrayList<>();
        if (!checkCsceneIsRunning(csceneId)) {
            return connectionStates;
        }
        for (int i = 0; i < 10; i++) {
            ConnectionState cs = new ConnectionState();
            cs.setState("LISTEN");
            cs.setLocalAddress("0.0.0.0:22");
            cs.setForeignAddress("0.0.0.0:*");
            cs.setProtocol("tcp");
            connectionStates.add(cs);
        }
        return connectionStates;
    }


    private boolean checkCsceneIsRunning(String csceneId) {
        ServiceResult<SceneStateInfo> result = csceneSpecialService.getSceneStateInfo(csceneId);
        SceneStateInfo sceneStateInfo = result.getSource();
        return sceneStateInfo.isRunning();
    }


}
