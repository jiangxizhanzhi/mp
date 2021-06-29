package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.dto.TargetDto;
import cn.cyberpeace.mp.auth.dto.org.BindOrgUsersRequest;
import cn.cyberpeace.mp.auth.dto.target.BindRoleTargetsRequest;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.OrgUserModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.util.ServletUtils;
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

@Api(value = "组织-用户管理接口", tags = "OrgUserController")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/org_users")
public class OrgUserController extends BaseModelController<OrgUserModel, QueryParams, OrgUserService> {

    @Autowired
    public OrgUserController(OrgUserService modelService) {
        setModelService(modelService);
    }


    @ApiOperation(value = "根据orgId查询组织下所有用户信息", notes = "")
    @GetMapping("/byorg")
    public ServiceResult getByOrg(@RequestParam String orgId) {
        List<UserModel> users = getModelService().listUsersByOrgId(orgId);
        return ServiceResult.builder().source(users).build().asResult();
    }


    @ApiOperation(value = "根据userId查询所在组织信息（按照层级排列）", notes = "")
    @GetMapping("/hierarchical/byuser")
    public ServiceResult listHierarchicalOrgs(@RequestParam String userId) {
        List<OrgDto> orgDtos = getModelService().listHierarchicalOrgsByUserId(userId);
        // todo
        return ServiceResult.builder().source(orgDtos).build().asResult();
    }



    @ApiOperation(value = "增量绑定组织-用户关系！", notes = "")
    @PostMapping("/bind")
    public ServiceResult bindOrgPermission(@RequestBody @Validated BindOrgUsersRequest bindOrgUsersReq) {
        boolean success = getModelService().additionalBindOrgUsers(bindOrgUsersReq.getOrgId(), bindOrgUsersReq.getUserIds());
        return ServletUtils.commonResult(success, null);
    }



}
