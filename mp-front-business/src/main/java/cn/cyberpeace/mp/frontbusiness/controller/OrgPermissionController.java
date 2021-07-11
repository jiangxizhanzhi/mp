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


@Api(value = "组织-权限管理接口(业务)", tags = "AUTH-组织-权限管理接口(业务)")
@Slf4j
@RestController("orgPermissionFrontController")
@RequestMapping(ControllerConstant.BASE_API_PATH + "/org_permissions")
public class OrgPermissionController {

    @Autowired
    private OrgPermissionService orgPermissionService;


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


    @ApiOperation(value = "查询组织所有权限")
    @ApiImplicitParam(name = "orgId", value = "组织ID", dataTypeClass = String.class)
    @GetMapping("/byorg")
    public ServiceResult<Set<TargetModel>> getOrgPermissions(@RequestParam String orgId) {
        Set<TargetModel> targets = orgPermissionService.listTargetsByOrgId(orgId);
        return ResultCommon.success(targets);
    }


    @ApiOperation(value = "查询组织所有权限（继承上级组织权限）")
    @ApiImplicitParam(name = "orgId", value = "组织ID", dataTypeClass = String.class)
    @GetMapping("/byorg/includeparent")
    public ServiceResult<Set<TargetModel>> getOrgPermission(@RequestParam String orgId) {
        Set<TargetModel> targets = orgPermissionService.listTargetsByOrgIdIncludeAllParentsOrg(orgId);
        return ResultCommon.success(targets);
    }


}
