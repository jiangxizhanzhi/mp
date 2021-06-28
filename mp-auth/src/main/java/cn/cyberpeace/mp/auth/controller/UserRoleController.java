package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.role.BindUserRolesRequest;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.util.ServletUtils;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.controller.BaseModelController;
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

@Api(value = "用户-角色管理接口", tags = "UserRoleController")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/user_roles")
public class UserRoleController extends BaseModelController<UserRoleModel, UserRoleService> {

    @Autowired
    public UserRoleController(UserRoleService modelService) {
        setModelService(modelService);
    }


    @ApiOperation(value = "根据userId查询用户所有角色", notes = "")
    @GetMapping("/byuser")
    public ServiceResult getRolesByUser(@RequestParam String userId) {
        List<RoleModel> roles = getModelService().listByUserId(userId);
//        return ServiceResult.builder().source(roles).build().asResult();
        return ServletUtils.commonResult(true, roles);
    }


    @ApiOperation(value = "绑定用户-角色关系，注意，如果roleIds为空，表示删除用户所有角色！", notes = "")
    @PostMapping("/bind")
    public ServiceResult bindUserRoles(@RequestBody @Validated BindUserRolesRequest userRolesReq) {
        boolean success = getModelService().bindUserRoles(userRolesReq.getUserId(), userRolesReq.getRoleIds());
        return ServletUtils.commonResult(success, null);
    }


}
