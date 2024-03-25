package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.auth.util.BaseResponse;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.MicrosoftAzureConverter;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public BaseResponse getCloudTargetList(@RequestParam(value = "per_page", defaultValue = "10") Integer pageSize,
                                           @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
                                           @RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "systemType", required = false) String systemType) {
        JSONObject requestData = new JSONObject();
        requestData.put("name", name);
        requestData.put("systemType", systemType);
        requestData.put("pageNum", pageNum);
        requestData.put("pageSize", pageSize);
        return microsoftAzureConverter.getCloudTargetList(requestData);
    }

    /**
     * 操作系统下拉框
     */
    @ApiOperation(value = "操作系统下拉框", notes = "")
    @GetMapping("/getOS")
    public BaseResponse getOS() {
        return microsoftAzureConverter.getOS();
    }

    /**
     * 云镜像删除
     */
    @ApiOperation(value = "云镜像删除", notes = "")
    @DeleteMapping("/delete")
    public BaseResponse deleteCloudTarget(@RequestBody(required = false) JSONObject requestData) {
        return microsoftAzureConverter.deleteCloudTarget(requestData);
    }

    @ApiOperation(value = "云镜像管理页面pk查询", notes = "")
    @GetMapping("/getByPk")
    public BaseResponse getCloudTargetByPk(@RequestParam(value = "pk", required = false) String pk) {
        JSONObject requestData = new JSONObject();
        requestData.put("pk", pk);
        return microsoftAzureConverter.getCloudTargetByPk(requestData);
    }

    /**
     * 云镜像编辑
     */
    @ApiOperation(value = "云镜像编辑", notes = "")
    @PostMapping("/update")
    public BaseResponse updateCloudTarget(@RequestBody(required = false) JSONObject requestData) {
        return microsoftAzureConverter.updateCloudTarget(requestData);
    }

    /**
     * 云镜像管理同步
     */
    @ApiOperation(value = "云镜像管理同步", notes = "")
    @GetMapping("/synchronousCloudData")
    public BaseResponse synchronousCloudData(@RequestBody(required = false) JSONObject requestData) throws Exception {
        return microsoftAzureConverter.synchronousCloudData(requestData);
    }
}
