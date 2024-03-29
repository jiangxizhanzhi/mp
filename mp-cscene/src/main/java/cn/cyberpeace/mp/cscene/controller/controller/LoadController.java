package cn.cyberpeace.mp.cscene.controller.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoadSceneLog;
import cn.cyberpeace.mp.cscene.service.service.LoadServiceImpl;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.util.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Api(value = "载荷页面", tags = "载荷页面管理")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_LOAD_API_PATH + "/load")
public class LoadController {

    @Autowired
    private LoadServiceImpl LoadService;

    @ApiOperation(value = "载荷页面", notes = "")
    @GetMapping("/getLoadList")
    public ServiceResult<List> getLoadList(@RequestParam(value = "per_page", defaultValue = "10") Integer pageSize,
                                           @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
                                           @RequestParam(value = "actionType", required = false) String actionType,
                                           @RequestParam(value = "runTable", required = false) String runTable,
                                           @RequestParam(value = "toolName", required = false) String toolName,
                                           @RequestParam(value = "controlType", required = false) String controlType,
                                           @RequestParam(value = "source", required = false) String source) {
        JSONObject requestData = new JSONObject();
        requestData.put("pageNum", pageNum);
        requestData.put("pageSize", pageSize);
        requestData.put("actionType", actionType);
        requestData.put("runTable", runTable);
        requestData.put("toolName", toolName);
        requestData.put("controlType", controlType);
        requestData.put("source", source);
        List list = LoadService.getLoadList(requestData);
        return new ServiceResult<List>(list, BaseErrorCode.SUCCESS).asResult();
    }

    @ApiOperation(value = "查询载荷", notes = "")
    @GetMapping("/getLoadByPk")
    public ServiceResult getLoadList(@RequestParam(value = "pk", required = true) String pk) {
        JSONObject requestData = new JSONObject();
        requestData.put("pk", pk);
        List list = LoadService.getLoadList(requestData);
        if(list != null && list.size() == 1){
            return new ServiceResult(list.get(0), BaseErrorCode.SUCCESS).asResult();
        }else{
            return new ServiceResult(null, BaseErrorCode.ERROR,"ERROR").asResult();
        }
    }

    @ApiOperation(value = "修改载荷", notes = "")
    @PutMapping("/updateLoadByPk")
    public ServiceResult updateLoadByPk(@RequestBody(required = false) JSONObject requestData) {
        Boolean updateLoadByPk = LoadService.updateLoadByPk(requestData);
        if(updateLoadByPk){
            return new ServiceResult<>(null, BaseErrorCode.SUCCESS,"SUCCESS").asResult();
        }else{
            return new ServiceResult<>(null, BaseErrorCode.ERROR,"ERROR").asResult();
        }
    }

    @ApiOperation(value = "删除载荷", notes = "")
    @DeleteMapping("/deleteLoadByPk")
    public ServiceResult deleteLoadByPk(@RequestBody(required = false) JSONObject requestData) {
        Boolean deleteLoadByPk = LoadService.deleteLoadByPk(requestData);
        if(deleteLoadByPk){
            return new ServiceResult<>(null, BaseErrorCode.SUCCESS,"SUCCESS").asResult();
        }else{
            return new ServiceResult<>(null, BaseErrorCode.ERROR,"ERROR").asResult();
        }
    }

    @ApiOperation(value = "载荷数据同步", notes = "")
    @GetMapping("/synchronousData")
    public ServiceResult synchronousData() {
        Boolean synchronousData = LoadService.synchronousData();
        if(synchronousData){
            return new ServiceResult<>(null, BaseErrorCode.SUCCESS,"SUCCESS").asResult();
        }else{
            return new ServiceResult<>(null, BaseErrorCode.ERROR,"ERROR").asResult();
        }
    }

    @ApiOperation(value = "根据场景id获取绑定的载荷", notes = "")
    @GetMapping("/getLoadBySceneId")
    public ServiceResult getLoadBySceneId(@RequestParam(value = "sceneId", required = true) String sceneId) {
        List list = LoadService.getLoadBySceneId(sceneId);
        return new ServiceResult<>(list, BaseErrorCode.SUCCESS,"SUCCESS").asResult();
    }

    @ApiOperation(value = "载荷执行结果统计", notes = "")
    @GetMapping("/getLoadExecutionResultStatistics")
    public ServiceResult getLoadExecutionResultStatistics(@RequestParam(value = "sceneId", required = true) String sceneId) {
        JSONObject requestData = new JSONObject();
        requestData.put("sceneId", sceneId);
        Map<String,Integer> map = LoadService.getLoadExecutionResultStatistics(requestData);
        return new ServiceResult<>(map, BaseErrorCode.SUCCESS,"SUCCESS").asResult();
    }

