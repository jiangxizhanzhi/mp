package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.UserPermissionModel;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/user_permissions")
public class UserPermissionController extends BaseModelController<UserPermissionModel, UserPermissionService> {

    @Autowired
    public UserPermissionController(UserPermissionService modelService) {
        setModelService(modelService);
    }

}
