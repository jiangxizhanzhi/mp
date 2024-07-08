package cn.cyberpeace.mp.cscene.controller.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoad;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoadSceneLog;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoadSource;
import cn.cyberpeace.mp.cscene.request.load.LoadRequest;
import cn.cyberpeace.mp.cscene.request.load.LoadResultSearchRequest;
import cn.cyberpeace.mp.cscene.request.load.OutLoadTaskUpdateRequest;
import cn.cyberpeace.mp.cscene.request.ojj.IdsRequest;
import cn.cyberpeace.mp.cscene.service.service.LoadServiceImpl;
import cn.cyberpeace.mp.sceneplugincollection.plugin.azure.azurefunction.FirewallFunction;
import cn.cyberpeace.mp.sceneplugincollection.plugin.azure.functionrequest.FirewallCreateRequestBody;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Api(value = "载荷页面", tags = "载荷页面管理")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_LOAD_API_PATH + "/load")
public class LoadController {

    @Autowired
    private LoadServiceImpl loadService;

    @Autowired
    private FirewallFunction firewallFunction;

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
        JSONObject result = loadService.getLoadList(requestData);
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
        JSONObject result = loadService.getLoadList(requestData);
        return new ListObjServiceResult((List) result.get("source"), (int) result.get("total")).asResult();
    }

    /*
     * 根据ids获取载荷列表
     * */
    @ApiOperation(value = "根据ids获取载荷列表", notes = "")
    @PostMapping("/getToolListByloadIds")
    public ServiceResult getToolListByloadIds(@RequestBody IdsRequest requestData) {
        if (requestData != null) {
            List<MpDevopsLoad> getToolListByloadPks = loadService.getToolListByloadIds(requestData);
            return new ServiceResult(getToolListByloadPks, BaseErrorCode.SUCCESS).asResult();
        }
        return new ServiceResult(null, BaseErrorCode.PARAMS_INVALID).asResult();
    }

    /*
     * 查询载荷
     * */
    @ApiOperation(value = "查询载荷", notes = "")
    @GetMapping("/getLoadByPk")
    public ServiceResult getLoadByPk(@RequestParam(value = "pk") String pk) {
        MpDevopsLoad result = loadService.getLoadByPk(pk);
        return new ServiceResult(result, BaseErrorCode.SUCCESS).asResult();
    }

    /*
     * 修改载荷
     * */
    @ApiOperation(value = "修改载荷", notes = "")
    @PatchMapping("/updateLoadByPk")
    public ServiceResult updateLoadByPk(@RequestBody(required = false) MpDevopsLoad requestData) {
        JSONObject jsonObject = JsonCommon.snakeParse(JsonCommon.stringify(requestData), JSONObject.class);
        Boolean updateLoadByPk = loadService.updateLoadByPk(jsonObject);
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
        ServiceResult addLoad = loadService.addLoad(jsonObject);
        return addLoad;
    }

    /*
     * 删除载荷
     * */
    @ApiOperation(value = "删除载荷", notes = "")
    @DeleteMapping("/deleteLoadByPk")
    public ServiceResult deleteLoadByPk(@RequestBody(required = false) MpDevopsLoad requestData) {
        JSONObject jsonObject = JsonCommon.snakeParse(JsonCommon.stringify(requestData), JSONObject.class);
        Boolean deleteLoadByPk = loadService.deleteLoadByPk(jsonObject);
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
    public ServiceResult synchronousData(@RequestParam(value = "is_need_message", defaultValue = "true") Boolean isNeedMessage) {
        JSONObject synchronousData = loadService.synchronousData(isNeedMessage);
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
        MpDevopsLoadSource result = loadService.getSynchronousDataSource(requestData);
        return new ServiceResult(result, BaseErrorCode.SUCCESS).asResult();
    }

    /*
     * 载荷数据同步设置保存
     * */
    @ApiOperation(value = "载荷数据同步设置保存", notes = "")
    @PostMapping("/synchronousDataSource")
    public ServiceResult synchronousDataSource(@RequestBody MpDevopsLoadSource requestData) {
        ServiceResult synchronousDataSource = loadService.synchronousDataSource(requestData);
        return synchronousDataSource;
    }

    /*
     * 载荷改造
     * */
    @ApiOperation(value = "载荷改造", notes = "")
    @PostMapping("/loadTransform")
    public ServiceResult loadTransform(@RequestParam(value = "file") String file, @RequestParam(value = "transform_type") String transformType) {
        ServiceResult loadTransform = loadService.loadTransform(file, transformType);
        return loadTransform;
    }

//**********************************************************************************************************//

    /*
     * 根据场景id获取绑定的载荷
     * */
    @ApiOperation(value = "根据场景id获取绑定的载荷", notes = "")
    @GetMapping("/getLoadBySceneId")
    public ServiceResult getLoadBySceneId(@RequestParam(value = "scene_id") String sceneId) {
        List<MpDevopsLoad> list = loadService.getLoadBySceneId(sceneId);
        return new ServiceResult(list, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
    }

    /*
     * 载荷执行结果统计
     * */
    @ApiOperation(value = "载荷执行结果统计", notes = "")
    @PostMapping("/getLoadExecutionResultStatistics")
    public ServiceResult getLoadExecutionResultStatistics(@RequestBody(required = false) LoadResultSearchRequest requestData) {
        ListObjServiceResult listObjServiceResult = loadService.checkGetResultParam(requestData);
        if (!listObjServiceResult.isSuccess()) {
            return listObjServiceResult;
        }
        Map<String, Integer> map = loadService.getLoadExecutionResultStatistics(requestData);
        return new ServiceResult(map, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
    }

    /*
     * 载荷执行结果
     * */
    @ApiOperation(value = "载荷执行结果", notes = "")
    @PostMapping("/getLoadExecutionResult")
    public ListObjServiceResult getLoadExecutionResult(@RequestBody(required = false) LoadResultSearchRequest requestData) {
        ListObjServiceResult listObjServiceResult = loadService.checkGetResultParam(requestData);
        if (!listObjServiceResult.isSuccess()) {
            return listObjServiceResult;
        }
        return loadService.getLoadExecutionResult(requestData);
    }

    /*
     * 3D态势-载荷攻击TOP5
     * */
    @ApiOperation(value = "3D态势-载荷攻击TOP5", notes = "")
    @GetMapping("/getTop5Loads")
    public ServiceResult getTop5Loads(@RequestParam(value = "scene_id") String sceneId) {
        LinkedList<JSONObject> getTop5Loads = loadService.getTop5Loads(sceneId);
        return new ServiceResult(getTop5Loads, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
    }

    /*
     * 载荷执行结果详情
     * */
    @ApiOperation(value = "载荷执行结果详情", notes = "")
    @GetMapping("/getLoadExecutionResultByPk")
    public ServiceResult getLoadExecutionResultByLogPk(@RequestParam(value = "pk") String pk) {
        MpDevopsLoadSceneLog loadExecutionResult = loadService.getLoadExecutionResultByLogPk(pk);
        if (loadExecutionResult != null) {
            return new ServiceResult(loadExecutionResult, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
        } else {
            return new ServiceResult(null, BaseErrorCode.DATA_NOT_EXIST, "数据不存在").asResult();
        }
    }

    /*
     * 删除载荷执行结果
     * */
    @ApiOperation(value = "删除载荷执行结果", notes = "")
    @DeleteMapping("/deleteLoadExecutionResultByPk")
    public ServiceResult deleteLoadExecutionResultByPk(@RequestBody MpDevopsLoadSceneLog request) {
        ServiceResult deleteLoadExecutionResultByPk = loadService.deleteLoadExecutionResultByPk(request);
        return deleteLoadExecutionResultByPk;
    }

    /*
     * 修改载荷执行结果
     * */
    @ApiOperation(value = "修改载荷执行结果", notes = "")
    @PutMapping("/updateLoadExecutionResultByPk")
    public ServiceResult updateLoadExecutionResultByPk(@RequestBody MpDevopsLoadSceneLog request) {
        ServiceResult updateLoadExecutionResultByPk = loadService.updateLoadExecutionResultByPk(request);
        return updateLoadExecutionResultByPk;
    }

    /*
     * 新增载荷执行结果
     * */
    @ApiOperation(value = "新增载荷执行结果", notes = "")
    @PostMapping("/addLoadExecutionResult")
    public ServiceResult addLoadExecutionResult(@RequestBody MpDevopsLoadSceneLog request) {
        Boolean addLoadExecutionResult = loadService.addLoadExecutionResult(request);
        if (addLoadExecutionResult) {
            return new ServiceResult(null, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
        }
        return new ServiceResult(null, BaseErrorCode.ERROR, "新增载荷执行结果失败").asResult();
    }

    /*
     * 根据场景获取阶段列表
     * */
    @ApiOperation(value = "查询载荷", notes = "")
    @GetMapping("/getStagesBySceneId")
    public ServiceResult getStagesBySceneId(@RequestParam(value = "scene_id") String sceneId) {
        List StageList = loadService.getStagesBySceneId(sceneId);
        return new ServiceResult(StageList, BaseErrorCode.SUCCESS).asResult();
    }

    /*
     * 批量新增载荷执行结果
     * */
    @ApiOperation(value = "批量新增载荷执行结果", notes = "")
    @PostMapping("/addLoadExecutionOutResult")
    public ServiceResult addLoadExecutionOutResult(@RequestBody(required = false) List<LoadRequest> requestData) {
        ServiceResult serviceResult = loadService.updateLoadExecutionOutResult(requestData);
        return serviceResult;
    }

    /*
     * 载荷执行结果导入
     * */
    @ApiOperation(value = "载荷执行结果导入", notes = "")
    @PostMapping("/importLoadExecutionResult")
    public ServiceResult importLoadExecutionResult(@RequestParam(value = "file") MultipartFile file) throws IOException {
        ServiceResult importLoadExecutionResult = loadService.importLoadExecutionResult(file);
        return importLoadExecutionResult;
    }

    /*
     * 载荷执行结果导出
     * */
    @ApiOperation(value = "载荷执行结果导出", notes = "")
    @PostMapping("/exportLoadExecutionResult")
    public ServiceResult exportLoadExecutionResult(HttpServletRequest request, HttpServletResponse response,
                                                   @RequestBody(required = false) LoadResultSearchRequest requestData) throws IOException {
        Boolean exportLoadExecutionResult = loadService.exportLoadExecutionResult(request, response, requestData);
        if (exportLoadExecutionResult) {
            return new ServiceResult(null, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
        } else {
            return new ServiceResult(null, BaseErrorCode.ERROR, "载荷执行结果导出失败").asResult();
        }
    }

    /**
     * 阶段载荷接入(根据taskId获取访问链接)
     */
    @ApiOperation(value = "阶段载荷接入", notes = "")
    @GetMapping("/getTaskNet")
    public ServiceResult getTaskNet(@RequestParam(value = "scene_id") String sceneId,
                                    @RequestParam(value = "stage_id") String stageId,
                                    @RequestParam(value = "id") String id,
                                    @RequestParam(value = "task_id") String taskId,
                                    @RequestParam(value = "instance_id") String instanceId) {
        JSONObject requestData = new JSONObject();
        requestData.put("sceneId", sceneId);
        requestData.put("stageId", stageId);
        requestData.put("id", id);
        requestData.put("taskId", taskId);
        requestData.put("instanceId", instanceId);
        if (requestData != null) {
            Map<String, Object> getTaskNet = loadService.getTaskNet(requestData);
            if (getTaskNet != null) {
                return new ServiceResult(getTaskNet, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
            }
        }
        return new ServiceResult(null, BaseErrorCode.ERROR, "阶段载荷接入失败");
    }

    /**
     * 阶段载荷参数设置
     */
    @ApiOperation(value = "阶段载荷参数设置", notes = "")
    @PutMapping("/updateLoadParam")
    public ServiceResult updateLoadParam(@RequestBody OutLoadTaskUpdateRequest requestData) {
        if (requestData != null) {
            Boolean updateLoadParam = loadService.updateLoadParam(requestData);
            if (updateLoadParam) {
                return new ServiceResult(null, BaseErrorCode.SUCCESS, "阶段载荷参数设置成功").asResult();
            }
        }
        return new ServiceResult(null, BaseErrorCode.ERROR, "阶段载荷参数设置失败");
    }

    /*
     * 查询该场景绑定的载荷及记录
     * */
    @ApiOperation(value = "查询该场景绑定的载荷及记录", notes = "")
    @GetMapping("/getLoadResultBySceneId")
    public ServiceResult getLoadResultBySceneId(@RequestParam(value = "scene_id") String sceneId) {
        List<JSONObject> getLoadResultBySceneId = loadService.getLoadResultBySceneId(sceneId);
        return new ServiceResult(getLoadResultBySceneId, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
    }

    /*
     * 查询该节点id的载荷执行结果
     * */
    @ApiOperation(value = "查询该节点id的载荷执行结果", notes = "")
    @GetMapping("/getLoadResultByNodeIds")
    public ServiceResult getLoadResultByNodeIds(@RequestParam(value = "node_ids") String[] nodeIds) {
        List<MpDevopsLoadSceneLog> log = loadService.getLoadResultByNodeIds(nodeIds);
        return new ServiceResult(log, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
    }


    /**
     * 创建防火墙
     */
    @ApiOperation(value = "创建防火墙", notes = "")
    @PostMapping("/createFireWall")
    public ServiceResult createFireWall(@RequestBody FirewallCreateRequestBody fireWallCreateRequestBody) {
        firewallFunction.createFireWall(1, fireWallCreateRequestBody);
        return new ServiceResult(log, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
    }


}
