package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.target.BindRoleTargetsRequest;
import cn.cyberpeace.mp.auth.model.RolePermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.service.RolePermissionModelService;
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

/**
 * @Description: 角色权限管理接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "角色-权限管理接口", tags = "AUTH-角色-权限管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/role_permissions")
public class RolePermissionController extends BaseModelController<RolePermissionModel, QueryParams, RolePermissionModelService> {

    @Autowired
    public RolePermissionController(RolePermissionModelService modelService) {
        setModelService(modelService);
    }

    @ApiOperation(value = "查询角色权限", notes = "")
    @GetMapping("/byrole")
    public ServiceResult<List<TargetModel>> getTargetsByRole(@RequestParam String roleId) {
        List<TargetModel> targets = getModelService().listTargetsByRoleId(roleId);

        return ServiceResult.success(targets);
    }

    @ApiOperation(value = "绑定角色-权限关系，注意，如果targetIds为空，表示删除角色所有权限！", notes = "")
    @PostMapping("/bind")
    public ServiceResult bindRolePermission(@RequestBody @Validated BindRoleTargetsRequest bindRoleTargetsReq) {
        boolean success = getModelService().bindRoleTargets(bindRoleTargetsReq.getRoleId(), bindRoleTargetsReq.getTargetIds(), true);
        if (success) {
            return ServiceResult.successful();
        } else {
            return ServiceResult.fail("绑定角色权限失败!");
        }
    }




}
