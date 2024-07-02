package cn.cyberpeace.mp.frontbusiness.controller;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.request.target.BindRoleTargetsRequest;
import cn.cyberpeace.mp.auth.service.RolePermissionModelService;
import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
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
 * @ClassName RolePermissionController
 * @Description 角色权限管理接口
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 */
@Api(value = "角色管理接口", tags = "角色管理接口")
@Slf4j
@RestController("rolePermissionFrontController")
@RequestMapping(ControllerConstant.BASE_API_PATH + "/role_permissions")
@AuthTarget(TargetEnum.NO_AUTH)
public class RolePermissionController {

    @Autowired
    private RolePermissionModelService rolePermissionModelService;

    /**
     * 角色ID
     *
     * @param roleId
     * @return
     */
    @ApiOperation(value = "[角色权限管理] 查询角色权限", notes = "")
    @ApiImplicitParam(name="roleId", value="角色ID", dataTypeClass = String.class)
    @GetMapping("/byrole")
    public ServiceResult<List<TargetModel>> getTargetsByRole(@RequestParam String roleId) {
        List<TargetModel> targets = rolePermissionModelService.listTargetsByRoleIds(Arrays.asList(roleId));

        return ResultCommon.success(targets);
    }

    /**
     * 角色权限绑定请求
     *
     * @param bindRoleTargetsReq
     * @return 统一服务返回结果
     */
    @ApiOperation(value = "[角色权限管理] 绑定角色-权限关系，注意，如果targetIds为空，表示删除角色所有权限！", notes = "")
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
