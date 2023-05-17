package cn.cyberpeace.mp.frontbusiness.controller;

import cn.cyberpeace.mp.auth.bo.role.RoleBo;
import cn.cyberpeace.mp.auth.bo.role.UserRoleStatisticBo;
import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.request.role.RoleUsersRequest;
import cn.cyberpeace.mp.auth.request.role.UserRolePageRequest;
import cn.cyberpeace.mp.auth.request.role.UserRolesRequest;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.util.AuthLogUtils;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import com.google.common.base.Joiner;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 用户角色管理接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "用户-角色管理接口(业务)", tags = "AUTH-用户-角色管理接口(业务)")
@Slf4j
@RestController("userRoleFrontController")
@RequestMapping(ControllerConstant.BASE_API_PATH + "/user_roles")
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

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
        String roleId = roleUsersReq.getRoleId();
        String roleName = getRoleName(roleId);
        List<UserModel> users = getUsersByRoleId(roleId);
        boolean success = userRoleService.bindRoleUsers(roleUsersReq.getRoleId(), roleUsersReq.getUserIds());
        List<UserModel> updatedUsers = getUsersByRoleId(roleId);
        String logContent = getLogContent(roleName, users, updatedUsers);
        AuthLogUtils.recordAuthLog(OpTypeEnums.UPDATE, OpTargetEnums.ROLE,"修改角色用户关系",logContent, StringUtils.EMPTY);
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
        String roleId = roleUsersReq.getRoleId();
        String roleName = getRoleName(roleId);
        List<UserModel> users = getUsersByRoleId(roleId);
        boolean success = userRoleService.deleteByRoleIdAndUserIds(roleUsersReq.getRoleId(), roleUsersReq.getUserIds());
        List<UserModel> updatedUsers = getUsersByRoleId(roleId);
        String logContent = getLogContent(roleName, users, updatedUsers);
        AuthLogUtils.recordAuthLog(OpTypeEnums.UPDATE, OpTargetEnums.ROLE,"修改角色用户关系",logContent, StringUtils.EMPTY);
        if (success) {
            return ResultCommon.success();
        } else {
            log.error("delete user role relation failed!");
            throw new MpRuntimeException(AuthErrorCode.USER_ROLE_DELETE_ERROR);
        }
    }

    /**
     * @description: 获取角色名称
     * @param roleId
     * @return String
     */
    private String getRoleName(String roleId){
        RoleBo roleDto = roleService.getRoleDto(roleId);
        return roleDto.getName();
    }

    /**
     * @description: 获取角色下的用户信息
     * @param roleId
     * @return List<UserModel>
     */
    private List<UserModel> getUsersByRoleId(String roleId){
        return userRoleService.listUsersByRoleIdNoRestrict(roleId);
    }

    /**
     * @description: 获取日志content内容
     * @param roleName 角色名称
     * @param users 操作前角色下的用户
     * @param updatedUsers 操作后角色下的用户
     * @return String
     */
    private String getLogContent(String roleName,List<UserModel> users,List<UserModel> updatedUsers){
        //获取删除的用户
        Collection<UserModel> deletedUsers = CollectionUtils.subtract(users, updatedUsers);
        //获取新增的用户
        Collection<UserModel> addedUsers = CollectionUtils.subtract(updatedUsers, users);
        StringBuilder contentBuilder = new StringBuilder();
        if(CollectionUtils.isNotEmpty(deletedUsers) || CollectionUtils.isNotEmpty(addedUsers)){
            contentBuilder.append("更新角色");
            contentBuilder.append(roleName);
            contentBuilder.append("下的用户。");
            if(CollectionUtils.isNotEmpty(deletedUsers)){
                contentBuilder.append(" 删除了用户：");
                List<String> nameList = deletedUsers.stream().map(UserModel::getName).collect(Collectors.toList());
                String userNames = Joiner.on(",").join(nameList);
                contentBuilder.append(userNames);
            }
            if(CollectionUtils.isNotEmpty(addedUsers)){
                contentBuilder.append(" 新增了用户：");
                List<String> updatedNameList = addedUsers.stream().map(UserModel::getName).collect(Collectors.toList());
                String UpdatedUserNames = Joiner.on(",").join(updatedNameList);
                contentBuilder.append(UpdatedUserNames);
            }
        }
        return contentBuilder.toString();
    }

}
