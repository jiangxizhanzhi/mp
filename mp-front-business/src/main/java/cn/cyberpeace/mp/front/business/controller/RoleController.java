package cn.cyberpeace.mp.front.business.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.role.RolePageRequest;
import cn.cyberpeace.mp.auth.dto.role.RolePermDto;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 角色管理接口(业务)
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "角色管理接口(业务)", tags = "AUTH-角色管理接口(业务)")
@Slf4j
@RestController("roleFrontController")
@RequestMapping(ControllerConstant.BASE_API_PATH + "/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @ApiOperation("查询指定id角色（附带权限、用户数）")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @GetMapping("/perm/{id}")
    public ObjServiceResult getRolePerm(@PathVariable String id, @RequestParam(required = false) String[] fields) {
        RolePermDto rolePermDto = roleService.getRolePermDto(id);

        return ResultUtils.obj2ObjServiceResult(rolePermDto);
    }

    @ApiOperation(value = "分页查询角色列表（附加权限信息，绑定的用户信息）", notes = "")
    @GetMapping("/page")
    public ServiceResult getRolesWithUserCnt(RolePageRequest pageRequest) {
        pageRequest.ensure();
        PageInfo page = roleService.getPageWithUserCnt(pageRequest);

        return ResultUtils.pageInfo2ObjServiceResult(page);
    }

}
