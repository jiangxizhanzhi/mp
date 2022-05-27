package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.role.RoleDto;
import cn.cyberpeace.mp.auth.dto.role.RoleRequest;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.service.OperateRolePermissionService;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.log.annotation.OperateLog;
import cn.cyberpeace.mp.devops.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTypeEnums;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * @Description: 角色管理接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "角色管理接口", tags = "AUTH-角色管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private OperateRolePermissionService opRolePermService;


    @ApiOperation("删除指定id角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="id",dataTypeClass = String.class),
    })
    @DeleteMapping("/{id}")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.DELETE, title = "删除角色", target = OpTargetEnums.ROLE)
    public ServiceResult<Boolean> delete(@PathVariable String id) {
        opRolePermService.checkByTargetRoleIds(Arrays.asList(id));
        return roleService.delete(id);
    }

    /**
     * 批量删除
     * @param ids 数据id集
     * @return 成功数量
     */
    @ApiOperation("批量删除角色")
    @DeleteMapping("/bulk_delete")
    public ServiceResult<Integer> bulkDelete(@RequestBody String[] ids) {
        return roleService.bulkDelete(ids).asResult();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "查询指定id数据，附带权限集合", notes = "")
    public ObjServiceResult get(@PathVariable String id) {
        RoleDto roleDto = roleService.getRoleDto(id);
        return ResultUtils.obj2ObjServiceResult(roleDto);
    }

    @ApiOperation("获取指定id数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "数据id", dataTypeClass = String.class)
    })
    @GetMapping
    public ObjServiceResult<RoleModel> getByParam(@RequestParam String id) {
        return roleService.get(id).asResult();
    }

    @ApiOperation(value = "创建角色，同时完成角色-权限绑定关系", notes = "")
    @PostMapping("/add")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.CREATE, title = "新增角色", target = OpTargetEnums.ROLE)
    public ServiceResult<RoleModel> add(@RequestBody @Validated RoleRequest createRequest) {
        RoleModel roleModel = roleService.add(createRequest);
        return ServiceResult.success(roleModel);
    }


    @ApiOperation(value = "更新角色，同时完成角色-权限绑定关系", notes = "")
    @PostMapping("modify")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "修改角色", target = OpTargetEnums.ROLE)
    public ServiceResult<RoleModel> modify(@RequestBody @Validated RoleRequest modifyRequest) {
        RoleModel roleModel = roleService.modify(modifyRequest);
        return ServiceResult.success(roleModel);
    }

    @ApiOperation(value = "查询所有角色", notes = "")
    @GetMapping("/all")
    public ServiceResult<List<RoleModel>> listAll() {
        List<RoleModel> roles = roleService.listAll();
        return ServiceResult.success(roles);
    }

}
