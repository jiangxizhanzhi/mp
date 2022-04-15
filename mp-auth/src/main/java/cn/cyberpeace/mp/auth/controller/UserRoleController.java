package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.role.RoleUsersRequest;
import cn.cyberpeace.mp.auth.dto.role.UserRolePageRequest;
import cn.cyberpeace.mp.auth.dto.role.UserRoleStatistic;
import cn.cyberpeace.mp.auth.dto.role.UserRolesRequest;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "用户-角色管理接口", tags = "AUTH-用户-角色管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/user_roles")
public class UserRoleController extends BaseModelController<UserRoleModel, QueryParams, UserRoleService> {

    @Autowired
    public UserRoleController(UserRoleService modelService) {
        setModelService(modelService);
    }


    @ApiOperation(value = "分页查询 根据userId查询用户所有角色", notes = "")
    @GetMapping("/roles/page/byuser")
    public ListObjServiceResult getRolesByUser(UserRolePageRequest pageRequest) {
        PageInfo pageInfo = getModelService().pageByUserId(pageRequest);
        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }

    @ApiOperation(value = "分页查询 根据roleId查询角色绑定的所有用户", notes = "")
    @GetMapping("/users/page/byrole")
    public ListObjServiceResult getUsersByRole(UserRolePageRequest pageRequest) {
        PageInfo pageInfo = getModelService().pageByRoleId(pageRequest);
        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }

    @ApiOperation(value = "查询所有角色以及绑定的用户数量", notes = "")
    @GetMapping("/statistics")
    public ServiceResult<List<UserRoleStatistic>> getUserRoleStatistics() {
        List statistics = getModelService().getUserRoleStatistics();
        return ResultUtils.objList2ListObjServiceResult(statistics, statistics.size());
    }


    @ApiOperation(value = "绑定用户-角色关系(单个用户 -> 多个角色)，注意，该操作会删除原有用户的所有角色。 如果roleIds为空，表示删除用户所有角色！", notes = "")
    @PostMapping("/bind/userRoles")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult bindUserRoles(@RequestBody @Validated UserRolesRequest userRolesReq) {
        boolean success = getModelService().bindUserRoles(userRolesReq.getUserId(), userRolesReq.getRoleIds(), true);
        return ResultUtils.commonResult(success, null);
    }


    @ApiOperation(value = "绑定用户-角色关系(单个角色 -> 多个用户)", notes = "")
    @PostMapping("/bind/roleUsers")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult bindRoleUsers(@RequestBody @Validated RoleUsersRequest roleUsersReq) {
        boolean success = getModelService().bindRoleUsers(roleUsersReq.getRoleId(), roleUsersReq.getUserIds());
        return ResultUtils.commonResult(success, null);
    }


    @ApiOperation(value = "删除用户-角色关系(通过 role_id 和 user_ids组合)", notes = "")
    @PostMapping("/delete/byRoleUsers")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult deleteByRoleIdAnUserIds(@RequestBody @Validated RoleUsersRequest roleUsersReq) {
        boolean success = getModelService().deleteByRoleIdAndUserIds(roleUsersReq.getRoleId(), roleUsersReq.getUserIds());
        return ResultUtils.commonResult(success, null);
    }

}
