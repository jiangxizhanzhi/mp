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
import cn.cyberpeace.mp.cscene.service.SceneElementMonitorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        ProcessState ps1 = new ProcessState();
        ps1.setPid("9527");
        ps1.setPpid("20");
        ps1.setUser("systemd+");
        ps1.setCmd("systemd");
        ps1.setState("S");
        ps1.setMemory(2.3);
        ps1.setCreateTime(now.minusHours(1).format(formatter));


        ProcessState ps2 = new ProcessState();
        ps2.setPid("9877");
        ps2.setPpid("20");
        ps2.setUser("systemd+");
        ps2.setCmd("systemd");
        ps2.setState("S");
        ps2.setMemory(4.0);
        ps2.setCreateTime(now.minusHours(1).format(formatter));


        ProcessState ps3 = new ProcessState();
        ps3.setPid("9087");
        ps3.setPpid("20");
        ps3.setUser("systemd+");
        ps3.setCmd("systemd");
        ps3.setState("S");
        ps3.setMemory(3.0);
        ps3.setCreateTime(now.minusHours(1).format(formatter));


        ProcessState ps4 = new ProcessState();
        ps4.setPid("18");
        ps4.setPpid("1");
        ps4.setUser("root");
        ps4.setCmd("kthreadd");
        ps4.setState("S");
        ps4.setMemory(1.3);
        ps4.setCreateTime(now.minusHours(3).minusMinutes(23).format(formatter));


        ProcessState ps5 = new ProcessState();
        ps5.setPid("17");
        ps5.setPpid("1");
        ps5.setUser("root");
        ps5.setCmd("migration");
        ps5.setState("S");
        ps5.setMemory(0.5);
        ps5.setCreateTime(now.minusHours(2).minusMinutes(20).format(formatter));


        ProcessState ps6 = new ProcessState();
        ps6.setPid("13");
        ps6.setPpid("1");
        ps6.setUser("root");
        ps6.setCmd("rcushed");
        ps6.setState("I");
        ps6.setMemory(1.3);
        ps6.setCreateTime(now.minusHours(1).minusMinutes(13).format(formatter));


        ProcessState ps7 = new ProcessState();
        ps7.setPid("21");
        ps7.setPpid("20");
        ps7.setUser("root");
        ps7.setCmd("ksoftirqd/2");
        ps7.setState("S");
        ps7.setMemory(0.0);
        ps7.setCreateTime(now.minusMinutes(15).format(formatter));


        ProcessState ps8 = new ProcessState();
        ps8.setPid("32");
        ps8.setPpid("20");
        ps8.setUser("root");
        ps8.setCmd("cpuhp/4");
        ps8.setState("S");
        ps8.setMemory(0.0);
        ps8.setCreateTime(now.minusMinutes(12).format(formatter));

        ProcessState ps9 = new ProcessState();
        ps9.setPid("38");
        ps9.setPpid("1");
        ps9.setUser("root");
        ps9.setCmd("watchdog/2");
        ps9.setState("I");
        ps9.setMemory(0.0);
        ps9.setCreateTime(now.minusMinutes(8).format(formatter));

        ProcessState ps10 = new ProcessState();
        ps10.setPid("52");
        ps10.setPpid("20");
        ps10.setUser("root");
        ps10.setCmd("kworker/2");
        ps10.setState("I");
        ps10.setMemory(0.0);
        ps10.setCreateTime(now.minusMinutes(1).format(formatter));

        processStates.add(ps1);
        processStates.add(ps2);
        processStates.add(ps3);
        processStates.add(ps4);
        processStates.add(ps5);
        processStates.add(ps6);
        processStates.add(ps7);
        processStates.add(ps8);
        processStates.add(ps9);
        processStates.add(ps10);

        return processStates;
    }

    private List<PortState> mockPortStates(String csceneId) {
        List<PortState> portStates = new ArrayList<>();
        if (!checkCsceneIsRunning(csceneId)) {
            return portStates;
        }

        PortState ps1 = new PortState();
        ps1.setPid("2950946");
        ps1.setProcess("sshd");
        ps1.setProtocol("tcp");
        ps1.setLocalIp("0.0.0.0");
        ps1.setPort("22");
        ps1.setRemoteIp("0.0.0.0");
        ps1.setRemotePort("*");
        ps1.setState("ESTABLISHED");

        PortState ps2 = new PortState();
        ps2.setPid("3826");
        ps2.setProcess("sshd");
        ps2.setProtocol("tcp");
        ps2.setLocalIp("0.0.0.0");
        ps2.setPort("22");
        ps2.setRemoteIp("0.0.0.0");
        ps2.setRemotePort("*");
        ps2.setState("ESTABLISHED");


        PortState ps3 = new PortState();
        ps3.setPid("7440");
        ps3.setProcess("sshd");
        ps3.setProtocol("tcp");
        ps3.setLocalIp("10.100.0.251");
        ps3.setPort("22");
        ps3.setRemoteIp("192.168.5.152");
        ps3.setRemotePort("65474");
        ps3.setState("ESTABLISHED");



        PortState ps4 = new PortState();
        ps4.setPid("3983232");
        ps4.setProcess("sshd");
        ps4.setProtocol("tcp");
        ps4.setLocalIp("10.100.0.251");
        ps4.setPort("22");
        ps4.setRemoteIp("192.168.5.152");
        ps4.setRemotePort("65475");
        ps4.setState("ESTABLISHED");

        PortState ps5 = new PortState();
        ps5.setPid("2950946");
        ps5.setProcess("sshd");
        ps5.setProtocol("tcp");
        ps5.setLocalIp("0.0.0.0");
        ps5.setPort("22");
        ps5.setRemoteIp("0.0.0.0");
        ps5.setRemotePort("*");
        ps5.setState("LISTEN");

        PortState ps6 = new PortState();
        ps6.setPid("2453594");
        ps6.setProcess("prox");
        ps6.setProtocol("tcp");
        ps6.setLocalIp("0.0.0.0");
        ps6.setPort("19307");
        ps6.setRemoteIp("0.0.0.0");
        ps6.setRemotePort("*");
        ps6.setState("LISTEN");

        PortState ps7 = new PortState();
        ps7.setPid("3899826");
        ps7.setProcess("prox");
        ps7.setProtocol("tcp");
        ps7.setLocalIp("0.0.0.0");
        ps7.setPort("19306");
        ps7.setRemoteIp("0.0.0.0");
        ps7.setRemotePort("*");
        ps7.setState("LISTEN");

        PortState ps8 = new PortState();
        ps8.setPid("446218");
        ps8.setProcess("beam.smp");
        ps8.setProtocol("tcp6");
        ps8.setLocalIp("10.88.88.11");
        ps8.setPort("1567");
        ps8.setRemoteIp("10.88.88.11");
        ps8.setRemotePort("34272");
        ps8.setState("ESTABLISHED");


        PortState ps9 = new PortState();
        ps9.setPid("423106");
        ps9.setProcess("beam.smp");
        ps9.setProtocol("tcp6");
        ps9.setLocalIp("10.88.88.11");
        ps9.setPort("1567");
        ps9.setRemoteIp("10.88.88.11");
        ps9.setRemotePort("40828");
        ps9.setState("ESTABLISHED");


        PortState ps10 = new PortState();
        ps10.setPid("446234");
        ps10.setProcess("beam.smp");
        ps10.setProtocol("tcp6");
        ps10.setLocalIp("10.88.88.11");
        ps10.setPort("1567");
        ps10.setRemoteIp("10.88.88.11");
        ps10.setRemotePort("43360");
        ps10.setState("ESTABLISHED");

        portStates.add(ps1);
        portStates.add(ps3);
        portStates.add(ps3);
        portStates.add(ps4);
        portStates.add(ps5);
        portStates.add(ps6);
        portStates.add(ps7);
        portStates.add(ps10);
        portStates.add(ps9);
        portStates.add(ps10);

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