    @ApiOperation(value = "载荷执行结果", notes = "")
    @GetMapping("/getLoadExecutionResult")
    public ServiceResult getLoadExecutionResult(@RequestParam(value = "sceneId", required = true) String sceneId,
                                                          @RequestParam(value = "beginTime", required = false) String beginTime,
                                                          @RequestParam(value = "overTime", required = false) String overTime) {
        JSONObject requestData = new JSONObject();
        requestData.put("sceneId", sceneId);
        //将time转换成LocaldateTime
        LocalDateTime beginTimeLocalDateTime = null;
        LocalDateTime overTimeLocalDateTime = null;
        if(beginTime != null && overTime != null){
            try {
                beginTimeLocalDateTime = LocalDateTime.parse(beginTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                overTimeLocalDateTime = LocalDateTime.parse(overTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                //比较时间
                if(beginTimeLocalDateTime.isAfter(overTimeLocalDateTime)){
                    return new ServiceResult<>(null, BaseErrorCode.ERROR,"结束时间不能小于开始时间");
                }
            } catch (Exception e) {
                return new ServiceResult<>(null, BaseErrorCode.ERROR,"开始时间转换时间类型错误");
            }
        }
        requestData.put("beginTime", beginTimeLocalDateTime.toString());
        requestData.put("overTime", overTimeLocalDateTime.toString());
        List<MpDevopsLoadSceneLog> list = LoadService.getLoadExecutionResult(requestData);
        return new ServiceResult<>(list, BaseErrorCode.SUCCESS,"SUCCESS").asResult();
    }

    @ApiOperation(value = "载荷执行结果详情", notes = "")
    @GetMapping("/getLoadExecutionResultByPk")
    public ServiceResult getLoadExecutionResultById(@RequestParam(value = "pk", required = true) String pk) {
        JSONObject requestData = new JSONObject();
        requestData.put("sceneId", pk);
        List<MpDevopsLoadSceneLog> list = LoadService.getLoadExecutionResult(requestData);
        if(list != null  && list.size() == 1){
            return new ServiceResult<>(list.get(0), BaseErrorCode.SUCCESS,"SUCCESS").asResult();
        }else{
            return new ServiceResult<>(null, BaseErrorCode.ERROR,"ERROR").asResult();
        }
    }

    @ApiOperation(value = "删除载荷执行结果", notes = "")
    @DeleteMapping("/deleteLoadExecutionResultByPk")
    public ServiceResult deleteLoadExecutionResultByPk(@RequestBody(required = false) JSONObject requestData) {
        Boolean deleteLoadExecutionResultByPk = LoadService.deleteLoadExecutionResultByPk(requestData);
        if(deleteLoadExecutionResultByPk){
            return new ServiceResult<>(null, BaseErrorCode.SUCCESS,"SUCCESS").asResult();
        }else{
            return new ServiceResult<>(null, BaseErrorCode.ERROR,"ERROR").asResult();
        }
    }

    @ApiOperation(value = "修改载荷执行结果", notes = "")
    @PutMapping("/updateLoadExecutionResultByPk")
    public ServiceResult updateLoadExecutionResultByPk(@RequestBody(required = false) JSONObject requestData) {
        Boolean updateLoadExecutionResultByPk = LoadService.updateLoadExecutionResultByPk(requestData);
        if(updateLoadExecutionResultByPk){
            return new ServiceResult<>(null, BaseErrorCode.SUCCESS,"SUCCESS").asResult();
        }else{
            return new ServiceResult<>(null, BaseErrorCode.ERROR,"ERROR").asResult();
        }
    }

    @ApiOperation(value = "新增载荷执行结果", notes = "")
    @PostMapping("/addLoadExecutionResultByPk")
    public ServiceResult addLoadExecutionResultByPk(@RequestBody(required = false) JSONObject requestData) {
        Boolean addLoadExecutionResultByPk = LoadService.addLoadExecutionResultByPk(requestData);
        if(addLoadExecutionResultByPk){
            return new ServiceResult<>(null, BaseErrorCode.SUCCESS,"SUCCESS").asResult();
        }else{
            return new ServiceResult<>(null, BaseErrorCode.ERROR,"ERROR").asResult();
        }
    }

    @ApiOperation(value = "载荷执行结果导入", notes = "")
    @PostMapping("/importLoadExecutionResult")
    public ServiceResult importLoadExecutionResult(@RequestParam(value = "file", required = true) MultipartFile file){
        Boolean importLoadExecutionResult = LoadService.importLoadExecutionResult(file);
        if(importLoadExecutionResult){
            return new ServiceResult<>(null, BaseErrorCode.SUCCESS,"SUCCESS").asResult();
        }else{
            return new ServiceResult<>(null, BaseErrorCode.ERROR,"ERROR").asResult();
        }
    }

}
