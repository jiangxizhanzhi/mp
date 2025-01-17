 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TargetController.java.
 // File size 2813.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.controller;

import cn.cyberpeace.mp.auth.bo.target.TargetBo;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.service.MenuConfigService;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

 /**
  * 目标控制器
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Api(value = "权限管理接口（业务）", tags = "AUTH-权限管理接口（业务）")
@Slf4j
@RestController("targetFrontController")
@RequestMapping(ControllerConstant.BASE_API_PATH + "/targets")
public class TargetController {

     /**
      * 目标服务
      */
     @Autowired
    @Qualifier("targetServiceImpl")
//    @Qualifier("templateTargetServiceImpl")
    private TargetService targetService;

     /**
      * 菜单配置服务
      */
     @Autowired
    private MenuConfigService menuConfigService;

     /**
      * 用户许可服务
      */
     @Autowired
    private UserPermissionService userPermissionService;

     /**
      * 让树
      *
      * @param targetId 目标id
      * @param type     类型
      * @return {@link ServiceResult }
      */
     @ApiOperation(value = "查询权限树", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "targetId", value = "权限id", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "type", value = "权限类型 1：菜单，2：API，3：数据权限 不传默认为1：菜单", paramType = "query", dataTypeClass = Integer.class)
    })
    @GetMapping("/tree")
    public ServiceResult getTree(@RequestParam(required = false) String targetId, @RequestParam(required = false) Integer type) {
        List<TargetBo> tree = targetService.getTree(targetId, type);
        return ServiceResult.builder().source(tree).build().asResult();
    }

     /**
      * 让树
      *
      * @return {@link ServiceResult }
      */
     @ApiOperation(value = "查询当前用户菜单")
    @GetMapping("/current_user/tree")
    public ServiceResult getTree() {
        List<String> menuIdents = userPermissionService.listMenuIdentsByLoginUser();
        var result = menuConfigService.getUserTreeMenuBy(UserUtils.getLoginUser().getId(), menuIdents);
        return ServiceResult.builder().source(result).build().asResult();
    }
}
