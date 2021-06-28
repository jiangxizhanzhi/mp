package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.TargetDto;
import cn.cyberpeace.mp.auth.model.OrgUserModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "组织-用户管理接口", tags = "OrgUserController")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/org_users")
public class OrgUserController extends BaseModelController<OrgUserModel, OrgUserService> {

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





}
