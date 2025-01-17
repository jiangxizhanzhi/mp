 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MenuExtraInfoService.java.
 // File size 1480.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service.sys.activmenu;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.frontadmin.model.MenuExtraInfoModel;
import cn.cyberpeace.mp.frontadmin.repository.MenuExtraInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

 /**
  * 菜单额外信息服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Service
@Slf4j
public class MenuExtraInfoService {

     /**
      *
      */
     @Autowired
    private MenuExtraInfoMapper menuExtraInfoMapper;

     /**
      * 保存
      *
      * @param menuExtraInfoModel 菜单额外信息模型
      * @return boolean
      */
     public boolean save(MenuExtraInfoModel menuExtraInfoModel) {
        menuExtraInfoMapper.create(menuExtraInfoModel);
        return true;
    }

     /**
      * 批量创建
      *
      * @param menuExtraInfoModels
      * @return boolean
      */
     public boolean bulkCreate(List<MenuExtraInfoModel> menuExtraInfoModels) {
        int result = menuExtraInfoMapper.bulkCreate(menuExtraInfoModels).getSuccessCount();
        log.info("added {} menu extra info rows", result);
        return true;
    }

     /**
      * @return {@link List }<{@link MenuExtraInfoModel }>
      */
     public List<MenuExtraInfoModel> listAll() {
        return menuExtraInfoMapper.list(new HumanCondition());
    }

     /**
      * @return boolean
      */
     public boolean deleteAll() {
        HumanCondition condition = new HumanCondition()
                .filterGreaterThan(MenuExtraInfoModel::getPk, MenuExtraInfoModel.PK_DELETE_ALL_START);
        int result = menuExtraInfoMapper.filterDelete(condition);
        return true;
    }

}
