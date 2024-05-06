package cn.cyberpeace.mp.cscene.controller.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoad;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoadSceneLog;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoadSource;
import cn.cyberpeace.mp.cscene.service.service.LoadServiceImpl;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    /*
     * 载荷页面
     * */
    @ApiOperation(value = "载荷页面", notes = "")
    @GetMapping("/getLoadList")
    public ListObjServiceResult getLoadList(@RequestParam(value = "per_page", defaultValue = "10") Integer pageSize,
                                            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
                                            @RequestParam(value = "action_type", required = false) String actionType,
                                            @RequestParam(value = "run_table", required = false) String runTable,
                                            @RequestParam(value = "tool_name", required = false) String toolName,
                                            @RequestParam(value = "control_type", required = false) String controlType,
                                            @RequestParam(value = "source", required = false) String source) {
        JSONObject requestData = new JSONObject();
        requestData.put("pageNum", pageNum);
        requestData.put("pageSize", pageSize);
        requestData.put("actionType", actionType);
        requestData.put("runTable", runTable);
        requestData.put("toolName", toolName);
        requestData.put("controlType", controlType);
        requestData.put("source", source);
        JSONObject result = LoadService.getLoadList(requestData);
        return new ListObjServiceResult((List) result.get("source"), (int) result.get("total")).asResult();
    }

    /*
     * 载荷列表(ojj调用)
     * */
    @ApiOperation(value = "载荷列表", notes = "")
    @GetMapping("/getLoadListByMp")
    public ListObjServiceResult getLoadListByMp() {
        JSONObject requestData = new JSONObject();
        requestData.put("pageNum", 1);
        requestData.put("pageSize", 1000);
        JSONObject result = LoadService.getLoadList(requestData);
        return new ListObjServiceResult((List) result.get("source"), (int) result.get("total")).asResult();
    }

    /*
     * 根据pks获取载荷列表
     * */
    @ApiOperation(value = "根据pks获取载荷列表", notes = "")
    @PostMapping("/getToolListByloadPks")
    public ServiceResult getToolListByloadPks(@RequestBody(required = false) MpDevopsLoad requestData) {
        JSONObject jsonObject = JsonCommon.snakeParse(JsonCommon.stringify(requestData), JSONObject.class);
        if (requestData != null) {
            List<MpDevopsLoad> getToolListByloadPks = LoadService.getToolListByloadPks(jsonObject);
            return new ServiceResult(getToolListByloadPks, BaseErrorCode.SUCCESS).asResult();
        }
        return new ServiceResult(null, BaseErrorCode.PARAMS_INVALID).asResult();
    }

    /*
     * 查询载荷
     * */
    @ApiOperation(value = "查询载荷", notes = "")
    @GetMapping("/getLoadByPk")
    public ServiceResult getLoadList(@RequestParam(value = "pk") String pk) {
        JSONObject requestData = new JSONObject();
        requestData.put("pk", pk);
        JSONObject result = LoadService.getLoadList(requestData);
        if (result != null && result.get("source") != null && ((List) result.get("source")).size() == 1) {
            MpDevopsLoad load = (MpDevopsLoad) ((List) result.get("source")).get(0);
            List loopholes = LoadService.getBindLoopholes(load.getPk());
            load.setLoopholes(loopholes);
            return new ServiceResult(load, BaseErrorCode.SUCCESS).asResult();
        } else {
            return new ServiceResult(null, BaseErrorCode.DATA_NOT_EXIST, "不存在该载荷").asResult();
        }
    }

    /*
     * 修改载荷
     * */
    @ApiOperation(value = "修改载荷", notes = "")
    @PatchMapping("/updateLoadByPk")
    public ServiceResult updateLoadByPk(@RequestBody(required = false) MpDevopsLoad requestData) {
        JSONObject jsonObject = JsonCommon.snakeParse(JsonCommon.stringify(requestData), JSONObject.class);
        Boolean updateLoadByPk = LoadService.updateLoadByPk(jsonObject);
        if (updateLoadByPk) {
            return new ServiceResult(null, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
        } else {
            return new ServiceResult(null, BaseErrorCode.ERROR, "修改失败").asResult();
        }
    }

    /*
     * 新增载荷
     * */
    @ApiOperation(value = "新增载荷", notes = "")
    @PostMapping("/addLoad")
    public ServiceResult addLoad(@RequestBody(required = false) MpDevopsLoad requestData) {
        JSONObject jsonObject = JsonCommon.snakeParse(JsonCommon.stringify(requestData), JSONObject.class);
        ServiceResult addLoad = LoadService.addLoad(jsonObject);
        return addLoad;
    }

    /*
     * 删除载荷
     * */
    @ApiOperation(value = "删除载荷", notes = "")
    @DeleteMapping("/deleteLoadByPk")
    public ServiceResult deleteLoadByPk(@RequestBody(required = false) MpDevopsLoad requestData) {
        JSONObject jsonObject = JsonCommon.snakeParse(JsonCommon.stringify(requestData), JSONObject.class);
        Boolean deleteLoadByPk = LoadService.deleteLoadByPk(jsonObject);
        if (deleteLoadByPk) {
            return new ServiceResult(null, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
        } else {
            return new ServiceResult(null, BaseErrorCode.DATA_NOT_EXIST, "删除失败,请检查列表中是否存在该数据").asResult();
        }
    }

    /*
     * 载荷数据同步
     * */
    @ApiOperation(value = "载荷数据同步", notes = "")
    @GetMapping("/synchronousData")
    public ServiceResult synchronousData() {
        JSONObject synchronousData = LoadService.synchronousData();
        if (synchronousData != null) {
            return new ServiceResult(synchronousData, BaseErrorCode.SUCCESS, "载荷数据同步成功").asResult();
        } else {
            return new ServiceResult(null, BaseErrorCode.ERROR, "载荷数据同步失败").asResult();
        }
    }

    /*
     * 载荷数据同步设置查看
     * */
    @ApiOperation(value = "载荷数据同步设置查看", notes = "")
    @GetMapping("/getSynchronousDataSource")
    public ServiceResult getSynchronousDataSource(@RequestParam(value = "pk", required = false) String pk) {
        JSONObject requestData = new JSONObject();
        requestData.put("pk", pk);
        MpDevopsLoadSource result = LoadService.getSynchronousDataSource(requestData);
        return new ServiceResult(result, BaseErrorCode.SUCCESS).asResult();
    }

    /*
     * 载荷数据同步设置保存
     * */
    @ApiOperation(value = "载荷数据同步设置保存", notes = "")
    @PostMapping("/synchronousDataSource")
    public ServiceResult synchronousDataSource(@RequestBody(required = false) MpDevopsLoadSource requestData) {
        JSONObject jsonObject = JsonCommon.snakeParse(JsonCommon.stringify(requestData), JSONObject.class);
        Boolean synchronousDataSource = LoadService.synchronousDataSource(jsonObject);
        if (synchronousDataSource) {
            return new ServiceResult(null, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
        } else {
            return new ServiceResult(null, BaseErrorCode.ERROR, "载荷数据同步设置保存失败").asResult();
        }
    }

//**********************************************************************************************************//

    /*
     * 根据场景id获取绑定的载荷
     * */
    @ApiOperation(value = "根据场景id获取绑定的载荷", notes = "")
    @GetMapping("/getLoadBySceneId")
    public ServiceResult getLoadBySceneId(@RequestParam(value = "scene_id") String sceneId) {
        List list = LoadService.getLoadBySceneId(sceneId);
        return new ServiceResult(list, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
    }

    /*
     * 载荷执行结果统计
     * */
    @ApiOperation(value = "载荷执行结果统计", notes = "")
    @PostMapping("/getLoadExecutionResultStatistics")
    public ServiceResult getLoadExecutionResultStatistics(@RequestBody(required = false) MpDevopsLoadSceneLog requestData) {
        JSONObject request = JsonCommon.snakeParse(JsonCommon.stringify(requestData), JSONObject.class);
        String sceneId = request.get("sceneId") != null && !request.get("sceneId").toString().equals("") ? request.get("sceneId").toString() : "";
        if (sceneId.equals("")) {
            return new ListObjServiceResult(null, BaseErrorCode.PARAMS_INVALID, "sceneId不能为空").asResult();
        }
        //将time转换成LocaldateTime
        LocalDateTime beginTimeLocalDateTime = null;
        LocalDateTime overTimeLocalDateTime = null;
        if (request.get("beginTime") != null && request.get("overTime") != null) {
            try {
                beginTimeLocalDateTime = LocalDateTime.parse(request.get("beginTime").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                overTimeLocalDateTime = LocalDateTime.parse(request.get("overTime").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                //比较时间
                if (beginTimeLocalDateTime.isAfter(overTimeLocalDateTime)) {
                    return new ListObjServiceResult(null, BaseErrorCode.PARAMS_INVALID, "结束时间不能小于开始时间");
                }
            } catch (Exception e) {
                return new ListObjServiceResult(null, BaseErrorCode.PARAMS_INVALID, "开始时间转换时间类型错误");
            }
        }
        request.put("beginTime", beginTimeLocalDateTime != null ? beginTimeLocalDateTime.toString() : null);
        request.put("overTime", overTimeLocalDateTime != null ? overTimeLocalDateTime.toString() : null);
        Map<String, Integer> map = LoadService.getLoadExecutionResultStatistics(request);
        return new ServiceResult(map, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
    }

    /*
     * 载荷执行结果
     * */
    @ApiOperation(value = "载荷执行结果", notes = "")
    @PostMapping("/getLoadExecutionResult")
    public ListObjServiceResult getLoadExecutionResult(@RequestBody(required = false) MpDevopsLoadSceneLog requestData) {
        JSONObject request = JsonCommon.snakeParse(JsonCommon.stringify(requestData), JSONObject.class);
        String sceneId = request.get("sceneId") != null && !request.get("sceneId").toString().equals("") ? request.get("sceneId").toString() : "";
        if (sceneId.equals("")) {
            return new ListObjServiceResult(null, BaseErrorCode.PARAMS_INVALID, "sceneId不能为空").asResult();
        }
        //将time转换成LocaldateTime
        LocalDateTime beginTimeLocalDateTime = null;
        LocalDateTime overTimeLocalDateTime = null;
        if (request.get("beginTime") != null && request.get("overTime") != null) {
            try {
                beginTimeLocalDateTime = LocalDateTime.parse(request.get("beginTime").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                overTimeLocalDateTime = LocalDateTime.parse(request.get("overTime").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                //比较时间
                if (beginTimeLocalDateTime.isAfter(overTimeLocalDateTime)) {
                    return new ListObjServiceResult(null, BaseErrorCode.PARAMS_INVALID, "结束时间不能小于开始时间");
                }
            } catch (Exception e) {
                return new ListObjServiceResult(null, BaseErrorCode.PARAMS_INVALID, "开始时间转换时间类型错误");
            }
        }
        request.put("beginTime", beginTimeLocalDateTime != null ? beginTimeLocalDateTime.toString() : null);
        request.put("overTime", overTimeLocalDateTime != null ? overTimeLocalDateTime.toString() : null);
        List<MpDevopsLoadSceneLog> list = LoadService.getLoadExecutionResult(request);
        return new ListObjServiceResult(list, list.size()).asResult();
    }

    /*
     * 载荷执行结果详情
     * */
    @ApiOperation(value = "载荷执行结果详情", notes = "")
    @GetMapping("/getLoadExecutionResultByPk")
    public ServiceResult getLoadExecutionResultById(@RequestParam(value = "pk") String pk) {
        JSONObject requestData = new JSONObject();
        requestData.put("pk", pk);
        List<MpDevopsLoadSceneLog> list = LoadService.getLoadExecutionResult(requestData);
        if (list != null && list.size() == 1) {
            return new ServiceResult(list.get(0), BaseErrorCode.SUCCESS, "SUCCESS").asResult();
        } else {
            return new ServiceResult(null, BaseErrorCode.ERROR, "查询载荷执行结果详情失败").asResult();
        }
    }

    /*
     * 删除载荷执行结果
     * */
    @ApiOperation(value = "删除载荷执行结果", notes = "")
    @DeleteMapping("/deleteLoadExecutionResultByPk")
    public ServiceResult deleteLoadExecutionResultByPk(@RequestBody(required = false) MpDevopsLoadSceneLog request) {
        JSONObject requestData = JsonCommon.snakeParse(JsonCommon.stringify(request), JSONObject.class);
        Boolean deleteLoadExecutionResultByPk = LoadService.deleteLoadExecutionResultByPk(requestData);
        if (deleteLoadExecutionResultByPk) {
            return new ServiceResult(null, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
        } else {
            return new ServiceResult(null, BaseErrorCode.ERROR, "删除载荷执行结果失败").asResult();
        }
    }

    /*
     * 修改载荷执行结果
     * */
    @ApiOperation(value = "修改载荷执行结果", notes = "")
    @PutMapping("/updateLoadExecutionResultByPk")
    public ServiceResult updateLoadExecutionResultByPk(@RequestBody(required = false) MpDevopsLoadSceneLog request) {
        JSONObject requestData = JsonCommon.snakeParse(JsonCommon.stringify(request), JSONObject.class);
        Boolean updateLoadExecutionResultByPk = LoadService.updateLoadExecutionResultByPk(requestData);
        if (updateLoadExecutionResultByPk) {
            return new ServiceResult(null, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
        } else {
            return new ServiceResult(null, BaseErrorCode.ERROR, "修改载荷执行结果失败").asResult();
        }
    }

    /*
     * 新增载荷执行结果
     * */
    @ApiOperation(value = "新增载荷执行结果", notes = "")
    @PostMapping("/addLoadExecutionResult")
    public ServiceResult addLoadExecutionResult(@RequestBody(required = false) MpDevopsLoadSceneLog request) {
        JSONObject requestData = JsonCommon.snakeParse(JsonCommon.stringify(request), JSONObject.class);
        if (requestData != null) {
            Boolean addLoadExecutionResult = LoadService.addLoadExecutionResult(requestData);
            if (addLoadExecutionResult) {
                return new ServiceResult(null, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
            }
        }
        return new ServiceResult(null, BaseErrorCode.ERROR, "新增载荷执行结果失败").asResult();
    }

    /*
     * 批量新增载荷执行结果
     * */
    @ApiOperation(value = "批量新增载荷执行结果", notes = "")
    @PostMapping("/addLoadExecutionOutResult")
    public ServiceResult addLoadExecutionOutResult(@RequestBody(required = false) List<MpDevopsLoadSceneLog> requestData) {
        if (requestData != null) {
            Boolean addLoadExecutionOutResult = LoadService.addLoadExecutionOutResult(requestData);
            if (addLoadExecutionOutResult) {
                return new ServiceResult(null, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
            }
        }
        return new ServiceResult(null, BaseErrorCode.ERROR, "批量新增载荷执行结果失败").asResult();
    }

    /*
     * 载荷执行结果导入
     * */
    @ApiOperation(value = "载荷执行结果导入", notes = "")
    @PostMapping("/importLoadExecutionResult")
    public ServiceResult importLoadExecutionResult(@RequestParam(value = "file") MultipartFile file) throws IOException {
        Boolean importLoadExecutionResult = LoadService.importLoadExecutionResult(file);
        if (importLoadExecutionResult) {
            return new ServiceResult(null, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
        } else {
            return new ServiceResult(null, BaseErrorCode.ERROR, "载荷执行结果导入失败").asResult();
        }
    }

    /*
     * 载荷执行结果导出
     * */
    @ApiOperation(value = "载荷执行结果导出", notes = "")
    @PostMapping("/exportLoadExecutionResult")
    public ServiceResult exportLoadExecutionResult(HttpServletRequest request, HttpServletResponse response,
                                                   @RequestBody(required = false) MpDevopsLoadSceneLog requestData) throws IOException {
        Boolean exportLoadExecutionResult = LoadService.exportLoadExecutionResult(request, response, requestData);
        if (exportLoadExecutionResult) {
            return new ServiceResult(null, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
        } else {
            return new ServiceResult(null, BaseErrorCode.ERROR, "载荷执行结果导出失败").asResult();
        }
    }

    /*
     * 阶段测试绑定载荷工具
     * */
    @ApiOperation(value = "阶段测试绑定载荷工具", notes = "")
    @PostMapping("/stageBindLoad")
    public ServiceResult stageBindLoad(@RequestBody(required = false) JSONObject requestData) {
        if (requestData != null && requestData.get("addType") != null && !requestData.get("addType").toString().equals("")) {
            String addType = requestData.get("addType").toString();
            if (addType.equals("name") || addType.equals("loopholes") || addType.equals("tactics")) {
                Boolean stageBindLoad = LoadService.stageBindLoad(addType, requestData);
                if (stageBindLoad) {
                    return new ServiceResult(null, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
                }
            }
        }
        return new ServiceResult(null, BaseErrorCode.PARAMS_INVALID, "参数无效").asResult();
    }

    /**
     * 根据靶标漏洞List获取载荷
     */
    @ApiOperation(value = "根据靶标漏洞List获取载荷", notes = "")
    @PostMapping("/getToolByLoopholes")
    public ServiceResult getToolByLoopholes(@RequestBody JSONObject requestData) {
        if (requestData != null) {
            List<MpDevopsLoad> getToolByLoopholes = LoadService.getToolByLoopholes(requestData);
            return new ServiceResult(getToolByLoopholes, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
        } else {
            return new ServiceResult(null, BaseErrorCode.ERROR, "根据靶标漏洞List获取载荷失败");
        }
    }

    /**
     * 根据关联技战术获取载荷
     */
    @ApiOperation(value = "根据关联技战术获取载荷", notes = "")
    @GetMapping("/getToolBytactics")
    public ServiceResult getToolBytactics(@RequestParam(value = "tactics", required = false) String tactics) {
        JSONObject requestData = new JSONObject();
        requestData.put("tactics", tactics);
        if (requestData != null) {
            List<MpDevopsLoad> getToolBytactics = LoadService.getToolBytactics(requestData);
            return new ServiceResult(getToolBytactics, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
        } else {
            return new ServiceResult(null, BaseErrorCode.ERROR, "根据关联技战术获取载荷失败");
        }
    }

    /**
     * 阶段载荷接入(根据taskId获取访问链接)
     */
    @ApiOperation(value = "阶段载荷接入", notes = "")
    @GetMapping("/getTaskNet")
    public ServiceResult getTaskNet(@RequestParam(value = "taskId", required = false) String taskId,
                                    @RequestParam(value = "instanceId", required = false) String instanceId) {
        JSONObject requestData = new JSONObject();
        requestData.put("taskId", taskId);
        requestData.put("instanceId", instanceId);
        if (requestData != null) {
            Map<String, Object> getTaskNet = LoadService.getTaskNet(requestData);
            if (getTaskNet != null) {
                return new ServiceResult(getTaskNet, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
            }
        }
        return new ServiceResult(null, BaseErrorCode.ERROR, "阶段载荷接入失败");
    }

    /**
     * 阶段载荷执行
     */
    @ApiOperation(value = "阶段载荷执行", notes = "")
    @GetMapping("/startTool")
    public ServiceResult startTool(@RequestParam(value = "taskId", required = false) String taskId,
                                   @RequestParam(value = "sceneId", required = false) String sceneId,
                                   @RequestParam(value = "loadId", required = false) String loadId) {
        JSONObject requestData = new JSONObject();
        requestData.put("taskId", taskId);
        requestData.put("sceneId", sceneId);
        requestData.put("loadId", loadId);
        if (requestData != null) {
            JSONObject startTool = LoadService.startTool(requestData);
            if (startTool != null) {
                return new ServiceResult(startTool, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
            } else {
                return new ServiceResult(null, BaseErrorCode.ERROR, "阶段载荷执行失败");
            }
        } else {
            return new ServiceResult(null, BaseErrorCode.PARAMS_INVALID, "参数缺失");
        }
    }

    /**
     * 阶段载荷参数设置
     */
    @ApiOperation(value = "阶段载荷参数设置", notes = "")
    @PutMapping("/updateLoadParam")
    public ServiceResult updateLoadParam(@RequestBody JSONObject requestData) {
        if (requestData != null) {
            Boolean updateLoadParam = LoadService.updateLoadParam(requestData);
            if (updateLoadParam) {
                return new ServiceResult(null, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
            }
        }
        return new ServiceResult(null, BaseErrorCode.ERROR, "阶段载荷参数设置失败");
    }

    /**
     * 阶段载荷移除
     */
    @ApiOperation(value = "阶段载荷移除", notes = "")
    @DeleteMapping("/deleteLoad")
    public ServiceResult deleteLoad(@RequestBody JSONObject requestData) {
        if (requestData != null) {
            Boolean deleteLoadBind = LoadService.deleteLoadBind(requestData);
            if (deleteLoadBind) {
                return new ServiceResult(null, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
            }
        }
        return new ServiceResult(null, BaseErrorCode.ERROR, "阶段载荷参数移除失败");
    }

    /*
     * 阶段测试绑定载荷页面
     * */
    @ApiOperation(value = "阶段测试绑定载荷页面", notes = "")
    @GetMapping("/stageBindLoadList")
    public ServiceResult stageBindLoadList(@RequestParam(value = "sceneId", required = false) String sceneId) {
        List stageBindLoadList = LoadService.stageBindLoadList(sceneId);
        return new ServiceResult(stageBindLoadList, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
    }

    /*
     * 查询该场景绑定的载荷及记录
     * */
    @ApiOperation(value = "查询该场景绑定的载荷及记录", notes = "")
    @GetMapping("/getLoadResultBySceneId")
    public ServiceResult getLoadResultBySceneId(@RequestParam(value = "sceneId") String sceneId) {
        List<JSONObject> getLoadResultBySceneId = LoadService.getLoadResultBySceneId(sceneId);
        return new ServiceResult(getLoadResultBySceneId, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
    }

}
