package cn.cyberpeace.mp.devopscluster.controller;


import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.request.AssetManagementPageRequest;
import cn.cyberpeace.mp.devopscluster.request.AssetManagementRequest;
import cn.cyberpeace.mp.devopscluster.request.TaskManagementPageRequest;
import cn.cyberpeace.mp.devopscluster.request.TaskManagementRequest;
import cn.cyberpeace.mp.devopscluster.service.TaskManagementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/task_management")
@Api(tags = "任务管理")
public class TaskManagementController {


    @Autowired
    private TaskManagementService taskManagementService;


    @GetMapping("/page")
    @ApiOperation(value = "获取任务管理", notes = "")
    public ListObjServiceResult<Object> getPageTaskManagement(TaskManagementPageRequest taskManagementPageRequest){
         return new ListObjServiceResult<>(taskManagementService.getPageTaskManagement(taskManagementPageRequest)).asResult();
    }
    @GetMapping("")
    @ApiOperation(value = "获取单个任务管理", notes = "")
    public ServiceResult<Object> getTaskManagement(TaskManagementRequest taskManagementRequest){
       return taskManagementService.getTaskManagement(taskManagementRequest).asResult();
    }
    @PostMapping("")
    @ApiOperation(value = "新增任务管理", notes = "")
    public ServiceResult<Object> insertTaskManagement(@RequestBody TaskManagementRequest taskManagementRequests){
        return taskManagementService.insertTaskManagement(taskManagementRequests).asResult();
    }
    @PatchMapping
    @ApiOperation(value = "修改资产管理", notes = "")
    public ServiceResult<Object> patchTaskManagement(@RequestBody TaskManagementRequest taskManagementRequests){
        return taskManagementService.patchTaskManagement(taskManagementRequests).asResult();
    }
    @DeleteMapping
    @ApiOperation(value = "删除资产管理", notes = "")
    public ServiceResult<Object> deleteTaskManagement(@RequestBody String[] ids){
        return taskManagementService.deleteTaskManagement(ids).asResult();
    }

}
