package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoad;
import cn.cyberpeace.mp.cscene.service.service.LoadServiceImpl;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.MicrosoftAzureConverter;
import cn.cyberpeace.mp.devopscluster.request.TargetSceneRequest;
import cn.cyberpeace.mp.devopscluster.vo.DeviceVO;
import cn.cyberpeace.mp.devopscluster.vo.TargerRequestVO;
import cn.cyberpeace.mp.devopscluster.vo.common.ListResult;
import cn.cyberpeace.mp.devopscluster.vo.common.Result;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 靶标管理类
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/outTarget")
@Api(tags = "云靶标管理")
@RequiredArgsConstructor
public class CloudDevopsTargetController {

    @Autowired
    private MicrosoftAzureConverter microsoftAzureConverter;

    @Autowired
    private LoadServiceImpl LoadService;

    /**
     * 查询所有镜像管理分类
     */
    @ApiOperation("查询所有镜像管理分类")
    @GetMapping("/imageCategories")
    public ServiceResult imageCategories(@RequestParam(value = "code", defaultValue = "100201") String code) {
        List list = microsoftAzureConverter.getImageCategories(code);
        return new ServiceResult(list, BaseErrorCode.SUCCESS).asResult();
    }

    /**
     * 查询所有云靶标信息
     */
    @ApiOperation("微软云服务节点列表获取")
    @PostMapping("/getTarget")
    public ListObjServiceResult getTarget(@RequestBody TargerRequestVO request) {
        JSONObject result = microsoftAzureConverter.getCloudTargetListByCodes(request);
        return new ListObjServiceResult((List) result.get("list"), (int) result.get("total")).asResult();
    }

    /**
     * 查询所有云靶标信息
     */
    @ApiOperation("拓扑编辑获取载荷列表")
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

    /**
     * 查询所有靶标信息
     *
     * @param request
     * @return
     */
    @ApiOperation("查询常用靶标信息")
    @GetMapping("/getCommonTargets")
    public ServiceResult getCommonTargets(TargetSceneRequest request) {
        List<DeviceVO> returnData = microsoftAzureConverter.getCommonTargets(request);
        return new Result(new ListResult(returnData)).asResult();
    }

    /*
     * 查询节点详情
     * */
    @ApiOperation(value = "查询节点详情", notes = "")
    @GetMapping("/getDetailsByTarget")
    public ServiceResult getDetailsByTarget(@RequestParam(value = "pk") String pk,
                                            @RequestParam(value = "catagory") Integer catagory,
                                            @RequestParam(value = "cloud_service_type") String cloudServiceType) {
        return microsoftAzureConverter.getDetailsByTarget(pk,catagory,cloudServiceType);
    }

}
