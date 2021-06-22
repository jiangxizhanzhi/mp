package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.OrgUserModel;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.impl.OrgUserServiceImpl;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "组织用户管理接口", tags = "OrgUserController")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/org_user")
public class OrgUserController extends BaseModelController<OrgUserModel, OrgUserService> {

    @Autowired
    public OrgUserController(OrgUserService modelService) {
        setModelService(modelService);
    }

}
