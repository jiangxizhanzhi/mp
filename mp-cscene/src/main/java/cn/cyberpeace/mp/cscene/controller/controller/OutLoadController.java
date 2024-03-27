package cn.cyberpeace.mp.cscene.controller.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.service.service.OutLoadServiceImpl;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "载荷页面", tags = "载荷页面管理")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_LOAD_API_PATH + "/outLoad")
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
                                           @RequestParam(value = "keyword", required = false) String keyWord,
                                           @RequestParam(value = "sortby", required = false) Boolean sortBy) {
        JSONObject requestData = new JSONObject();
        requestData.put("pageNum", pageNum);
        requestData.put("pageSize", pageSize);
        requestData.put("keyWord", keyWord);
        requestData.put("sortBy", sortBy);
        List<JSONObject> list = outLoadService.getTaskList(requestData);
        return new ServiceResult<List>(list, BaseErrorCode.SUCCESS).asResult();
    }

    /**
     * 根据所使用的工具创建任务，主要流程包括相关联的实例创建，任务参数写入到数据库
     */
    @ApiOperation(value = "创建任务", notes = "")
    @PostMapping("/createTask")
    public ServiceResult<Boolean> createTask(@RequestBody(required = true) JSONObject requestData) {
        Boolean addAzurer = outLoadService.createTask(requestData);
        if(addAzurer){
            return new ServiceResult<>(null, BaseErrorCode.SUCCESS);
        }else{
            return new ServiceResult<>(null, BaseErrorCode.ERROR);
        }
    }
}
