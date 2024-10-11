package cn.cyberpeace.mp.cscene.controller.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.request.load.*;
import cn.cyberpeace.mp.cscene.service.service.OutLoadServiceImpl;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Api(value = "外部接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_LOAD_API_PATH + "/outLoadFunction")
public class OutLoadController {

    @Autowired
    private OutLoadServiceImpl outLoadService;

    /**
     * 返回任务列表，包括任务序号，任务名称、任务描述、所使用的工具、实例状态、控制页面的端口、创建时间、创建者ID
     */
    @ApiOperation(value = "任务列表", notes = "")
    @GetMapping("/getTaskList")
    public ServiceResult<List> getTaskList(@RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize,
                                           @RequestParam(value = "pagenum", defaultValue = "1") Integer pageNum,
                                           @RequestParam(value = "keyword", required = false) String keyword,
                                           @RequestParam(value = "sortby", required = false) Boolean sortby) {
        JSONObject requestData = new JSONObject();
        requestData.put("pageNum", pageNum);
        requestData.put("pageSize", pageSize);
        requestData.put("keyword", keyword);
        requestData.put("sortby", sortby);
        List<JSONObject> list = outLoadService.getTaskList(requestData);
        return new ServiceResult<List>(list, BaseErrorCode.SUCCESS).asResult();
    }

    /**
     * 根据所使用的工具创建任务，主要流程包括相关联的实例创建，任务参数写入到数据库
     */
    @ApiOperation(value = "创建任务", notes = "")
    @PostMapping("/createTask")
    public ServiceResult createTask(@RequestBody OutLoadTaskCreateRequest requestData) {
        Map<String, Object> addAzurer = outLoadService.createTask(requestData);
        return new ServiceResult<>(addAzurer, BaseErrorCode.SUCCESS).asResult();
    }

    /**
     * 根据任务id获取任务的访问链接
     */
    @ApiOperation(value = "获取任务访问链接", notes = "")
    @GetMapping("/getTaskNet")
    public ServiceResult getTaskNet(@RequestParam(value = "task_id") String taskId) {
        Map<String, Object> data = outLoadService.getTaskNet(taskId);
        return new ServiceResult(data, BaseErrorCode.SUCCESS).asResult();
    }

    /**
     * 根据任务id修改任务的详细信息
     */
    @ApiOperation(value = "修改任务信息", notes = "")
    @PostMapping("/updateTaskInfo")
    public ServiceResult<Boolean> updateTaskInfo(@RequestBody OutLoadTaskUpdateRequest requestData) {
        Boolean updateTaskInfo = outLoadService.updateTaskInfo(requestData);
        return new ServiceResult(updateTaskInfo).asResult();
    }

    /**
     * 根据传入的ID删除任务，主要流程为删除实例和数据库中的条目
     */
    @ApiOperation(value = "删除任务", notes = "")
    @PostMapping("/deleteTask")
    public ServiceResult<Boolean> deleteTask(@RequestBody OutLoadTaskDeleteRequest requestData) {
        Boolean deleteTask = outLoadService.deleteTask(requestData);
        return new ServiceResult(deleteTask).asResult();
    }

    /**
     * 根据传入的任务ID重置任务，主要过程为根据镜像重新创建一个实例，然后修改任务信息保存到数据库，最后删除原实例
     */
    @ApiOperation(value = "重置任务", notes = "")
    @PostMapping("/resetTask")
    public ServiceResult<Boolean> resetTask(@RequestBody OutLoadTaskResetRequest requestData) {
        Boolean resetTask = outLoadService.resetTask(requestData);
        return new ServiceResult(resetTask).asResult();
    }

    /**
     * 根据传入的实例ID启动实例
     */
    @ApiOperation(value = "启动实例", notes = "")
    @PostMapping("/startInstance")
    public ServiceResult startInstance(@RequestBody JSONObject requestData) {
        Map<String, Object> startInstance = outLoadService.startInstance(requestData);
        return new ServiceResult(startInstance, BaseErrorCode.SUCCESS).asResult();
    }

    /**
     * 根据传入的实例ID关闭实例
     */
    @ApiOperation(value = "关闭实例", notes = "")
    @PostMapping("/stopInstance")
    public ServiceResult<Boolean> stopInstance(@RequestBody JSONObject requestData) {
        Boolean stopInstance = outLoadService.stopInstance(requestData);
        return new ServiceResult(stopInstance).asResult();
    }

    /**
     * 根据传入的实例ID暂停实例
     */
    @ApiOperation(value = "暂停实例", notes = "")
    @PostMapping("/pauseInstance")
    public ServiceResult<Boolean> pauseInstance(@RequestBody JSONObject requestData) {
        Boolean pauseInstance = outLoadService.pauseInstance(requestData);
        return new ServiceResult(pauseInstance).asResult();
    }

    /**
     * 根据传入的实例ID恢复实例
     */
    @ApiOperation(value = "恢复实例", notes = "")
    @PostMapping("/unpauseInstance")
    public ServiceResult<Boolean> unpauseInstance(@RequestBody JSONObject requestData) {
        Boolean unpauseInstance = outLoadService.unpauseInstance(requestData);
        return new ServiceResult(unpauseInstance).asResult();
    }

    /**
     * 根据传入的实例ID删除实例
     */
    @ApiOperation(value = "删除实例", notes = "")
    @PostMapping("/deleteInstance")
    public ServiceResult<Boolean> deleteInstance(@RequestBody JSONObject requestData) {
        Boolean deleteInstance = outLoadService.deleteInstance(requestData);
        return new ServiceResult(deleteInstance).asResult();
    }

    /**
     * 根据传入的实例ID查询当前状态
     */
    @ApiOperation(value = "查询实例状态", notes = "")
    @GetMapping("/getInstanceById")
    public ServiceResult getInstanceById(@RequestParam(value = "id") String id) {
        JSONObject data = outLoadService.getInstanceById(id);
        return new ServiceResult(data, BaseErrorCode.SUCCESS).asResult();
    }

    /**
     * 返回工具列表，包括工具编号，工具名称、工具描述、控制类型、控制端口、工具镜像名称、工具控制台默认运行的命令、工具的更新时间、工具的创建时间、创建者ID
     */
    @ApiOperation(value = "工具列表", notes = "")
    @GetMapping("/getToolList")
    public ServiceResult getToolList(@RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize,
                                     @RequestParam(value = "pagenum", defaultValue = "1") Integer pageNum,
                                     @RequestParam(value = "keyword", required = false) String keyword,
                                     @RequestParam(value = "sortby", required = false) Boolean sortby) {
        JSONObject requestData = new JSONObject();
        requestData.put("pageNum", pageNum);
        requestData.put("pageSize", pageSize);
        requestData.put("keyword", keyword);
        requestData.put("sortby", sortby);
        List list = outLoadService.getToolList(requestData);
        return new ServiceResult(list, BaseErrorCode.SUCCESS).asResult();
    }

    /**
     * 添加工具，导入tar包并将信息保存到数据库中
     */
    @ApiOperation(value = "添加工具", notes = "")
    @PostMapping("/addTool")
    public ServiceResult<Boolean> addTool(@RequestBody JSONObject requestData) {
        Boolean addTool = outLoadService.addTool(requestData);
        return new ServiceResult<Boolean>(addTool).asResult();
    }

    /**
     * 根据工具id修改工具的详细信息
     */
    @ApiOperation(value = "修改工具信息", notes = "")
    @PostMapping("/updateToolInfo")
    public ServiceResult<Boolean> updateToolInfo(@RequestBody JSONObject requestData) {
        Boolean updateToolInfo = outLoadService.updateToolInfo(requestData);
        return new ServiceResult<Boolean>(updateToolInfo).asResult();
    }

    /**
     * 工具镜像上传
     */
    @ApiOperation(value = "工具上传", notes = "")
    @PostMapping("/uploadTool")
    public ServiceResult<Boolean> uploadTool(@RequestParam(value = "file") MultipartFile file) {
        Boolean uploadTool = outLoadService.uploadTool(file);
        return new ServiceResult<Boolean>(uploadTool).asResult();
    }

    /**
     * 更新工具标签
     */
    @ApiOperation(value = "更新工具标签", notes = "")
    @PostMapping("/modifyLoadTags")
    public ServiceResult<Boolean> modifyLoadTags(@RequestBody OutLoadModifyTagsRequest request) {
        Boolean modifyLoadTags = outLoadService.modifyLoadTags(request);
        return new ServiceResult<Boolean>(modifyLoadTags).asResult();
    }

    /**
     * 根据工具id删除工具镜像
     */
    @ApiOperation(value = "工具删除", notes = "")
    @PostMapping("/deleteTool")
    public ServiceResult<Boolean> deleteTool(@RequestBody JSONObject requestData) {
        Boolean deleteTool = outLoadService.deleteTool(requestData);
        return new ServiceResult(deleteTool).asResult();
    }

    /**
     * 根据工具名称查询是否存在该工具
     */
    @ApiOperation(value = "检查工具名称是否可用", notes = "")
    @GetMapping("/checkNameVaild")
    public ServiceResult<JSONObject> checkNameVaild(@RequestParam(value = "name") String name) {
        JSONObject requestData = new JSONObject();
        requestData.put("name", name);
        JSONObject data = outLoadService.checkNameVaild(requestData);
        return new ServiceResult(data, BaseErrorCode.SUCCESS).asResult();
    }

    /**
     * 根据工具id获取工具的帮助文档
     */
    @ApiOperation(value = "获取工具帮助文档", notes = "")
    @GetMapping("/getReadmeByToolId")
    public ServiceResult<JSONObject> getReadmeByToolId(@RequestParam(value = "id") String id) {
        JSONObject requestData = new JSONObject();
        requestData.put("id", id);
        JSONObject data = outLoadService.getReadmeByToolId(requestData);
        return new ServiceResult(data, BaseErrorCode.SUCCESS).asResult();
    }

    /**
     * 根据工具id上传帮助文档
     */
    @ApiOperation(value = "工具帮助文档上传", notes = "")
    @PostMapping("/uploadReadme")
    public ServiceResult<Boolean> uploadReadme(@RequestParam(value = "file") MultipartFile file) {
        Boolean uploadReadme = outLoadService.uploadReadme(file);
        return new ServiceResult(uploadReadme).asResult();
    }
}
