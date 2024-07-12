 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysActivationController.java.
 // File size 5804.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.frontadmin.constant.AdminConstant;
import cn.cyberpeace.mp.frontadmin.dto.activation.ActivationStatus;
import cn.cyberpeace.mp.frontadmin.request.activation.ActivationRequest;
import cn.cyberpeace.mp.frontadmin.response.ModuleInfoResponse;
import cn.cyberpeace.mp.frontadmin.response.activation.ActivationResponse;
import cn.cyberpeace.mp.frontadmin.service.sys.SysActivationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * @ClassName SysActivationController
 * @Description 系统激活管理接口
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 */
@Api(value = "系统激活管理接口", tags = "系统激活管理接口")
@Slf4j
@RestController
@RequestMapping("/sys/activation")
@AuthTarget(TargetEnum.NO_AUTH)
public class SysActivationController {

    @Autowired
    private SysActivationService activationService;


    /**
     * 激活
     *
     * @param activationRequest
     * @return 统一服务返回结果
     */
    @ApiOperation("激活")
    @ApiImplicitParam(name = "activationRequest", value = "激活请求体", dataTypeClass = ActivationRequest.class)
    @PostMapping
    public ServiceResult activate(@RequestBody @Validated ActivationRequest activationRequest) {
        activationService.activate(activationRequest);
        return ResultCommon.success();
    }

    /**
     * 延期激活
     *
     * @param activationRequest
     * @return
     */
    @ApiOperation("延期激活")
    @ApiImplicitParam(name = "activationRequest", value = "延期激活请求体", dataTypeClass = ActivationRequest.class)
    @PostMapping("/delay")
    public ServiceResult<Boolean> delayActivate(@RequestBody @Validated ActivationRequest activationRequest) {
        activationService.delayActivate(activationRequest);
        return ResultCommon.success(true);
    }

    /**
     * 查询激活选项内容
     *
     * @return
     */
    @ApiOperation("查询激活选项内容")
    @GetMapping("/status")
    public ServiceResult<ActivationResponse> status() {
        log.debug("query activation status...");
        ActivationResponse content = getActivationResponse();
        if (content != null && AdminConstant.STATUS_NOT_ACTIVITED == content.getIsActived()) {
            content = new ActivationResponse();
        }
        return ResultCommon.success(content);
    }

    /**
     * 查询模块激活信息
     *
     * @return
     */
    @ApiOperation("查询模块激活信息，包括竞赛模块，实训模块，靶场模块")
    @GetMapping("/module_infos")
    public ServiceResult<List<ModuleInfoResponse>> moduleInfos() {
        List<ModuleInfoResponse> moduleInfoList = activationService.getModuleInfoList();
        return ResultCommon.success(moduleInfoList);
    }


    /**
     * 查询激活状态
     *
     * @return
     */
    @ApiOperation("查询激活状态")
    @GetMapping("/license_status")
    public ServiceResult<ActivationStatus> licenseStatus() {
        ActivationResponse content = getActivationResponse();
        ActivationStatus status = new ActivationStatus();
        status.setIsActived(content.getIsActived());
        return ResultCommon.success(status);
    }


    /**
     * 查询激活配置项
     *
     * @param key
     * @return
     */
    @ApiOperation("查询激活配置项")
    @ApiImplicitParam(name = "key", value = "配置名称", dataTypeClass = String.class)
    @GetMapping("/config_value")
    public ServiceResult<String> getConfigValue(@RequestParam String key) {
        String value = activationService.getValueByKey(key);

        return ResultCommon.success(value);
    }


    /**
     * 查询机器序列号
     *
     * @return 统一服务返回结果
     */
    @ApiOperation("查询机器序列号")
    @GetMapping("/machinecode")
    public ServiceResult getMachineCode() {
        String code = activationService.querySysUUID();
        return ResultCommon.success(code);
    }


    /**
     * 重置菜单可见性
     *
     * @return 统一服务返回结果
     */
    @ApiOperation("重置菜单可见性")
    @GetMapping("/reset_menu_visibility")
    public ServiceResult resetMenuVisibility() {
        activationService.resetMenuVisibility();
        return ResultCommon.success();
    }


    /**
     * @return
     */
    private ActivationResponse getActivationResponse() {
        ActivationResponse content = activationService.status();
        if (Objects.isNull(content)) {
            log.error("system activation status not found!");
            content = new ActivationResponse();
            content.setIsActived(AdminConstant.STATUS_NOT_ACTIVITED);
        }
        return content;
    }

}
