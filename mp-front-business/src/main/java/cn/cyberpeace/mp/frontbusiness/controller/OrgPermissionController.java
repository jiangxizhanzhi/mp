 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OrgPermissionController.java.
 // File size 3063.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.controller;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.request.target.BindOrgTargetsRequest;
import cn.cyberpeace.mp.auth.service.OrgPermissionService;
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

import java.util.Set;

 /**
  * 组织权限控制器
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Api(value = "组织-权限管理接口(业务)", tags = "AUTH-组织-权限管理接口(业务)")
@Slf4j
@RestController("orgPermissionFrontController")
@RequestMapping(ControllerConstant.BASE_API_PATH + "/org_permissions")
public class OrgPermissionController {

     /**
      * org许可服务
      */
     @Autowired
    private OrgPermissionService orgPermissionService;

     /**
      * @param bindOrgTargetsReq
      * @return {@link ServiceResult }
      */
     @ApiOperation(value = "绑定组织-权限关系，注意，该操作会清除原来绑定的所有权限， 如果targetIds为空，表示删除组织所有权限！", notes = "")
    @ApiImplicitParam(name = "bindOrgTargetsReq", value = "绑定组织权限请求体", dataTypeClass = BindOrgTargetsRequest.class)
    @PostMapping("/bind")
    public ServiceResult bindOrgPermissions(@RequestBody @Validated BindOrgTargetsRequest bindOrgTargetsReq) {
        boolean success = orgPermissionService.bindOrgTargets(bindOrgTargetsReq.getUserId(), bindOrgTargetsReq.getTargetIds(), true);
        if (success) {
            return ResultCommon.success();
        } else {
            log.error("bind org permission failed!");
            throw new MpRuntimeException(AuthErrorCode.ORG_TARGET_ADD_ERROR);
        }

    }

     /**
      * @param orgId org id
      * @return {@link ServiceResult }<{@link Set }<{@link TargetModel }>>
      */
     @ApiOperation(value = "查询组织所有权限")
    @ApiImplicitParam(name = "orgId", value = "组织ID", dataTypeClass = String.class)
    @GetMapping("/byorg")
    public ServiceResult<Set<TargetModel>> getOrgPermissions(@RequestParam String orgId) {
        Set<TargetModel> targets = orgPermissionService.listTargetsByOrgId(orgId);
        return ResultCommon.success(targets);
    }

     /**
      * 获得org权限
      *
      * @param orgId org id
      * @return {@link ServiceResult }<{@link Set }<{@link TargetModel }>>
      */
     @ApiOperation(value = "查询组织所有权限（继承上级组织权限）")
    @ApiImplicitParam(name = "orgId", value = "组织ID", dataTypeClass = String.class)
    @GetMapping("/byorg/includeparent")
    public ServiceResult<Set<TargetModel>> getOrgPermission(@RequestParam String orgId) {
        Set<TargetModel> targets = orgPermissionService.listTargetsByOrgIdIncludeAllParentsOrg(orgId);
        return ResultCommon.success(targets);
    }


}
