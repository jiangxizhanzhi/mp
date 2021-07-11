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


@Api(value = "角色-权限管理接口(业务)", tags = "AUTH-角色-权限管理接口(业务)")
@Slf4j
@RestController("rolePermissionFrontController")
@RequestMapping(ControllerConstant.BASE_API_PATH + "/role_permissions")
public class RolePermissionController {

    @Autowired
    private RolePermissionModelService rolePermissionModelService;

    @ApiOperation(value = "查询角色权限", notes = "")
    @ApiImplicitParam(name="roleId", value="角色ID", dataTypeClass = String.class)
    @GetMapping("/byrole")
    public ServiceResult<List<TargetModel>> getTargetsByRole(@RequestParam String roleId) {
        List<TargetModel> targets = rolePermissionModelService.listTargetsByRoleIds(Arrays.asList(roleId));

        return ResultCommon.success(targets);
    }

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
