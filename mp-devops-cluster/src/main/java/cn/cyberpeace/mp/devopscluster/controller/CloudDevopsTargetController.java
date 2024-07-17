package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.bo.MpSysCodeEnum;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoad;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.MicrosoftAzureConverter;
import cn.cyberpeace.mp.devopscluster.vo.EquipmentRequestVO;
import cn.cyberpeace.mp.devopscluster.vo.LoadRequestVO;
import cn.cyberpeace.mp.devopscluster.vo.TargerRequestVO;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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

    //公有云code
    private static final String CLOUD_TYPE_CODE = "100401";

    /**
     * 查询所有镜像管理分类-拓扑编辑器
     */
    @ApiOperation("查询所有镜像管理分类")
    @GetMapping("/imageCategories")
    @Cacheable(key = "'getImageCategories' + #code", value = "getImageCategories")
    public ServiceResult imageCategories(@RequestParam(value = "code", defaultValue = CLOUD_TYPE_CODE) String code) {
        List list = microsoftAzureConverter.getImageCategories(code);
        return new ServiceResult(list, BaseErrorCode.SUCCESS).asResult();
    }

    /**
     * 查询所有云靶标信息
     */
    @ApiOperation("微软云服务节点列表获取")
    @PostMapping("/getTarget")
//    @Cacheable(key = "'getTarget' + #request", value = {"getTarget"})
    public ServiceResult getTarget(@RequestBody TargerRequestVO request) {
        List<JSONObject> result = microsoftAzureConverter.getCloudTargetListByCodes(request);
        return new ServiceResult(result).asResult();
    }

    /**
     * 拓扑编辑获取载荷列表条件
     */
    @ApiOperation("拓扑编辑获取载荷列表条件")
    @GetMapping("/getLoadListConditions")
    public ServiceResult getLoadListConditions() {
        List<MpSysCodeEnum> conditions = microsoftAzureConverter.getLoadListConditions();
        return new ServiceResult(conditions).asResult();
    }

    /**
     * 查询所有云靶标信息
     */
    @ApiOperation("拓扑编辑获取载荷列表")
    @PostMapping("/getLoadList")
    public ServiceResult getLoadList(@RequestBody LoadRequestVO request) {
        List<MpDevopsLoad> result = microsoftAzureConverter.getTopoLoadList(request);
        return new ServiceResult(result).asResult();
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
