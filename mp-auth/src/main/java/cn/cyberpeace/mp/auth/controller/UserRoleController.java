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
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ListObjServiceResult getRolesByUser(UserRolePageRequest pageRequest) {
        PageInfo pageInfo = getModelService().pageByUserId(pageRequest);
        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }

    @ApiOperation(value = "分页查询 根据roleId查询角色绑定的所有用户", notes = "")
    @GetMapping("/users/page/byrole")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ListObjServiceResult getUsersByRole(UserRolePageRequest pageRequest) {
        PageInfo pageInfo = getModelService().pageByRoleId(pageRequest);
        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }

    @ApiOperation(value = "查询所有角色以及绑定的用户数量", notes = "")
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
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





    /**
     * 通用查询方法
     * @param queryParams 查询参数
     * @return 列表查询结果
     */
    @Override
    @ApiOperation("获取分页数据")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ListObjServiceResult<UserRoleModel> list(QueryParams queryParams) {
        handleQueryParams("list", queryParams);
        return getModelService().list(queryParams).asResult();
    }

    /**
     * 通用单条数据id查询
     * @param id 数据id
     * @param fields 返回数据字段
     * @return 单条数据结果
     */
    @Override
    @ApiOperation("获取指定id数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ObjServiceResult<UserRoleModel> get(@PathVariable String id, @RequestParam(required = false) String[] fields) {
        log.info("id:{}, fields:{}", id, fields);
        return getModelService().get(id, fields).asResult();
    }

    /**
     * 通用创建
     * @param obj 数据对象
     * @param fields 返回数据字段
     * @return 单条数据结果
     */
    @Override
    @ApiOperation("创建数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ObjServiceResult<UserRoleModel> create(@RequestBody UserRoleModel obj, @RequestParam(required = false) String[] fields) {
        return getModelService().create(obj, fields).asResult();
    }

    /**
     * 通用更新
     * @param id 数据id
     * @param obj 数据对象
     * @param fields 返回数据字段，只涉及更新字段
     * @return 单条数据结果
     */
    @Override
    @ApiOperation("更新指定id数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ObjServiceResult<UserRoleModel> update(@PathVariable String id, @RequestBody UserRoleModel obj,
                                                  @RequestParam(required = false) String[] fields) {
        obj.setId(id);
        return getModelService().update(obj, fields).asResult();
    }

    /**
     * 通用删除
     * @param id 数据id
     * @return 是否删除成功
     */
    @Override
    @ApiOperation("删除指定id数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult<Boolean> delete(@PathVariable String id) {
        log.info("id:{}", id);
        return getModelService().delete(id).asResult();
    }

    /**
     * 通用批量创建
     * @param objs 创建对象集
     * @return 成功数量
     */
    @Override
    @ApiOperation("批量创建数据")
    @PostMapping("/bulk_create")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult<Integer> bulkCreate(@RequestBody List<UserRoleModel> objs) {
        return getModelService().bulkCreate(objs).asResult();
    }

    /**
     * 通用批量更新
     * @param objs 更新对象集，需要更新的字段必须一致
     * @return 成功数量
     */
    @Override
    @ApiOperation("批量更新数据")
    @PatchMapping("/bulk_update")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult<Integer> bulkUpdate(@RequestBody List<UserRoleModel> objs) {
        return getModelService().bulkUpdate(objs).asResult();
    }

    /**
     * 通用批量删除
     * @param ids 数据id集
     * @return 成功数量
     */
    @Override
    @ApiOperation("批量删除数据")
    @DeleteMapping("/bulk_delete")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult<Integer> bulkDelete(@RequestBody String[] ids) {
        return getModelService().bulkDelete(ids).asResult();
    }

}
