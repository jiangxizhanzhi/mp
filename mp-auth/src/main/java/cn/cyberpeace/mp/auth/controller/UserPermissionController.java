package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.TargetDto;
import cn.cyberpeace.mp.auth.dto.target.BindUserTargetsRequest;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserPermissionModel;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.auth.util.ResultUtils;
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

@Api(value = "用户-权限管理接口", tags = "用户-权限管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/user_permissions")
public class UserPermissionController extends BaseModelController<UserPermissionModel, QueryParams, UserPermissionService> {

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


    @ApiOperation(value = "根据userId查询所有权限", notes = "")
    @GetMapping("/tree/byuser")
    public ServiceResult getTargetsTreeByUser(@RequestParam String userId) {
        List<TargetDto> tree = getModelService().getTreeByUserId(userId);
        return ServiceResult.builder().source(tree).build().asResult();
    }



    @ApiOperation(value = "根据userId查询所有菜单权限（对象集合）", notes = "")
    @GetMapping("/menus/byuser")
    public ServiceResult getMenuTargetsByUser(@RequestParam String userId) {
        List<TargetModel> targets = getModelService().listMenusByUserId(userId);
        return ServiceResult.builder().source(targets).build().asResult();
    }


    @ApiOperation(value = "查询当前登录用户所有菜单权限（对象集合）", notes = "")
    @GetMapping("/menus/byloginuser")
    public ServiceResult getMenuTargetsByLoginUser() {
        List<TargetModel> targets = getModelService().listMenusByLoginUser();
        return ServiceResult.builder().source(targets).build().asResult();
    }


    @ApiOperation(value = "查询当前登录用户所有菜单权限（编码集合）", notes = "")
    @GetMapping("/menuidents/byloginuser")
    public ServiceResult listMenuIdentsByLoginUser() {
        List<String> menuIdents = getModelService().listMenuIdentsByLoginUser();
        return ServiceResult.builder().source(menuIdents).build().asResult();
    }


    @ApiOperation(value = "绑定用户-资源关系，注意，如果roleIds为空，表示删除用户所有角色！", notes = "")
    @PostMapping("/bind")
    public ServiceResult bindUserRoles(@RequestBody @Validated BindUserTargetsRequest userTargetsReq) {
        boolean success = getModelService().bindUserTargets(userTargetsReq.getUserId(), userTargetsReq.getTargetIds());
        return ResultUtils.commonResult(success, null);
    }



}
