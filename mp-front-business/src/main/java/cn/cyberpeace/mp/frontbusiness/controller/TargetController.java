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
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.impl.ThirdSsoService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

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

     @Resource
     private UserService userService ;

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

     @ApiOperation(value = "查询当前用户菜单")
     @GetMapping("/current_user/menus")
     public ServiceResult<Menus> getMenus() {
         String userName = UserUtils.getLoginUser().getUsername();
         var userModel = userService.getByUserName(userName);
         String menuContent = userModel.getMenusContent();

         Set<String> parentIds = new HashSet<>();
         Set<String> childIds = new HashSet<>();
         if (StringUtils.isNotEmpty(menuContent)) {
             List<ThirdSsoService.ThirdRole> roles = JsonCommon.parse(menuContent, new TypeReference<List<ThirdSsoService.ThirdRole>>() {
             });
             for (var role : roles) {
                 Menus oneMenus = handleOneRole(role);
                 parentIds.addAll(oneMenus.getParentIds());
                 childIds.addAll(oneMenus.getChildIds());
             }
         }

         Menus result = new Menus();
         result.getParentIds().addAll(parentIds);
         result.getChildIds().addAll(childIds);
         return new ServiceResult<>(result).asResult();
     }

     private Menus handleOneRole(ThirdSsoService.ThirdRole role){
         Set<String> parentIds = new HashSet<>();
         Set<String> childIds = new HashSet<>();
         List<ThirdSsoService.ThirdRoleMenu> menus = role.getMenu();
         if (CollectionUtils.isNotEmpty(menus)) {
             Queue<ThirdSsoService.ThirdRoleMenu> menuQueue = new LinkedList<>();
             for (ThirdSsoService.ThirdRoleMenu m : menus) {
                 parentIds.add(m.getName());
                 menuQueue.add(m);
             }
             while (!menuQueue.isEmpty()) {
                 ThirdSsoService.ThirdRoleMenu m = menuQueue.poll();
                 if (!parentIds.contains(m.getName())) {
                     childIds.add(m.getName());
                 }
                 if (m.getChild() != null) {
                     m.getChild().forEach(c -> {
                         menuQueue.add(c);
                     });
                 }
             }
         }
         Menus result = new Menus();
         result.getParentIds().addAll(parentIds);
         result.getChildIds().addAll(childIds);
         return result;
     }

     @Getter
     @Setter
     public static class Menus {
         // 顶部栏可见的菜单id
         private List<String> parentIds = new ArrayList<>();
         // 侧边栏可见的菜单id
         private List<String> childIds = new ArrayList<>();
     }
 }
