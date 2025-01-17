 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MenuReImportSupportService.java.
 // File size 5101.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service.sys.activmenu;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.MenuTemplateModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.repository.MenuTemplateMapper;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.frontadmin.model.MenuExtraInfoModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

 /**
  * 菜单再保险进口支持服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Service
@Slf4j
public class MenuReImportSupportService {

     /**
      * target服务
      */
     @Autowired
    @Qualifier("targetServiceImpl")
    private TargetService targetService;

     /**
      * 菜单额外信息服务
      */
     @Autowired
    private MenuExtraInfoService menuExtraInfoService;

     /**
      * templatetarget服务
      */
     @Autowired
    @Qualifier("templateTargetServiceImpl")
    private TargetService templateTargetService;

     /**
      *
      */
     @Autowired
    private MenuTemplateMapper menuTemplateMapper;

     /**
      * 场景：重新导入menu.json
      * 清除并且重建menus, menu_extra_infos
      *
      * @param targets
      * @param menuTemplateModels  菜单模板模型
      * @param menuExtraInfoModels
      * @return
      */
     @Transactional(rollbackFor = Exception.class)
    public boolean rebuildAllMenusAndExtraInfos(List<TargetModel> targets, List<MenuTemplateModel> menuTemplateModels, List<MenuExtraInfoModel> menuExtraInfoModels) {
        // step1. clean menus, keymenus, editionmenus
        boolean cleanSuccess = cleanAllMenusAndExtraInfos();
        boolean reImportSuccess = false;
        if (cleanSuccess) {
            // step2. rebuild menus, menuExtraInfos
            reImportSuccess = reInsertAllMenusAndExtraInfos(targets, menuTemplateModels, menuExtraInfoModels);
        }
        return reImportSuccess;
    }

     /**
      * 场景：重新激活
      * 清除并且重建menus
      *
      * @param targets
      * @return
      */
     @Transactional(rollbackFor = Exception.class)
    public boolean rebuildAllMenus(List<TargetModel> targets) {
        // step1. clean menus, keymenus, editionmenus
        boolean cleanSuccess = cleanAllMenus();
        boolean reImportSuccess = false;
        if (cleanSuccess) {
            // step2. rebuild menus, menuExtraInfos
            reImportSuccess = reInsertAllMenus(targets);
        }
        return reImportSuccess;
    }

     /**
      * 该方法会清除所有菜单(target)
      *
      * @return
      */
     private boolean cleanAllMenus() {
        boolean re1 = targetService.deleteAll();
        if (re1) {
            return true;
        }
        log.error("clean all menus failed!");
        throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
    }

     /**
      * 该方法会重新插入所有菜单(target)
      *
      * @param targets
      * @return
      */
     private boolean reInsertAllMenus(List<TargetModel> targets) {
        ServiceResult<Integer> result = targetService.bulkCreate(targets);
        if (Objects.nonNull(result.getSource()) && result.getSource() > 0) {
            return true;
        }
        log.error("rebuild all menus failed!");
        throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
    }

     /**
      * 该方法会清除所有菜单(target)，菜单附属信息(menuExtraInfos)
      *
      * @return
      */
     private boolean cleanAllMenusAndExtraInfos() {
        boolean re1 = targetService.deleteAll();
        boolean re2 = menuExtraInfoService.deleteAll();
        boolean re3 = templateTargetService.deleteAll();

        if (re1 && re2 && re3) {
            return true;
        }
        log.error("clean all menus and menus extra infos failed!");
        throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
    }

     /**
      * 该方法会重新插入所有菜单(target)，菜单附属信息(menuExtraInfos)
      *
      * @param targets
      * @param menuTemplateModels  菜单模板模型
      * @param menuExtraInfoModels 菜单额外信息模型
      * @return
      */
     private boolean reInsertAllMenusAndExtraInfos(List<TargetModel> targets, List<MenuTemplateModel> menuTemplateModels, List<MenuExtraInfoModel> menuExtraInfoModels) {
        ServiceResult<Integer> result = targetService.bulkCreate(targets);
        menuTemplateMapper.bulkCreate(menuTemplateModels);
        log.info("inserted {} target rows.", result.getSource());
        boolean re1 = menuExtraInfoService.bulkCreate(menuExtraInfoModels);
        if (Objects.nonNull(result.getSource()) && result.getSource() > 0 && re1) {
            log.info("rebuild all menus and menu extra info success!");
            return true;
        }
        log.error("rebuild all menus and menu extra info failed!");
        throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
    }

}
