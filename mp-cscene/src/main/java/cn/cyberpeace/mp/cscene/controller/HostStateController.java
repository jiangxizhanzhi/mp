 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HostStateController.java.
 // File size 6327.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.mptool.DatePattern;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.dto.scene.SceneAttCkTypeCountInfo;
import cn.cyberpeace.mp.cscene.request.agent.AgentNameBasedRequest;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.request.hoststate.HostStateTrendRequest;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.service.HostStateService;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionElementProcessDataModel;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionElementProcessDataMapper;
import cn.cyberpeace.mp.sceneplugincollection.service.param.TimeListPageInfoQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.util.DatetimeUtil;
import cn.cyberpeace.mp.situationaware.model.WazuhProcessModel;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

 /**
 * @ClassName HostStateController
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/11/22 11:22
 * @Version 1.0.0
 */
@Api(value = "主机状态监控", tags = "主机状态监控")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/monitor/hoststate")
@AuthTarget(TargetEnum.CSCENE_MONITOR_GROUP)
public class HostStateController {

    @Autowired
    private HostStateService hostStateService;

    /**
     * 默认查询场景所有节点
     * 忽略elementIds的值
     *
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
     *
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
     *
     * @param hostStateTrendRequest
     * @return
     */
    @ApiOperation(value = "节点主机状态趋势图")
    @GetMapping("/element_trend")
    public ServiceResult<List<HostState>> getElementHostStateTrend(HostStateTrendRequest hostStateTrendRequest) {
        List<HostState> hostStates = hostStateService.getElementHostStateTrend(hostStateTrendRequest);

        return ResultCommon.success(hostStates);
    }


    /**
     * getElementHostState
     *
     * @param pageRequest
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

    /**
     * getHostStateByAgentName
     *
     * @param request
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

    /**
     * pagePorts
     *
     * @param listPageInfoQueryParams
     * @return
     */
    @ApiOperation(value = "节点端口信息")
    @GetMapping("/element_port")
    public ListObjServiceResult<Port> pagePorts(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        return hostStateService.pagePorts(listPageInfoQueryParams).asResult();
    }

    /**
     * pageProcesses
     *
     * @param listPageInfoQueryParams
     * @return
     */
    @ApiOperation(value = "节点进程信息")
    @GetMapping("/element_process")
    public ListObjServiceResult<Process> pageProcesses(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        if (listPageInfoQueryParams.getStartTime() != null) {
            return hostStateService.pageProcesses(listPageInfoQueryParams).asResult();
        } else {
            CsceneInstanceService instanceService = BeanCommon.getBean(CsceneInstanceService.class);
            var intance = instanceService.getSceneId(listPageInfoQueryParams.getSceneIds().get(0));
            if (intance == null) {
                return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<Process>(), 0)).asResult();
            }
            SceneElementMapper sceneElementMapper = BeanCommon.getBean(SceneElementMapper.class);
            var condition = new HumanCondition().filterEqual(SceneElementModel::getScene, intance)
                    .filterEqual(SceneElementModel::getNodeId, listPageInfoQueryParams.getNodeIds().get(0));
            var model = sceneElementMapper.first(condition);
            String agentName = model.getId();
            CollectionElementProcessDataMapper dataMapper = BeanCommon.getBean(CollectionElementProcessDataMapper.class);
            var dataCondition = new HumanCondition().filterEqual(CollectionElementProcessDataModel::getAgentName, agentName)
                    .orderBy(CollectionElementProcessDataModel::getCreateTime,true);
            var listData = dataMapper.list(dataCondition);

            int page = listPageInfoQueryParams.getPage();
            int pageSize = listPageInfoQueryParams.getPerPage();
            int skip = (page - 1) * pageSize;
            log.info("skip:{}, limit:{}", skip, pageSize);
            List<CollectionElementProcessDataModel> resultList = listData.stream()
                    .filter(wazuhProcessModel -> Objects.nonNull(wazuhProcessModel)).skip(skip).limit(pageSize)
                    .collect(Collectors.toList());

            List<Process> processes = new ArrayList<>();
            for (CollectionElementProcessDataModel p : resultList) {
                WazuhProcessModel processModel = JsonCommon.parse(p.getData(), WazuhProcessModel.class);
                Process process = convertWazuhProcess2Process(processModel);
                if (p.getStatus() == 0) {
                    process.setState("stoped");
                    process.setTime(processModel.getTime());
                }
                processes.add(process);
            }
            return new ListObjServiceResult<>(new ListObjResult<>(processes, listData.size())).asResult();
        }
    }

    private Process convertWazuhProcess2Process(WazuhProcessModel wazuhProcess) {
        if (Objects.isNull(wazuhProcess)) {
             return null;
        }
        Process process = new Process();
        process.setState(wazuhProcess.getStatus());
        process.setPid(StringUtils.isNotBlank(wazuhProcess.getPid()) ? Integer.valueOf(wazuhProcess.getPid()) : null);
        process.setPpid(StringUtils.isNotBlank(wazuhProcess.getParentId()) ? Integer.valueOf(wazuhProcess.getParentId()) : null);
        process.setStartTime(getProcessStartTime(wazuhProcess.getCreateTime()));
        process.setVmSize(StringUtils.isNotBlank(wazuhProcess.getVms()) ? Long.valueOf(wazuhProcess.getVms()) : 0);
        process.setSize(StringUtils.isNotBlank(wazuhProcess.getVms()) ? Long.valueOf(wazuhProcess.getVms()) : 0);
        process.setPriority(wazuhProcess.getPriority());
        process.setCmd(wazuhProcess.getCommand());
        process.setEuser(wazuhProcess.getUsername());
        process.setName(wazuhProcess.getName());
        process.setDescription(wazuhProcess.getDescription());
        process.setTime(wazuhProcess.getTime());
        process.setLevel(wazuhProcess.getLevel());
        return process;
    }

    private String getProcessStartTime(String startTime) {
        if (StringUtils.isBlank(startTime)) {
            return null;
        }
        LocalDateTime localDateTime = DatetimeUtil.parseDateTimeFronLacunchOutput(startTime);
        String dateTime = DatePattern.NORM_DATETIME_FORMAT.format(localDateTime);
        return dateTime;
    }

    /**
     * pageNetifaces
     *
     * @param listPageInfoQueryParams
     * @return
     */
    @ApiOperation(value = "节点网卡信息")
    @GetMapping("/element_netifaces")
    public ListObjServiceResult<Netiface> pageNetifaces(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        return hostStateService.pageNetifaces(listPageInfoQueryParams).asResult();
    }

}
