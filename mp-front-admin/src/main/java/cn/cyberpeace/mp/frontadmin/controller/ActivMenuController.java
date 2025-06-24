 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ActivMenuController.java.
 // File size 4554.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.controller;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.frontadmin.service.MenuSetService;
import cn.cyberpeace.mp.frontadmin.service.sys.activmenu.ActivMenuService;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

 /**
  * @author 二竹
  * @date 2025/01/17
  */
 @Api(value = "SYS-ADMIN", tags = "ADMIN")
@Slf4j
@RestController
@RequestMapping("/sys/admin")
public class ActivMenuController {

     /**
      *
      */
     @Autowired
    private ActivMenuService activMenuService;

     /**
      *
      */
     @Autowired
    private MenuSetService menuSetService;

     /**
      * @param file
      * @return {@link ServiceResult }
      */
     @PostMapping("/menu/reimport")
    @OperateLog(module = OpModuleEnums.SYSTEM, type = OpTypeEnums.UPDATE, title = "更新菜单列表", target = OpTargetEnums.MENU)
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult activMenu(MultipartFile file) {
         final boolean b = activMenuService.reImportMenus(file);
         return ResultCommon.success();
    }

     /**
      * @param actives
      * @return {@link ServiceResult }
      */
     @ApiOperation(value = "根据激活配置查询所有菜单")
    @GetMapping("/menu/tree")
    public ServiceResult getTree(@RequestParam(required = false) List<String> actives) {
        Set<String> visibleMenuIds = null;
        if (CollectionUtils.isEmpty(actives)) {
//            visibleMenuIds = menuSetService.getSystemAllVisibleMenuIds();
            return new ServiceResult(new ArrayList<>()).asResult();
        } else {
            visibleMenuIds = menuSetService.getVisibleMenuIdsBy(actives);
        }
        var tree = menuSetService.getSystemTreeMenusBy(visibleMenuIds);
        return new ServiceResult(tree).asResult();
    }

     /**
      * @param template
      * @return {@link ServiceResult }
      */
     @ApiOperation(value = "刷新默认菜单模板")
    @PostMapping("/menu/reset_default_menu_template")
    public ServiceResult refreshDefaultMenuTemplate(@RequestBody MenuSetService.MenuTemplate template) {
        var result = menuSetService.refreshTemplate(template.getMenuTree(), "1");
        return new ServiceResult(result).asResult();
    }

     /**
      * @param template
      * @return {@link ServiceResult }
      */
     @ApiOperation(value = "新增一套模板,可以同时关联到某个角色")
    @PostMapping("/menu/add_template")
    public ServiceResult addMenuTemplate(@RequestBody MenuSetService.MenuTemplate template) {
        MenuSetService.MenuTemplate result;
        if (StringUtils.isEmpty(template.getRoleId())) {
            result = menuSetService.refreshTemplate(template.getMenuTree(), "1");
        } else {
            result = menuSetService.addTemplate(template.getMenuTree());
            menuSetService.updateRoleMenuTemplate(template.getRoleId(), result.getTemplateId());
        }
        return new ServiceResult(result).asResult();
    }

     /**
      * @param file
      * @return {@link ServiceResult }
      */
     @ApiOperation(value = "导入并覆盖默认菜单模板")
    @PostMapping("/menu/import_template")
    public ServiceResult importMenuTemplate(MultipartFile file) {
        var result = menuSetService.importDefaultTemplate(file);
        return new ServiceResult(result).asResult();
    }

     /**
      * @param roleTemplate
      * @return {@link ServiceResult }
      */
     @ApiOperation(value = "修改角色和模板的关联关系")
    @PostMapping("/menu/update_role_template")
    public ServiceResult updateRoleMenuTemplate(@RequestBody MenuSetService.RoleTemplate roleTemplate) {
        menuSetService.updateRoleMenuTemplate(roleTemplate.getRoleId(), roleTemplate.getTemplateId());
        return new ServiceResult(true).asResult();
    }
}
