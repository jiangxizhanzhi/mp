package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.target.BindRoleTargetsRequest;
import cn.cyberpeace.mp.auth.model.RolePermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.service.RolePermissionService;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
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

@Api(value = "角色-权限管理接口", tags = "AUTH-角色-权限管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/role_permissions")
public class RolePermissionController extends BaseModelController<RolePermissionModel, QueryParams, RolePermissionService> {

    @Autowired
    public RolePermissionController(RolePermissionService modelService) {
        setModelService(modelService);
    }

    @ApiOperation(value = "根据roleId查询权限", notes = "")
    @GetMapping("/byrole")
    public ServiceResult getTargetsByRole(@RequestParam String roleId) {
        List<TargetModel> targets = getModelService().listTargetsByRoleId(roleId);

        return ResultUtils.commonResult(true, targets);
    }

    @ApiOperation(value = "绑定角色-权限关系，注意，如果targetIds为空，表示删除角色所有权限！", notes = "")
    @PostMapping("/bind")
    public ServiceResult bindOrgPermission(@RequestBody @Validated BindRoleTargetsRequest bindRoleTargetsReq) {
        boolean success = getModelService().bindRoleTargets(bindRoleTargetsReq.getRoleId(), bindRoleTargetsReq.getTargetIds(), true);
        return ResultUtils.commonResult(success, null);
    }




}
