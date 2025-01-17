 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RolePermissionModelService.java.
 // File size 873.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.target.TargetRoleBo;
import cn.cyberpeace.mp.auth.model.RolePermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.util.List;

 /**
  * 角色许可模型服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public interface RolePermissionModelService extends BaseModelService<RolePermissionModel, QueryParams> {

     /**
      * 按角色id列出目标
      *
      * @param roleIds 角色id
      * @return {@link List }<{@link TargetModel }>
      */
     List<TargetModel> listTargetsByRoleIds(List<String> roleIds);

     /**
      * 按角色id列出目标角色dtos
      *
      * @param roleIds 角色id
      * @return {@link List }<{@link TargetRoleBo }>
      */
     List<TargetRoleBo> listTargetRoleDtosByRoleIds(List<String> roleIds);

     /**
      * 绑定角色目标
      *
      * @param roleId          角色id
      * @param targetIds       目标id
      * @param cleanOldTargets 清除旧目标
      * @return boolean
      */
     boolean bindRoleTargets(String roleId, List<String> targetIds, boolean cleanOldTargets);

     /**
      * 按角色id删除
      *
      * @param roleId 角色id
      * @return boolean
      */
     boolean deleteByRoleId(String roleId);

     /**
      * @param targetId 目标id
      * @return boolean
      */
     boolean deleteByTargetId(String targetId);

}
