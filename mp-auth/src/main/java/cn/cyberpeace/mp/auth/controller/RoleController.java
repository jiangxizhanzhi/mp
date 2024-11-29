package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.bo.role.RoleBo;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.request.role.RolePageRequest;
import cn.cyberpeace.mp.auth.request.role.RoleRequest;
import cn.cyberpeace.mp.auth.service.OperateRolePermissionService;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.auth.util.ThirdSsoUtil;
import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 角色管理接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "角色管理接口", tags = "角色管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/roles")
@AuthTarget(TargetEnum.USER_MANAGE)
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private OperateRolePermissionService opRolePermService;


    @ApiOperation("删除指定id角色")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", paramType = "path", value = "id", dataTypeClass = String.class),})
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.DELETE, title = "删除角色", target = OpTargetEnums.ROLE)
    public ServiceResult<Boolean> delete(@PathVariable String id) {
        var model = roleService.get(id).getData();
        ThirdSsoUtil.SyncRole syncRole = new ThirdSsoUtil.SyncRole();
        syncRole.setApp_role__name(model.getName());
        syncRole.setApp_role__status("0");
        syncRole.setApp_role__remarks("");
        syncRole.setApp_role__code(id);
        opRolePermService.checkByTargetRoleIds(Arrays.asList(id));
        var res = roleService.delete(id);
        ThirdSsoUtil.syncRole(syncRole);
        return res;
    }

    /**
     * 批量删除
     * @param ids 数据id集
     * @return 成功数量
     */
    @AuthTarget(TargetEnum.USER_MANAGE)
    @ApiOperation("批量删除角色")
    @ApiImplicitParam(name = "ids", value = "角色ID列表", dataTypeClass = String[].class)
    @DeleteMapping("/bulk_delete")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult<Integer> bulkDelete(@RequestBody String[] ids) {
        List<ThirdSsoUtil.SyncRole> roles = new ArrayList<>();
        for (String id : ids) {
            var model = roleService.get(id).getData();
            ThirdSsoUtil.SyncRole syncRole = new ThirdSsoUtil.SyncRole();
            syncRole.setApp_role__name(model.getName());
            syncRole.setApp_role__status("0");
            syncRole.setApp_role__remarks("");
            syncRole.setApp_role__code(id);
            roles.add(syncRole);
        }
        var res = roleService.bulkDelete(ids).asResult();
        for (var r : roles) {
            ThirdSsoUtil.syncRole(r);
        }
        return res;
    }

    @AuthTarget(TargetEnum.USER_MANAGE)
    @GetMapping("/{id}")
    @ApiOperation(value = "查询指定id角色，附带权限集合", notes = "")
    @ApiImplicitParam(name = "id", value = "角色ID", dataTypeClass = String.class)
    public ObjServiceResult get(@PathVariable String id) {
        RoleBo roleBo = roleService.getRoleDto(id);
        return ResultUtils.obj2ObjServiceResult(roleBo);
    }

    @ApiOperation("获取指定id角色")
    @AuthTarget(TargetEnum.USER_MANAGE)
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "数据id", dataTypeClass = String.class)})
    @GetMapping
    public ObjServiceResult<RoleModel> getByParam(@RequestParam String id) {
        return roleService.get(id).asResult();
    }

    @AuthTarget(TargetEnum.USER_MANAGE)
    @ApiOperation(value = "创建角色，同时完成角色-权限绑定关系", notes = "")
    @ApiImplicitParam(name = "createRequest", value = "请求", dataTypeClass = RoleRequest.class)
    @PostMapping("/add")
//    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.CREATE, title = "新增角色", target = OpTargetEnums.ROLE)
    public ServiceResult<RoleModel> add(@RequestBody @Validated RoleRequest createRequest) {
        RoleModel roleModel = roleService.add(createRequest);

        ThirdSsoUtil.SyncRole syncRole = new ThirdSsoUtil.SyncRole();
        syncRole.setApp_role__name(roleModel.getName());
        syncRole.setApp_role__status("1");
        syncRole.setApp_role__remarks("");
        syncRole.setApp_role__code(roleModel.getId());
        ThirdSsoUtil.syncRole(syncRole);

        return ResultCommon.success(roleModel);
    }

    @AuthTarget(TargetEnum.NO_AUTH)
    @ApiOperation(value = "更新角色，同时完成角色-权限绑定关系", notes = "")
    @ApiImplicitParam(name = "modifyRequest", value = "修改请求", dataTypeClass = RoleRequest.class)
    @PostMapping("/modify")
//    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "修改角色", target = OpTargetEnums.ROLE)
    public ServiceResult<RoleModel> modify(@RequestBody @Validated RoleRequest modifyRequest) {
        RoleModel roleModel = roleService.modify(modifyRequest);

        ThirdSsoUtil.SyncRole syncRole = new ThirdSsoUtil.SyncRole();
        syncRole.setApp_role__name(roleModel.getName());
        syncRole.setApp_role__status("1");
        syncRole.setApp_role__remarks("");
        syncRole.setApp_role__code(roleModel.getId());
        ThirdSsoUtil.syncRole(syncRole);

        return ResultCommon.success(roleModel);
    }

    @AuthTarget({TargetEnum.USER_MANAGE, TargetEnum.SYSTEM_NOTICE_FOLDER})
    @ApiOperation(value = "查询所有角色，不分页，仅查询角色信息", notes = "")
    @GetMapping("/all")
    public ServiceResult<List<RoleModel>> listAll(RolePageRequest rolePageRequest) {
        rolePageRequest.ensure();
        List<RoleModel> roles = roleService.listAll(rolePageRequest);
        return ResultCommon.success(roles);
    }

}
