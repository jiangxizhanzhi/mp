// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name FlowInfoController.java.
// File size 15999.
// File create date Fri Jul 12 12:55:30 CST 2024.
// File update date Fri Jul 12 12:55:30 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.bo.DeleteActuatorParams;
import cn.cyberpeace.mp.flow.bo.EditFlowTaskConfRequest;
import cn.cyberpeace.mp.flow.bo.FlowTaskCommonConf;
import cn.cyberpeace.mp.flow.bo.FlowTaskConf;
import cn.cyberpeace.mp.flow.bo.FlowTaskFlowConf;
import cn.cyberpeace.mp.flow.bo.FlowTaskIpv4Conf;
import cn.cyberpeace.mp.flow.bo.FlowTaskRateConf;
import cn.cyberpeace.mp.flow.bo.FlowTaskTcpConf;
import cn.cyberpeace.mp.flow.bo.StartFlowActuatorParams;
import cn.cyberpeace.mp.flow.bo.StartFlowActuatorResponse;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.model.CsceneConfigFlowTaskModel;
import cn.cyberpeace.mp.flow.repository.CsceneConfigFlowTaskMapper;
import cn.cyberpeace.mp.flow.request.CsceneConfigFlowTaskRequest;
import cn.cyberpeace.mp.flow.request.CsceneFlowTaskRequest;
import cn.cyberpeace.mp.flow.request.out.OutputCsceneInstanceFlowTaskRequest;
import cn.cyberpeace.mp.flow.request.out.OutputEditFlowTaskConfRequest;
import cn.cyberpeace.mp.flow.request.out.OutputEditFlowTaskConfResponse;
import cn.cyberpeace.mp.flow.request.out.OutputFlowInfoResponse;
import cn.cyberpeace.mp.flow.request.out.OutputFlowTaskPageResponse;
import cn.cyberpeace.mp.flow.request.out.OutputStartTaskRequest;
import cn.cyberpeace.mp.flow.request.queryparam.FlowInfoQueryParams;
import cn.cyberpeace.mp.flow.request.queryparam.FlowTaskPageQueryParams;
import cn.cyberpeace.mp.flow.response.CsceneFlowTaskResponse;
import cn.cyberpeace.mp.flow.response.FlowActuatorResponse;
import cn.cyberpeace.mp.flow.response.FlowInfoResponse;
import cn.cyberpeace.mp.flow.service.FlowActuatorService;
import cn.cyberpeace.mp.flow.service.FlowActuatorTaskService;
import cn.cyberpeace.mp.flow.service.FlowCsceneService;
import cn.cyberpeace.mp.flow.service.impl.FlowInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Api(tags = "对外提供的流量接口")
@RestController
@RequestMapping(ControllerConstant.OUT_API_PATH)
public class OutputFlowInfoController {

    @Autowired
    private FlowInfoService flowServiceImpl;

    @Resource
    private CsceneConfigFlowTaskMapper csceneConfigFlowTaskMapper;

    @Resource
    private FlowActuatorTaskService flowActuatorTaskService;

    @Resource
    private FlowCsceneService flowCsceneService ;

    @Resource
    private FlowActuatorService flowActuatorService;

    @ApiOperation("启动流量发生器")
    @PostMapping("/create_actuator")
    public ServiceResult<StartFlowActuatorResponse> createFlowActuator(@RequestBody StartFlowActuatorParams params) {
        var result = flowActuatorService.createFlowActuator(params);
        StartFlowActuatorResponse startFlowActuatorResponse = new StartFlowActuatorResponse();
        startFlowActuatorResponse.setId(result.getId());
        return new ServiceResult<>(startFlowActuatorResponse).asResult();
    }

    @ApiOperation("删除流量发生器")
    @PostMapping("/delete_actuator")
    public ServiceResult<Boolean> deleteFlowActuator(@RequestBody DeleteActuatorParams params) {
        flowActuatorService.deleteFlowActuator(params.getActuatorId());
        return new ServiceResult<>(true).asResult();
    }

