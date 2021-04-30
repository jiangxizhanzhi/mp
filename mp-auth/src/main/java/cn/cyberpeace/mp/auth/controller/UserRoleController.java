package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/user_roles")
public class UserRoleController extends BaseModelController<UserRoleModel, UserRoleService> {

    @Autowired
    public UserRoleController(UserRoleService modelService) {
        setModelService(modelService);
    }

}
