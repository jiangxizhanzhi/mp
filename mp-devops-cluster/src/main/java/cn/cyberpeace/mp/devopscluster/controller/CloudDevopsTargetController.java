package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.model.MpDevopsBoundaryNetworkEquipment;
import cn.cyberpeace.mp.cscene.service.service.LoadServiceImpl;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.MicrosoftAzureConverter;
import cn.cyberpeace.mp.devopscluster.request.TargetSceneRequest;
import cn.cyberpeace.mp.devopscluster.vo.DeviceVO;
import cn.cyberpeace.mp.devopscluster.vo.EquipmentRequestVO;
import cn.cyberpeace.mp.devopscluster.vo.TargerRequestVO;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 云靶标管理类
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
     * 查询所有镜像管理分类-拓扑编辑器
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
     * 查询边界网络设备
     */
    @ApiOperation("查询边界网络设备")
    @GetMapping("/getCommonTargets")
    public ListObjServiceResult getCommonTargets(EquipmentRequestVO request) {
        ListObjServiceResult returnResult = microsoftAzureConverter.getCommonTargets(request);
        return returnResult;
    }
}
