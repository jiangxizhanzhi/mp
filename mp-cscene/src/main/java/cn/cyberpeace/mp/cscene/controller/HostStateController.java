package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.model.SceneCycleDataModel;
import cn.cyberpeace.mp.cscene.repository.SceneCycleDataModelMapper;
import cn.cyberpeace.mp.cscene.request.agent.AgentNameBasedRequest;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.request.hoststate.HostStateTrendRequest;
import cn.cyberpeace.mp.cscene.service.HostStateService;
import cn.cyberpeace.mp.cscene.service.multirange.testrange.TestRangeApiProxy;
import cn.cyberpeace.mp.sceneplugincollection.service.param.ListPageInfoQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.TimeListPageInfoQueryParams;
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

import java.util.ArrayList;
import java.util.List;

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
    @Autowired
    private TestRangeApiProxy testRangeApiProxy;

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
            var mapper = BeanCommon.getBean(SceneCycleDataModelMapper.class);
            String sceneId = hostStateTrendRequest.getSceneId();
            var result = mapper.list(new HumanCondition().filterEqual(SceneCycleDataModel::getScene, sceneId)
                                             .filterEqual(SceneCycleDataModel::getSceneElement, "")
                                             .filterEqual(SceneCycleDataModel::getType, SceneCycleDataModel.HOST)
                                             .orderBy(SceneCycleDataModel::getPk).pagination(1, 20));
            List<HostState> processes = new ArrayList<>();
            for (SceneCycleDataModel model : result) {
                var p = JsonCommon.parse(model.getData(), HostState.class);
                processes.add(p);
            }
            return ResultCommon.success(processes);
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
     *
     * @param hostStateTrendRequest
     * @return
     */
    @ApiOperation(value = "节点主机状态趋势图")
    @GetMapping("/element_trend")
    public ServiceResult<List<HostState>> getElementHostStateTrend(HostStateTrendRequest hostStateTrendRequest) {
//        if (testRangeApiProxy.elementExistInCurrentScene(hostStateTrendRequest)) {
//            List<HostState> hostStates = hostStateService.getElementHostStateTrend(hostStateTrendRequest);
//            return ResultCommon.success(hostStates);
//        } else {
//            return testRangeApiProxy.getElementHostStateTrend(hostStateTrendRequest);
//        }

        var mapper = BeanCommon.getBean(SceneCycleDataModelMapper.class);
        String sceneId = hostStateTrendRequest.getSceneId();
        String nodeId = hostStateTrendRequest.getElementIds().get(0);
        var result = mapper.list(new HumanCondition().filterEqual(SceneCycleDataModel::getScene, sceneId)
                                         .filterEqual(SceneCycleDataModel::getSceneElement, nodeId)
                                         .filterEqual(SceneCycleDataModel::getType, SceneCycleDataModel.HOST)
                                         .reverseOrderBy(SceneCycleDataModel::getPk).pagination(1, 20));
        List<HostState> processes = new ArrayList<>();
        for (SceneCycleDataModel model : result) {
            var p = JsonCommon.parse(model.getData(), HostState.class);
            processes.add(p);
        }
        return ResultCommon.success(processes);

    }


    /**
     * 查询节点当前状态信息
     *
     * @return
     */
    @ApiOperation(value = "节点主机状态")
    @GetMapping()
    public ServiceResult<List<HostState>> getElementHostState(BaseSceneElementPageRequest pageRequest) {
        if (testRangeApiProxy.elementExistInCurrentScene(pageRequest.toBaseSceneElementRequest())) {
            pageRequest.setPage(1);
            pageRequest.setPageSize(1);
            List<HostState> hostStates = hostStateService.listHostStates(pageRequest);
            return ResultCommon.success(hostStates);
        } else {
            pageRequest.setPage(1);
            pageRequest.setPageSize(1);
            return testRangeApiProxy.getElementHostState(pageRequest);
        }

    }

    /**
     * 查询节点当前状态信息
     *
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
        var mapper = BeanCommon.getBean(SceneCycleDataModelMapper.class);
        String sceneId = listPageInfoQueryParams.getSceneIds().get(0);
        String nodeId = listPageInfoQueryParams.getNodeIds().get(0);
        var result = mapper.list(new HumanCondition().filterEqual(SceneCycleDataModel::getScene, sceneId)
                                         .filterEqual(SceneCycleDataModel::getSceneElement, nodeId)
                                         .filterEqual(SceneCycleDataModel::getType, SceneCycleDataModel.PORT));
        List<Port> processes = new ArrayList<>();
        for (SceneCycleDataModel model : result) {
            var p = JsonCommon.parse(model.getData(), Port.class);
            processes.add(p);
        }
        return new ListObjServiceResult<>(new ListObjResult<>(processes, processes.size())).asResult();
//        if (testRangeApiProxy.elementExistInCurrentScene(toBaseSceneElementRequest(listPageInfoQueryParams))) {
//            return hostStateService.pagePorts(listPageInfoQueryParams).asResult();
//        } else {
//            return testRangeApiProxy.pagePorts(listPageInfoQueryParams);
//        }
    }

    @ApiOperation(value = "节点进程信息")
    @GetMapping("/element_process")
    public ListObjServiceResult<Process> pageProcesses(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        var mapper = BeanCommon.getBean(SceneCycleDataModelMapper.class);
        String sceneId = listPageInfoQueryParams.getSceneIds().get(0);
        String nodeId = listPageInfoQueryParams.getNodeIds().get(0);
        var result = mapper.list(new HumanCondition().filterEqual(SceneCycleDataModel::getScene, sceneId)
                                         .filterEqual(SceneCycleDataModel::getSceneElement, nodeId)
                                         .filterEqual(SceneCycleDataModel::getType, SceneCycleDataModel.PROCESS));
        List<Process> processes = new ArrayList<>();
        for (SceneCycleDataModel model : result) {
            var p = JsonCommon.parse(model.getData(), Process.class);
            processes.add(p);
        }
        return new ListObjServiceResult<>(new ListObjResult<>(processes, processes.size())).asResult();

//        if (testRangeApiProxy.elementExistInCurrentScene(toBaseSceneElementRequest(listPageInfoQueryParams))) {
//            return hostStateService.pageProcesses(listPageInfoQueryParams).asResult();
//        } else {
//            return testRangeApiProxy.pageProcesses(listPageInfoQueryParams);
//        }
    }

    @ApiOperation(value = "节点网卡信息")
    @GetMapping("/element_netifaces")
    public ListObjServiceResult<Netiface> pageNetifaces(TimeListPageInfoQueryParams listPageInfoQueryParams) {
//        if (testRangeApiProxy.elementExistInCurrentScene(toBaseSceneElementRequest(listPageInfoQueryParams))) {
//            return hostStateService.pageNetifaces(listPageInfoQueryParams).asResult();
//        } else {
//            return testRangeApiProxy.pageNetifaces(listPageInfoQueryParams);
//        }

        var mapper = BeanCommon.getBean(SceneCycleDataModelMapper.class);
        String sceneId = listPageInfoQueryParams.getSceneIds().get(0);
        String nodeId = listPageInfoQueryParams.getNodeIds().get(0);
        var result = mapper.list(new HumanCondition().filterEqual(SceneCycleDataModel::getScene, sceneId)
                                         .filterEqual(SceneCycleDataModel::getSceneElement, nodeId)
                                         .filterEqual(SceneCycleDataModel::getType, SceneCycleDataModel.NET));
        List<Netiface> processes = new ArrayList<>();
        for (SceneCycleDataModel model : result) {
            var p = JsonCommon.parse(model.getData(), Netiface.class);
            processes.add(p);
        }
        return new ListObjServiceResult<>(new ListObjResult<>(processes, processes.size())).asResult();
    }


    public BaseSceneElementRequest toBaseSceneElementRequest(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        BaseSceneElementRequest baseSceneElementRequest = new BaseSceneElementRequest();
        baseSceneElementRequest.setElementIds(listPageInfoQueryParams.getNodeIds());
        baseSceneElementRequest.setSceneId(listPageInfoQueryParams.getSceneIds().get(0));
        return baseSceneElementRequest;
    }

}
