package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.dto.element.XmlHostState;
import cn.cyberpeace.mp.cscene.dto.scene.SceneAttCkTypeCountInfo;
import cn.cyberpeace.mp.cscene.request.agent.AgentNameBasedRequest;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.request.hoststate.HostStateTrendRequest;
import cn.cyberpeace.mp.cscene.service.HostStateService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.TimeListPageInfoQueryParams;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.*;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/22 11:22
 */
@Api(value = "主机状态监控", tags = "主机状态监控")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/monitor/hoststate")
public class HostStateController {

    @Autowired
    private HostStateService hostStateService;

    /**
     * 默认查询场景所有节点
     * 忽略elementIds的值
     * @param hostStateTrendRequest
     * @return
     */
    @ApiOperation(value = "场景主机状态趋势图")
    @GetMapping("/scene_trend")
    public ServiceResult<List<HostState>> getSceneHostStateTrend(HostStateTrendRequest hostStateTrendRequest) {
        if (StringUtils.isEmpty(hostStateTrendRequest.getSceneId())) {
            return ResultCommon.success(new ArrayList<>());
        }
        if (CollectionUtils.isNotEmpty(hostStateTrendRequest.getElementIds())) {
            List<HostState> hostStates = hostStateService.getElementHostStateTrend(hostStateTrendRequest);
            return ResultCommon.success(hostStates);
        } else {
            List<HostState> hostStates = hostStateService.getSceneHostStateTrend(hostStateTrendRequest);
            return ResultCommon.success(hostStates);
        }
    }


    /**
     * 集群状态趋势
     * @param hostStateTrendRequest
     * @return
     */
    @ApiOperation(value = "集群状态趋势图")
    @GetMapping("/cluster_trend")
    public ServiceResult<List<HostState>> getClusterHostStateTrend(HostStateTrendRequest hostStateTrendRequest) {
        hostStateTrendRequest.setSceneId(null);
        hostStateTrendRequest.setElementIds(null);
        List<HostState> hostStates = hostStateService.getClusterHostStateTrend(hostStateTrendRequest);

        return ResultCommon.success(hostStates);
    }


    /**
     * elementIds若为多值，则只取第一个值
     * @param hostStateTrendRequest
     * @return
     */
    @ApiOperation(value = "节点主机状态趋势图")
    @GetMapping("/element_trend")
    public ServiceResult<List<HostState>> getElementHostStateTrend(HostStateTrendRequest hostStateTrendRequest) {
        List<HostState> hostStates = hostStateService.getElementHostStateTrend(hostStateTrendRequest);

        if (new Random().nextInt() % 2 == 0 && CollectionUtils.isNotEmpty(hostStates)) {
            for (int i = 0; i < hostStates.size(); i++) {
                if (hostStates.get(i).getMemoryPercent() > 0) {
                    hostStates.get(i).setMemoryPercent(100.0 + new Random().nextInt(100));
                }
            }
        }

        if (new Random().nextInt() % 2 == 0 && CollectionUtils.isNotEmpty(hostStates)) {
            for (int i = 0; i < hostStates.size(); i++) {
                if (hostStates.get(i).getDiskPercent() > 0) {
                    hostStates.get(i).setDiskPercent(100.0 + new Random().nextInt(100));
                }
            }
        }

        if (new Random().nextInt() % 2 == 0 && CollectionUtils.isNotEmpty(hostStates)) {
            for (int i = 0; i < hostStates.size(); i++) {
                if (hostStates.get(i).getCpuPercent() > 0) {
                    hostStates.get(i).setCpuPercent(new Random().nextInt(100) - 100.0);
                }
            }
        }

        return ResultCommon.success(hostStates);
    }

    public static Integer trend = 0;

    @ApiOperation(value = "节点主机状态趋势图")
    @GetMapping("/set_trend")
    public ServiceResult<Integer> setTrend(Integer trend) {
        this.trend = trend;
        return new ServiceResult<>(trend).asResult();
    }


    /**
     * 查询节点当前状态信息
     * @return
     */
    @ApiOperation(value = "节点主机状态")
    @GetMapping()
    public ServiceResult<List<HostState>> getElementHostState(BaseSceneElementPageRequest pageRequest) {
        pageRequest.setPage(1);
        pageRequest.setPageSize(1);
        List<HostState> hostStates = hostStateService.listHostStates(pageRequest);
        return ResultCommon.success(hostStates);
    }

    @ApiOperation(value = "节点主机状态")
    @GetMapping(value = "/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ServiceResult<List<XmlHostState>> getXmlOS(BaseSceneElementPageRequest pageRequest) {
        pageRequest.setPage(1);
        pageRequest.setPageSize(1);
        List<HostState> hostStates = hostStateService.listHostStates(pageRequest);
        List<XmlHostState> res = new ArrayList<>();
        hostStates.forEach(hostState -> {
            if (hostState != null) {
                XmlHostState xmlHostState = new XmlHostState();
                BeanUtils.copyProperties(hostState, xmlHostState);
                res.add(xmlHostState);
            }
        });
        return ResultCommon.success(res);
    }

    /**
     * 查询节点当前状态信息
     * @return
     */
    @ApiOperation(value = "节点主机状态")
    @GetMapping("/by_agent_name")
    public ServiceResult<List<HostState>> getHostStateByAgentName(AgentNameBasedRequest request) {
        request.setPage(1);
        request.setPageSize(1);
        List<HostState> hostStates = hostStateService.listHostStatesByAgentName(request);
        return ResultCommon.success(hostStates);
    }

    @ApiOperation(value = "节点端口信息")
    @GetMapping("/element_port")
    public ListObjServiceResult<Port> pagePorts(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        var res = hostStateService.pagePorts(listPageInfoQueryParams).asResult();
        var a1 = res.getData().getList();
        if (trend == 1 && CollectionUtils.isNotEmpty(a1)) {
            var x = a1.get(0);
            a1.add(0, x);
        }
        return new ListObjServiceResult<>(new ListObjResult<>(a1, a1.size())).asResult();
    }

    @ApiOperation(value = "节点进程信息")
    @GetMapping("/element_process")
    public ListObjServiceResult<Process> pageProcesses(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        var res = hostStateService.pageProcesses(listPageInfoQueryParams).asResult();
        var a1 = res.getData().getList();
        if (trend == 1 && CollectionUtils.isNotEmpty(a1)) {
            var x = a1.get(0);
            a1.add(0, x);

            Process process = new Process();
            BeanUtils.copyProperties(x, process);
            process.setTime(LocalDateTime.now().minusDays(1000));
            a1.add(process);
        }
        return new ListObjServiceResult<>(new ListObjResult<>(a1, a1.size())).asResult();
    }

    @ApiOperation(value = "节点网卡信息")
    @GetMapping("/element_netifaces")
    public ListObjServiceResult<Netiface> pageNetifaces(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        var res = hostStateService.pageNetifaces(listPageInfoQueryParams).asResult();
        var a1 = res.getData().getList();
        if (trend == 1 && CollectionUtils.isNotEmpty(a1)) {
            var x = a1.get(0);
            a1.add(0, x);
        }
        return new ListObjServiceResult<>(new ListObjResult<>(a1, a1.size())).asResult();
    }

}
