 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RoleController.java.
 // File size 2218.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.controller;

import cn.cyberpeace.mp.auth.bo.role.RolePermBo;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.request.role.RolePageRequest;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

 /**
  * 角色控制器
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Api(value = "角色管理接口(业务)", tags = "AUTH-角色管理接口(业务)")
@Slf4j
@RestController("roleFrontController")
@RequestMapping(ControllerConstant.BASE_API_PATH + "/roles")
public class RoleController {

     /**
      * 角色服务
      */
     @Autowired
    private RoleService roleService;

     /**
      * 烫角色发型
      *
      * @param id     id
      * @param fields 字段
      * @return {@link ObjServiceResult }
      */
     @ApiOperation("查询指定id角色（附带权限、用户数）")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @GetMapping("/perm/{id}")
    public ObjServiceResult getRolePerm(@PathVariable String id, @RequestParam(required = false) String[] fields) {
        RolePermBo rolePermBo = roleService.getRolePermDto(id);

        return ResultUtils.obj2ObjServiceResult(rolePermBo);
    }

     /**
      * 获取用户CNT角色
      *
      * @param pageRequest 页面请求
      * @return {@link ServiceResult }
      */
     @ApiOperation(value = "分页查询角色列表（附加权限信息，绑定的用户信息）", notes = "")
    @GetMapping("/page")
    public ServiceResult getRolesWithUserCnt(RolePageRequest pageRequest) {
        pageRequest.ensure();
        ListObjServiceResult result = roleService.getPageWithUserCnt(pageRequest);

        return result;
    }

}
