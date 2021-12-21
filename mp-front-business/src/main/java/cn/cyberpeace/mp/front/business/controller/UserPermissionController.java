package cn.cyberpeace.mp.front.business.controller;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.target.BindUserTargetsRequest;
import cn.cyberpeace.mp.auth.dto.target.TargetDto;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
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

/**
 * @Description: 用户权限管理接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "用户-权限管理接口(业务)", tags = "AUTH-用户-权限管理接口(业务)")
@Slf4j
@RestController("userPermissionFrontController")
@RequestMapping(ControllerConstant.BASE_API_PATH + "/user_permissions")
public class UserPermissionController {

    @Autowired
    private UserPermissionService userPermissionService;


    @ApiOperation(value = "根据userId查询所有权限", notes = "")
    @GetMapping("/byuser")
    public ServiceResult getTargetsByUser(String userId) {
        List<TargetModel> targets = userPermissionService.listTargetsByUserId(userId);
        return ServiceResult.builder().source(targets).build().asResult();
    }


    @ApiOperation(value = "根据userId查询所有权限", notes = "")
    @GetMapping("/tree/byuser")
    public ServiceResult getTargetsTreeByUser(@RequestParam String userId) {
        List<TargetDto> tree = userPermissionService.getTreeByUserId(userId);
        return ServiceResult.builder().source(tree).build().asResult();
    }



    @ApiOperation(value = "根据userId查询所有菜单权限（对象集合）", notes = "")
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
    @PostMapping("/bind")
    public ServiceResult bindUserRoles(@RequestBody @Validated BindUserTargetsRequest userTargetsReq) {
        boolean success = userPermissionService.bindUserTargets(userTargetsReq.getUserId(), userTargetsReq.getTargetIds(), true);
        if (success) {
            return ServiceResult.successful();
        } else {
            log.error("绑定用户资源失败!");
            throw new MpRuntimeException(AuthErrorCode.USER_TARGET_ADD_ERROR);
        }
    }



}
