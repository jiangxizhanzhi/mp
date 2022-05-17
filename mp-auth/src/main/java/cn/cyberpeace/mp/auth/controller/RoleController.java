package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthLogConstant;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.role.RoleDto;
import cn.cyberpeace.mp.auth.dto.role.RolePageRequest;
import cn.cyberpeace.mp.auth.dto.role.RolePermDto;
import cn.cyberpeace.mp.auth.dto.role.RoleRequest;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.service.OperateRolePermissionService;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.log.annotation.OperateLog;
import cn.cyberpeace.mp.devops.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTypeEnums;
import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Api(value = "角色管理接口", tags = "AUTH-角色管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/roles")
public class RoleController extends BaseModelController<RoleModel, QueryParams, RoleService> {

    @Autowired
    public RoleController(RoleService modelService) {
        setModelService(modelService);
    }

    @Autowired
    private OperateRolePermissionService opRolePermService;


    @Override
    @ApiOperation(value = "(弃用，建议用/add) 创建数据", notes = "")
    @ApiIgnore
    @ApiImplicitParams({
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ObjServiceResult<RoleModel> create(@RequestBody RoleModel obj, @RequestParam String[] fields) {
        return super.create(obj, fields);
    }


    @Override
    @ApiIgnore
    @ApiOperation(value = "(弃用，建议用/modify) 修改数据", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ObjServiceResult<RoleModel> update(@PathVariable String id, @RequestBody RoleModel obj,
                                              @RequestParam(required = false) String[] fields) {
        opRolePermService.checkByTargetRoleIds(Arrays.asList(id));
        return super.update(id, obj, fields);
    }

    @Override
    @ApiOperation("删除指定id角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="id",dataTypeClass = String.class),
    })
    @DeleteMapping("/{id}")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.DELETE, title = "删除角色", target = AuthLogConstant.TARGET_ROLE)
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult<Boolean> delete(@PathVariable String id) {
        opRolePermService.checkByTargetRoleIds(Arrays.asList(id));
        return super.delete(id);
    }

    @Override
    @ApiOperation(value = "查询指定id数据，附带权限集合", notes = "")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ObjServiceResult get(@PathVariable String id, @RequestParam(required = false) String[] fields) {
        RoleDto roleDto = getModelService().getRoleDto(id);
        return ResultUtils.obj2ObjServiceResult(roleDto);
    }

    @ApiOperation("查询指定id角色（附带权限、用户数）")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @GetMapping("/perm/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ObjServiceResult getRolePerm(@PathVariable String id, @RequestParam(required = false) String[] fields) {
        RolePermDto rolePermDto = getModelService().getRolePermDto(id);

        return ResultUtils.obj2ObjServiceResult(rolePermDto);
    }

    @ApiOperation(value = "创建角色，同时完成角色-权限绑定关系", notes = "")
    @PostMapping("/add")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.CREATE, title = "新增角色", target = AuthLogConstant.TARGET_ROLE)
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult add(@RequestBody @Validated RoleRequest createRequest) {
        RoleModel roleModel = getModelService().add(createRequest);
        return ResultUtils.commonResult(true, roleModel);
    }


    @ApiOperation(value = "更新角色，同时完成角色-权限绑定关系", notes = "")
    @PostMapping("modify")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "修改角色", target = AuthLogConstant.TARGET_ROLE)
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult modify(@RequestBody @Validated RoleRequest modifyRequest) {
        RoleModel roleModel = getModelService().modify(modifyRequest);
        return ResultUtils.commonResult(true, roleModel);
    }

    @ApiOperation(value = "根据租户查询所有角色", notes = "")
    @GetMapping("/bytenant")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult getRoleByTenant(@RequestParam(required = false) String tenant) {
        List<RoleModel> roles;
        if (StringUtils.isNotBlank(tenant)) {
            roles = getModelService().listByTenantId(tenant);
        } else {
            roles = getModelService().listAll();
        }
        return ServiceResult.builder().source(roles).build().asResult();
    }


    @ApiOperation(value = "分页查询角色列表（附加权限信息，绑定的用户信息）", notes = "")
    @GetMapping("/page")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult getRolesWithUserCnt(RolePageRequest pageRequest) {
        pageRequest.ensure();
        PageInfo page = getModelService().getPageWithUserCnt(pageRequest);

        return ResultUtils.pageInfo2ObjServiceResult(page);
    }


    @ApiOperation(value = "查询所有角色", notes = "")
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult getAllRoles() {
        List<RoleModel> roles = getModelService().listAllExcludeSuperUserRole();
        return ServiceResult.builder().source(roles).build().asResult();
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
    public ListObjServiceResult<RoleModel> list(QueryParams queryParams) {
        handleQueryParams("list", queryParams);
        return getModelService().list(queryParams).asResult();
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
    public ServiceResult<Integer> bulkCreate(@RequestBody List<RoleModel> objs) {
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
    public ServiceResult<Integer> bulkUpdate(@RequestBody List<RoleModel> objs) {
        List<String> roleIds = objs.stream().map(RoleModel::getId).filter(Objects::nonNull).collect(Collectors.toList());
        opRolePermService.checkByTargetRoleIds(roleIds);
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
        opRolePermService.checkByTargetRoleIds(CollectionUtil.toList(ids));
        return getModelService().bulkDelete(ids).asResult();
    }

}
