package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.target.BindOrgTargetsRequest;
import cn.cyberpeace.mp.auth.model.OrgPermissionModel;
import cn.cyberpeace.mp.auth.service.OrgPermissionService;
import cn.cyberpeace.mp.auth.util.ServletUtils;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/org_permissions")
public class OrgPermissionController extends BaseModelController<OrgPermissionModel, OrgPermissionService> {

    @Autowired
    public OrgPermissionController(OrgPermissionService modelService) {
        setModelService(modelService);
    }


    @ApiOperation(value = "绑定组织-权限关系，注意，如果targetIds为空，表示删除组织所有权限！", notes = "")
    @PostMapping("/bind")
    public ServiceResult bindOrgPermission(@RequestBody @Validated BindOrgTargetsRequest bindOrgTargetsReq) {
        boolean success = getModelService().bindOrgTargets(bindOrgTargetsReq.getUserId(), bindOrgTargetsReq.getTargetIds());
        return ServletUtils.commonResult(success, null);
    }

}
