package cn.cyberpeace.mp.frontbusiness.controller;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.bo.target.TargetBo;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.request.target.BindUserTargetsRequest;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(value = "用户-权限管理接口(业务)", tags = "AUTH-用户-权限管理接口(业务)")
@Slf4j
@RestController("userPermissionFrontController")
@RequestMapping(ControllerConstant.BASE_API_PATH + "/user_permissions")
public class UserPermissionController {

    @Autowired
    private UserPermissionService userPermissionService;


    @ApiOperation(value = "根据userId查询所有权限", notes = "")
    @ApiImplicitParam(name = "userId", value = "用户ID", dataTypeClass = String.class)
    @GetMapping("/byuser")
    public ServiceResult getTargetsByUser(String userId) {
        List<TargetModel> targets = userPermissionService.listTargetsByUserId(userId);
        return ServiceResult.builder().source(targets).build().asResult();
    }


    @ApiOperation(value = "根据userId查询所有权限", notes = "")
    @ApiImplicitParam(name = "userId", value = "用户ID", dataTypeClass = String.class)
    @GetMapping("/tree/byuser")
    public ServiceResult getTargetsTreeByUser(@RequestParam String userId) {
        List<TargetBo> tree = userPermissionService.getTreeByUserId(userId);
        return ServiceResult.builder().source(tree).build().asResult();
    }



    @ApiOperation(value = "根据userId查询所有菜单权限（对象集合）", notes = "")
    @ApiImplicitParam(name = "userId", value = "用户ID", dataTypeClass = String.class)
    @GetMapping("/menus/byuser")
    public ServiceResult getMenuTargetsByUser(@RequestParam String userId) {
        List<TargetModel> targets = userPermissionService.listMenusByUserId(userId);
        return ServiceResult.builder().source(targets).build().asResult();
    }


    @ApiOperation(value = "查询当前登录用户所有菜单权限（对象集合）", notes = "")
    @GetMapping("/menus/byloginuser")
    public ServiceResult getMenuTargetsByLoginUser() {
        List<TargetModel> targets = userPermissionService.listMenusByLoginUser();
        return ServiceResult.builder().source(targets).build().asResult();
    }


    @ApiOperation(value = "查询当前登录用户所有菜单权限（编码集合）", notes = "")
    @GetMapping("/menuidents/byloginuser")
    public ServiceResult listMenuIdentsByLoginUser() {
        List<String> menuIdents = userPermissionService.listMenuIdentsByLoginUser();
        return ServiceResult.builder().source(menuIdents).build().asResult();
    }


    @ApiOperation(value = "绑定用户-资源关系，注意，该操作会清除用户原来绑定的权限。如果targetIds为空，表示删除用户所有角色！", notes = "")
    @ApiImplicitParam(name="userTargetsReq", value="绑定请求", dataTypeClass = BindUserTargetsRequest.class)
    @PostMapping("/bind")
    public ServiceResult bindUserPermissions(@RequestBody @Validated BindUserTargetsRequest userTargetsReq) {
        boolean success = userPermissionService.bindUserTargets(userTargetsReq.getUserId(), userTargetsReq.getTargetIds(), true);
        if (success) {
            return ResultCommon.success();
        } else {
            log.error("bind user permissions failed!");
            throw new MpRuntimeException(AuthErrorCode.USER_TARGET_ADD_ERROR);
        }
    }



}
