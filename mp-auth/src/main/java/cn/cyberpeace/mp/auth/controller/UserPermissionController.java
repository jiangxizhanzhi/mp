package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserPermissionModel;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
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

@Api(value = "用户-权限管理接口", tags = "UserPermissionController")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/user_permissions")
public class UserPermissionController extends BaseModelController<UserPermissionModel, UserPermissionService> {

    @Autowired
    public UserPermissionController(UserPermissionService modelService) {
        setModelService(modelService);
    }


    @ApiOperation(value = "根据userId查询所有权限", notes = "")
    @GetMapping("/byuser")
    public ServiceResult getTargetsByUser(@RequestParam String userId) {
        List<TargetModel> targets = getModelService().listByUserId(userId);
        return ServiceResult.builder().source(targets).build().asResult();
    }



    @ApiOperation(value = "根据userId查询所有菜单权限", notes = "")
    @GetMapping("/menus/byuser")
    public ServiceResult getMenuTargetsByUser(@RequestParam String userId) {
        List<TargetModel> targets = getModelService().listMenusByUserId(userId);
        return ServiceResult.builder().source(targets).build().asResult();
    }



}
