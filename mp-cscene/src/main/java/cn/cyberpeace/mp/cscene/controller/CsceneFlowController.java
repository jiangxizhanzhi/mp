package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.controller.BaseModelListControllerBase;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.request.flow.AddFlowTaskTaskRequest;
import cn.cyberpeace.mp.cscene.request.flow.EditFlowTaskConfRequest;
import cn.cyberpeace.mp.cscene.response.QueryFlowTaskConfResponse;
import cn.cyberpeace.mp.cscene.service.CsceneFlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: 场景流量
 * @Author: guquan
 * @Date：2023/5/8 14:56
 */
@Api(value = "场景流量", tags = "场景流量")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/cscene_flow")
@Data
public class CsceneFlowController implements BaseModelListControllerBase {

    @Autowired
    private CsceneFlowService csceneFlowService;

    /**
     * 新增流量任务
     * @param request
     * @return
     */
    @ApiOperation("新增流量任务")
    @PostMapping("/task/add")
    public ServiceResult addTask(@RequestBody AddFlowTaskTaskRequest request) {
        log.info("CsceneFlowController.addTask begin. request:{}", request);
        try {
            csceneFlowService.addTask(request);
        } catch (MpRuntimeException e) {
            return ResultCommon.fail(e.getServiceCode());
        }
        log.info("CsceneFlowController.addTask end.");
        return ResultCommon.success();
    }

    /**
     * 删除流量任务
     * @param id
     * @return
     */
    @ApiOperation("删除流量任务")
    @ApiImplicitParam(name = "id", value = "流量任务ID", dataTypeClass = String.class)
    @DeleteMapping("/task/delete")
    public ServiceResult deleteTask(@RequestParam String id){
        log.info("FlowInfoController.deleteTask begin. id:{}", id);
        csceneFlowService.deleteTask(id);
        log.info("FlowInfoController.deleteTask end");
        return ResultCommon.success();
    }

    /**
     * 编辑流量任务配置
     * @param request
     * @return
     */
    @ApiOperation("编辑流量任务配置")
    @PostMapping("/task/conf/edit")
    public ServiceResult editTaskConf(@RequestBody EditFlowTaskConfRequest request) {
        log.info("CsceneFlowController.editTaskConf begin. request:{}", request);
        csceneFlowService.editTaskConf(request);
        log.info("CsceneFlowController.editTaskConf end.");
        return ResultCommon.success();
    }

    /**
     * 流量任务配置查询
     * @param taskId
     * @return
     */
    @ApiOperation("流量任务配置查询")
    @ApiImplicitParam(name = "taskId", value = "流量任务ID", dataTypeClass = String.class)
    @GetMapping("/task/conf/detail")
    public ServiceResult<QueryFlowTaskConfResponse> queryConfByTaskId(@RequestParam String taskId) {
        log.info("FlowInfoController.queryConfByTaskId begin. taskId:{}", taskId);
        QueryFlowTaskConfResponse result = csceneFlowService.queryConfByTaskId(taskId);
        log.info("FlowInfoController.queryConfByTaskId end. result:{}", result);
        return ResultCommon.success(result);
    }
}
