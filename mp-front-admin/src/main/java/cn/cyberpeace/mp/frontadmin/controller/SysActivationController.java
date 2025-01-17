 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysActivationController.java.
 // File size 4278.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.controller;

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
  * 系统激活控制器
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Api(value = "系统激活管理接口", tags = "AUTH-系统激活管理接口")
@Slf4j
@RestController
@RequestMapping("/sys/activation")
public class SysActivationController {

     /**
      * 激活服务
      */
     @Autowired
    private SysActivationService activationService;

     /**
      * 激活
      *
      * @param activationRequest 激活请求
      * @return {@link ServiceResult }
      */
     @ApiOperation("激活")
    @ApiImplicitParam(name = "activationRequest", value = "激活请求体", dataTypeClass = ActivationRequest.class)
    @PostMapping
    public ServiceResult activate(@RequestBody @Validated ActivationRequest activationRequest) {
        activationService.activate(activationRequest);
        return ResultCommon.success();
    }

     /**
      * 状态
      *
      * @return {@link ServiceResult }<{@link ActivationResponse }>
      */
     @ApiOperation("查询激活选项内容")
    @GetMapping("/status")
    public ServiceResult<ActivationResponse> status() {
        log.info("query activation status...");
        ActivationResponse content = getActivationResponse();

        return ResultCommon.success(content);
    }

     /**
      * 模块信息
      *
      * @return {@link ServiceResult }<{@link List }<{@link ModuleInfoResponse }>>
      */
     @ApiOperation("查询模块激活信息，包括竞赛模块，实训模块，靶场模块")
    @GetMapping("/module_infos")
    public ServiceResult<List<ModuleInfoResponse>> moduleInfos() {
        var moduleInfoList = activationService.getModuleInfoList();
        return ResultCommon.success(moduleInfoList);
    }

     /**
      * @return {@link ServiceResult }<{@link ActivationStatus }>
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
      * 得到配置价值
      *
      * @param key 关键
      * @return {@link ServiceResult }<{@link String }>
      */
     @ApiOperation("查询激活配置项")
    @ApiImplicitParam(name = "key", value = "配置名称", dataTypeClass = String.class)
    @GetMapping("/config_value")
    public ServiceResult<String> getConfigValue(@RequestParam String key) {
        String value = activationService.getValueByKey(key);

        return ResultCommon.success(value);
    }

     /**
      * 获取机器码
      *
      * @return {@link ServiceResult }
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
      * @return {@link ServiceResult }
      */
     @ApiOperation("重置菜单可见性")
    @GetMapping("/reset_menu_visibility")
    public ServiceResult resetMenuVisibility() {
        activationService.resetMenuVisibility();
        return ResultCommon.success();
    }

     /**
      * 获得激活响应
      *
      * @return {@link ActivationResponse }
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
