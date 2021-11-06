package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.target.BindOrgTargetsRequest;
import cn.cyberpeace.mp.auth.model.OrgPermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.service.OrgPermissionService;
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

import java.util.Set;

/**
 * @Description: 组织-权限管理接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "组织-权限管理接口", tags = "AUTH-组织-权限管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/org_permissions")
public class OrgPermissionController extends BaseModelController<OrgPermissionModel, QueryParams, OrgPermissionService> {

    @Autowired
    public OrgPermissionController(OrgPermissionService modelService) {
        setModelService(modelService);
    }


    @ApiOperation(value = "绑定组织-权限关系，注意，该操作会清除原来绑定的所有权限， 如果targetIds为空，表示删除组织所有权限！", notes = "")
    @PostMapping("/bind")
    public ServiceResult bindOrgPermissions(@RequestBody @Validated BindOrgTargetsRequest bindOrgTargetsReq) {
        boolean success = getModelService().bindOrgTargets(bindOrgTargetsReq.getUserId(), bindOrgTargetsReq.getTargetIds(), true);
        if (success) {
            return ServiceResult.successful();
        } else {
            return ServiceResult.fail("绑定组织权限失败!");
        }
    }


    @ApiOperation(value = "查询组织所有权限")
    @GetMapping("/byorg")
    public ServiceResult<Set<TargetModel>> getOrgPermissions(@RequestParam String orgId) {
        Set<TargetModel> targets = getModelService().listTargetsByOrgId(orgId);
        return ServiceResult.success(targets);
    }


    @ApiOperation(value = "查询组织所有权限（继承上级组织权限）")
    @GetMapping("/byorg/includeparent")
    public ServiceResult<Set<TargetModel>> getOrgPermission(@RequestParam String orgId) {
        Set<TargetModel> targets = getModelService().listTargetsByOrgIdIncludeAllParentsOrg(orgId);
        return ServiceResult.success(targets);
    }


}
