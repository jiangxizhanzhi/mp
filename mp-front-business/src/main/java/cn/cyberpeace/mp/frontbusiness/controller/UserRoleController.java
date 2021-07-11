package cn.cyberpeace.mp.frontbusiness.controller;

import cn.cyberpeace.mp.auth.bo.role.UserRoleStatisticBo;
import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.request.role.RoleUsersRequest;
import cn.cyberpeace.mp.auth.request.role.UserRolePageRequest;
import cn.cyberpeace.mp.auth.request.role.UserRolesRequest;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(value = "用户-角色管理接口(业务)", tags = "AUTH-用户-角色管理接口(业务)")
@Slf4j
@RestController("userRoleFrontController")
@RequestMapping(ControllerConstant.BASE_API_PATH + "/user_roles")
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    @ApiOperation(value = "分页查询 根据userId查询用户所有角色", notes = "")
    @GetMapping("/roles/page/byuser")
    public ListObjServiceResult<RoleModel> getRolesByUser(UserRolePageRequest pageRequest) {
        ListObjServiceResult result = userRoleService.pageByUserId(pageRequest);
        return result;
    }

    @ApiOperation(value = "分页查询 根据roleId查询角色绑定的所有用户", notes = "")
    @GetMapping("/users/page/byrole")
    public ListObjServiceResult<UserBo> getUsersByRole(UserRolePageRequest pageRequest) {
        ListObjServiceResult result = userRoleService.pageByRoleId(pageRequest);
        return result;
    }

    @ApiOperation(value = "查询所有角色以及绑定的用户数量", notes = "")
    @GetMapping("/statistics")
    public ServiceResult<List<UserRoleStatisticBo>> getUserRoleStatistics() {
        List statistics = userRoleService.getUserRoleStatistics();
        return ResultUtils.objList2ListObjServiceResult(statistics, statistics.size());
    }


    @ApiOperation(value = "绑定用户-角色关系(单个用户 -> 多个角色)，注意，该操作会删除原有用户的所有角色。 如果roleIds为空，表示删除用户所有角色！", notes = "")
    @ApiImplicitParam(name="userRolesReq", value="绑定请求", dataTypeClass = UserRolesRequest.class)
    @PostMapping("/bind/userRoles")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult bindUserRoles(@RequestBody @Validated UserRolesRequest userRolesReq) {
        boolean success = userRoleService.bindUserRoles(userRolesReq.getUserId(), userRolesReq.getRoleIds(), true);
        if (success) {
            return ResultCommon.success();
        } else {
            log.error("bind user roles failed!");
            throw new MpRuntimeException(AuthErrorCode.USER_ROLE_ADD_ERROR);
        }
    }


    @ApiOperation(value = "绑定用户-角色关系(单个角色 -> 多个用户)", notes = "")
    @ApiImplicitParam(name="roleUsersReq", value="绑定请求", dataTypeClass = RoleUsersRequest.class)
    @PostMapping("/bind/roleUsers")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult bindRoleUsers(@RequestBody @Validated RoleUsersRequest roleUsersReq) {
        boolean success = userRoleService.bindRoleUsers(roleUsersReq.getRoleId(), roleUsersReq.getUserIds());
        if (success) {
            return ResultCommon.success();
        } else {
            log.error("bind role users failed!");
            throw new MpRuntimeException(AuthErrorCode.USER_ROLE_ADD_ERROR);
        }
    }


    @ApiOperation(value = "删除用户-角色关系(通过 role_id 和 user_ids组合)", notes = "")
    @ApiImplicitParam(name="roleUsersReq", value="删除请求", dataTypeClass = RoleUsersRequest.class)
    @PostMapping("/delete/byRoleUsers")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult deleteByRoleIdAnUserIds(@RequestBody @Validated RoleUsersRequest roleUsersReq) {
        boolean success = userRoleService.deleteByRoleIdAndUserIds(roleUsersReq.getRoleId(), roleUsersReq.getUserIds());
        if (success) {
            return ResultCommon.success();
        } else {
            log.error("delete user role relation failed!");
            throw new MpRuntimeException(AuthErrorCode.USER_ROLE_DELETE_ERROR);
        }
    }

}