    /**
     * 分页查询流量信息
     * @param
     * @return
     */
    @ApiOperation("分页查询流量信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", required = true, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页大小", required = true, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "interactive_type", value = "交互类型", required = false, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "名称", required = false, dataType = "String", paramType = "query")
    })
    @GetMapping("/page")
    public ListObjServiceResult<OutputFlowInfoResponse> pageFlowInfo(@RequestParam("page") Integer page,
                                                                     @RequestParam("page_size") Integer pageSize,
                                                                     @RequestParam(value = "interactive_type",required = false) Integer interactiveType,
                                                                     @RequestParam(value = "name",required = false) String name) {
        FlowInfoQueryParams params = new FlowInfoQueryParams();
        params.setPage(page);
        params.setPageSize(pageSize);
        params.setInteractiveType(interactiveType);
        params.setSearch(name);
        params.setSearchFields(new String[]{"name"});
        var res = flowServiceImpl.pageFlowInfo(params).asResult();

        List<FlowInfoResponse> data = res.getData().getList();
        Integer total = res.getData().getTotal();

        List<OutputFlowInfoResponse> outRes = new ArrayList<>();
        for (var m : data) {
            OutputFlowInfoResponse response = new OutputFlowInfoResponse();
            BeanUtils.copyProperties(m,response);
            outRes.add(response);
        }
        return new ListObjServiceResult<>(new ListObjResult<>(outRes, total)).asResult();
    }

    @ApiOperation("更新场景模板节点的流量任务")
    @PostMapping("/cscene_config/update_tasks")
    public ListObjServiceResult<CsceneFlowTaskResponse> updateTasks(@RequestBody CsceneFlowTaskRequest request) {

        var condition = new HumanCondition().filterEqual(CsceneConfigFlowTaskModel::getCsceneId, request.getId())
                .filterEqual(CsceneConfigFlowTaskModel::getCsceneNodeId, request.getNodeId());
        csceneConfigFlowTaskMapper.filterDelete(condition);

        List<CsceneConfigFlowTaskModel> models = new ArrayList<>();
        for (var task : request.getTasks()) {
            CsceneConfigFlowTaskModel model = new CsceneConfigFlowTaskModel();
            model.setContent(JsonCommon.stringify(task));
            model.setCsceneId(request.getId());
            model.setCsceneNodeId(request.getNodeId());
            models.add(model);
        }
        csceneConfigFlowTaskMapper.bulkCreate(models);

        List<CsceneFlowTaskResponse> result = new ArrayList<>();
        for (var m : models) {
            CsceneFlowTaskResponse response = JsonCommon.parse(m.getContent(), CsceneFlowTaskResponse.class);
            response.setTaskId(m.getId());
            response.setCsceneId(m.getCsceneId());
            response.setCsceneNodeId(m.getCsceneNodeId());

            result.add(response);
        }
        return new ListObjServiceResult<>(new ListObjResult<>(result, result.size())).asResult();
    }

    @ApiOperation("查询场景模板节点中已配置的全部流量任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景模板id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "node_id", value = "节点ID", required = true, dataType = "String", paramType = "query"),
    })
    @GetMapping("/cscene_config/task_list")
    public ListObjServiceResult<CsceneFlowTaskResponse> list(@RequestParam("id") String id,
                                                             @RequestParam("node_id")String nodeId) {
        CsceneConfigFlowTaskRequest request = new CsceneConfigFlowTaskRequest();
        request.setId(id);
        request.setNodeId(nodeId);
        request.setPage(1);
        request.setPageSize(1000);
        return flowCsceneService.list(request).asResult();
    }

    @ApiOperation("分页查询场景实例节点中已配置的流量任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "node_id", value = "节点ID", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "当前页码", required = true, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页大小", required = true, dataType = "Integer", paramType = "query")
    })
    @GetMapping("/cscene_instance/task_list")
    public ListObjServiceResult<OutputFlowTaskPageResponse> taskList(@RequestParam("id") String id,
                                                                     @RequestParam("node_id")String nodeId,
                                                                     @RequestParam("page") Integer page,
                                                                     @RequestParam("page_size")Integer pageSize) {
        FlowTaskPageQueryParams params = new FlowTaskPageQueryParams();
        var result = flowActuatorTaskService.taskList(params).asResult().getData();
        var list = result.asResult().getList();
        List<OutputFlowTaskPageResponse> outputList = new ArrayList<>();
        list.forEach(l -> {
            OutputFlowTaskPageResponse output = new OutputFlowTaskPageResponse();
            BeanUtils.copyProperties(l, output);
            outputList.add(output);
        });
        return new ListObjServiceResult<>(new ListObjResult<>(outputList, result.getTotal())).asResult();
    }

    /**
     * 流量任务配置查询
     * @param taskId
     * @return
     */
    @ApiOperation("流量任务配置查询")
    @ApiImplicitParam(name = "task_id", value = "流量任务ID", dataTypeClass = String.class)
    @GetMapping("/cscene_instance/task/conf/detail")
    public ServiceResult<OutputEditFlowTaskConfResponse> queryConfByTaskId(@RequestParam("task_id") String taskId) {
        var res = flowActuatorTaskService.queryConfByTaskId(taskId).getFlowTaskConf();
        Integer duration = res.getCommonConf().getDuration();
        Integer rate = res.getRateConf().getRateMax();
        var attackConfs = res.getAttackConfs();
        OutputEditFlowTaskConfResponse response = new OutputEditFlowTaskConfResponse();
        response.setAttackConfs(attackConfs);
        response.setRate(rate);
        response.setTaskId(taskId);
        response.setDuration(duration);
        return new ServiceResult<>(response).asResult();
    }

    @ApiOperation("编辑流量任务配置")
    @PostMapping("/task/conf/edit")
    public ServiceResult editTaskConf(@RequestBody OutputEditFlowTaskConfRequest request) {
        EditFlowTaskConfRequest editFlowTaskConfRequest = new EditFlowTaskConfRequest();
        editFlowTaskConfRequest.setTaskId(request.getTaskId());
        editFlowTaskConfRequest.setConfig(1);
        editFlowTaskConfRequest.setKeepRunning(1);
        editFlowTaskConfRequest.setRateUnlimited(0);
        editFlowTaskConfRequest.setReplay(1);
        editFlowTaskConfRequest.setFlowTaskConf(new FlowTaskConf());
        var flowTaskConf = new FlowTaskConf();
        editFlowTaskConfRequest.setFlowTaskConf(flowTaskConf);

        // 速率配置 使用 均匀分布
        FlowTaskRateConf flowTaskRateConf = new FlowTaskRateConf();
        flowTaskRateConf.setRateMax(request.getRate());
        flowTaskRateConf.setRateMin(request.getRate());
        flowTaskRateConf.setLoadMode(0);
        flowTaskConf.setRateConf(flowTaskRateConf);

        // 通用配置 仅使用时长
        FlowTaskCommonConf flowTaskCommonConf = new FlowTaskCommonConf();
        flowTaskCommonConf.setDuration(request.getDuration());
        flowTaskCommonConf.setJitter(0);
        flowTaskCommonConf.setLatency(0);
        flowTaskConf.setCommonConf(flowTaskCommonConf);

        // 流配置
        FlowTaskFlowConf flowTaskFlowConf = new FlowTaskFlowConf();
        FlowTaskIpv4Conf ipv4Conf = new FlowTaskIpv4Conf();
        ipv4Conf.setDip("");
        ipv4Conf.setSip("");
        ipv4Conf.setDscp("ff");
        ipv4Conf.setTtl(32);
        flowTaskFlowConf.setIpv4Conf(ipv4Conf);
        FlowTaskTcpConf tcpConf = new FlowTaskTcpConf();
        tcpConf.setMss(1460);
        flowTaskFlowConf.setTcpConf(tcpConf);
        flowTaskConf.setFlowConf(flowTaskFlowConf);

        // 攻击流量配置
        flowTaskConf.setAttackConfs(request.getAttackConfs());

        flowActuatorTaskService.editTaskConf(editFlowTaskConfRequest);
        return ResultCommon.success();
    }

    @ApiOperation("启动单个流量任务")
    @PostMapping("/task/start")
    public ServiceResult<Boolean> startTask(@RequestBody OutputStartTaskRequest request) {
        flowActuatorTaskService.startTask(request.getTaskId());
        return new ServiceResult<>(true).asResult();
    }

    @ApiOperation("关闭单个流量任务")
    @PostMapping("/task/stop")
    public ServiceResult<Boolean> stopTask(@RequestBody OutputStartTaskRequest request) {
        flowActuatorTaskService.stopTask(request.getTaskId());
        return new ServiceResult<>(true).asResult();
    }

}
