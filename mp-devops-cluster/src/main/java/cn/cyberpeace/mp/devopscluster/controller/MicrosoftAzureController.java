package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.MicrosoftAzureConverter;
import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetVO;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 镜像管理
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/cloudTarget")
@Api(tags = "靶标管理-云镜像管理")
@RequiredArgsConstructor
public class MicrosoftAzureController {

    @Autowired
    private MicrosoftAzureConverter microsoftAzureConverter;

    /**
     * 云镜像管理页面分页查询
     */
    @ApiOperation(value = "云镜像管理页面分页查询", notes = "")
    @GetMapping("/list")
    public ListObjServiceResult getCloudTargetList(@RequestParam(value = "per_page", defaultValue = "10") Integer pageSize,
                                                   @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
                                                   @RequestParam(value = "name", required = false) String name,
                                                   @RequestParam(value = "operating_system", required = false) String operatingSystem) {
        JSONObject requestData = new JSONObject();
        requestData.put("name", name);
        requestData.put("operatingSystem", operatingSystem);
        requestData.put("pageNum", pageNum);
        requestData.put("pageSize", pageSize);
        JSONObject result = microsoftAzureConverter.getCloudTargetList(requestData);
//        List returnData = microsoftAzureConverter.getTarget(jsonObject);
        return new ListObjServiceResult((List) result.get("source"), (int) result.get("total")).asResult();
    }

    /**
     * 云镜像删除
     */
    @ApiOperation(value = "云镜像删除", notes = "")
    @DeleteMapping("/delete")
    public ServiceResult deleteCloudTarget(@RequestBody(required = false) JSONObject requestData) {
        Boolean deleteCloudTarget = microsoftAzureConverter.deleteCloudTarget(requestData);
        if (deleteCloudTarget) {
            return new ServiceResult<>(null, BaseErrorCode.SUCCESS).asResult();
        } else {
            return new ServiceResult<>(null, BaseErrorCode.ERROR, "数据删除失败,请检查列表是否存在该数据").asResult();
        }
    }

    @ApiOperation(value = "云镜像管理页面id查询", notes = "")
    @GetMapping("/getById")
    public ServiceResult<DeviceCloudTargetVO> getCloudTargetByPk(@RequestParam(value = "cloud_service_type") String cloudServiceType,
                                                                 @RequestParam(value = "id") String id) {
        JSONObject requestData = new JSONObject();
        requestData.put("id", id);
        requestData.put("cloudServiceType", cloudServiceType);
        return new ServiceResult<>(microsoftAzureConverter.getCloudTargetById(requestData)).asResult();
    }

    /**
     * 云镜像编辑
     */
    @ApiOperation(value = "云镜像编辑", notes = "")
    @PutMapping("/update")
    public ServiceResult updateCloudTarget(@RequestBody(required = false) DeviceCloudTargetVO requestData) {
        JSONObject jsonObject = JsonCommon.snakeParse(JsonCommon.stringify(requestData), JSONObject.class);
        Boolean updateCloudTarget = microsoftAzureConverter.updateCloudTarget(jsonObject);
        if (updateCloudTarget) {
            return new ServiceResult<>(null, BaseErrorCode.SUCCESS).asResult();
        } else {
            return new ServiceResult<>(null, BaseErrorCode.ERROR, "数据删除失败,请检查列表是否存在该数据").asResult();
        }
    }

    /**
     * 云镜像同步
     */
    @ApiOperation(value = "云镜像管理同步", notes = "根据云类型同步云镜像数据,cloud_type默认为1代表公有云")
    @GetMapping("/synchronousCloudData")
//    @CacheEvict(value = "getTarget", beforeInvocation = false)
    public ServiceResult synchronousCloudData() {
        microsoftAzureConverter.synchronousCloudData(1);
        microsoftAzureConverter.synchronousCloudData(2);
        microsoftAzureConverter.synchronousCloudData(3);
        return new ServiceResult(null, BaseErrorCode.SUCCESS).asResult();
    }
}
