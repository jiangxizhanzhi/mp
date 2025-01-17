 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysNoticeRoleService.java.
 // File size 624.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeRoleModel;

import java.util.List;

 /**
  * sys请注意角色服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public interface SysNoticeRoleService extends BaseModelService<SysNoticeRoleModel, QueryParams> {

     /**
      * 按通知编号列出
      *
      * @param noticeIds 注意id
      * @return {@link List }<{@link RoleModel }>
      */
     List<RoleModel> listByNoticeIds(List<String> noticeIds);

     /**
      * 按通知id删除
      *
      * @param noticeIds 注意id
      * @return int
      */
     int deleteByNoticeIds(List<String> noticeIds);

     /**
      * 按角色删除
      *
      * @param roleId 角色id
      * @return int
      */
     int deleteByRole(String roleId);
}
