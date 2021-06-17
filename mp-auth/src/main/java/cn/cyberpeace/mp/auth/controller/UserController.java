package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "用户管理接口", tags = "UserController")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/users")
public class UserController extends BaseModelController<UserModel, UserService> {

    @Autowired
    public UserController(UserService modelService) {
        setModelService(modelService);
    }


    @PostMapping("/excel/export")
    public ServiceResult exportUsers() {
        // todo excel 导出
        return null;
    }


    @PostMapping("/excel/import")
    public ServiceResult importUsers() {
        // todo excel 导入
        return null;
    }

    @GetMapping("/excel/template")
    public ServiceResult excelTemplate() {
        // todo excel 模板
        return null;
    }





}
