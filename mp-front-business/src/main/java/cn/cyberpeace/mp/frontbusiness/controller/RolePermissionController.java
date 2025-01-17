 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RolePermissionController.java.
 // File size 2636.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.controller;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.request.target.BindRoleTargetsRequest;
import cn.cyberpeace.mp.auth.service.RolePermissionModelService;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

 /**
  * 角色权限控制器
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Api(value = "角色-权限管理接口(业务)", tags = "AUTH-角色-权限管理接口(业务)")
@Slf4j
@RestController("rolePermissionFrontController")
@RequestMapping(ControllerConstant.BASE_API_PATH + "/role_permissions")
public class RolePermissionController {

     /**
      * 角色许可模型服务
      */
     @Autowired
    private RolePermissionModelService rolePermissionModelService;

     /**
      * 按角色获取目标
      *
      * @param roleId 角色id
      * @return {@link ServiceResult }<{@link List }<{@link TargetModel }>>
      */
     @ApiOperation(value = "查询角色权限", notes = "")
    @ApiImplicitParam(name="roleId", value="角色ID", dataTypeClass = String.class)
    @GetMapping("/byrole")
    public ServiceResult<List<TargetModel>> getTargetsByRole(@RequestParam String roleId) {
        List<TargetModel> targets = rolePermissionModelService.listTargetsByRoleIds(Arrays.asList(roleId));

        return ResultCommon.success(targets);
    }

     /**
      * 绑定角色权限
      *
      * @param bindRoleTargetsReq 需要绑定角色目标
      * @return {@link ServiceResult }
      */
     @ApiOperation(value = "绑定角色-权限关系，注意，如果targetIds为空，表示删除角色所有权限！", notes = "")
    @ApiImplicitParam(name = "bindRoleTargetsReq", value = "角色权限绑定请求", dataTypeClass = BindRoleTargetsRequest.class)
    @PostMapping("/bind")
    public ServiceResult bindRolePermission(@RequestBody @Validated BindRoleTargetsRequest bindRoleTargetsReq) {
        boolean success = rolePermissionModelService.bindRoleTargets(bindRoleTargetsReq.getRoleId(), bindRoleTargetsReq.getTargetIds(), true);
        if (success) {
            return ResultCommon.success();
        } else {
            log.error("bind role permission failed!");
            throw new MpRuntimeException(AuthErrorCode.ROLE_TARGET_ADD_ERROR);
        }
    }




}
