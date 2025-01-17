 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysConfigService.java.
 // File size 857.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service.sys;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysConfigModel;

import java.util.List;

 /**
  * sys配置服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public interface SysConfigService extends BaseModelService<SysConfigModel, QueryParams> {

     /**
      * 列出所有
      *
      * @return {@link List }<{@link SysConfigModel }>
      */
     List<SysConfigModel> listAll();

     /**
      * 按键名获取
      *
      * @param keyName 键名
      * @return {@link SysConfigModel }
      */
     SysConfigModel getByKeyName(String keyName);

     /**
      * 按键名更新
      *
      * @param sysConfigModel sys配置模型
      * @return boolean
      */
     boolean updateByKeyName(SysConfigModel sysConfigModel);

     /**
      * 按密钥名批量更新
      *
      * @param configs 配置
      * @return boolean
      */
     boolean batchUpdateByKeyname(List<SysConfigModel> configs);

     /**
      * 是否需要审核 true（1）:需要 false（0）:不需要
      *
      * @return
      */
     boolean isNewUserNeedAudit();

     /**
      * 发布配置更新事件
      *
      * @param configs 配置
      */
     void publishConfigUpdateEvent(List<SysConfigModel> configs);

}
