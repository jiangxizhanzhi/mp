 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserPermissionService.java.
 // File size 1320.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.target.TargetBo;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserPermissionModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.util.List;

 /**
  * 用户许可服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public interface UserPermissionService extends BaseModelService<UserPermissionModel, QueryParams> {

     /**
      * TYPE_ALL = 0 / null
      * TYPE_MENU = 1;
      * TYPE_API = 2;
      * TYPE_DATA = 3;
      *
      * @param userId
      * @return
      */
     List<TargetModel> listTargetsByUserId(String userId);

     /**
      * 按目标器id列出用户
      *
      * @param targetId 目标id
      * @return {@link List }<{@link UserModel }>
      */
     List<UserModel> listUsersByTargetId(String targetId);

     /**
      * 按用户id列出菜单
      *
      * @param userId 用户id
      * @return {@link List }<{@link TargetModel }>
      */
     List<TargetModel> listMenusByUserId(String userId);

     /**
      * 根据用户id获取树
      *
      * @param userId 用户id
      * @return {@link List }<{@link TargetBo }>
      */
     List<TargetBo> getTreeByUserId(String userId);

     /**
      * @param userId          用户id
      * @param targetIds       目标id
      * @param cleanOldTargets 清除旧目标
      * @return boolean
      */
     boolean bindUserTargets(String userId, List<String> targetIds, boolean cleanOldTargets);

     /**
      * @return {@link List }<{@link TargetModel }>
      */// 返回菜单对象集合
    List<TargetModel> listMenusByLoginUser();

     /**
      * @return {@link List }<{@link String }>
      */// 返回菜单对象编码集合
    List<String> listMenuIdentsByLoginUser();

     /**
      * 按用户id删除
      *
      * @param userId 用户id
      * @return boolean
      */
     boolean deleteByUserId(String userId);

     /**
      * 按目标器id删除
      *
      * @param targetId 目标id
      * @return boolean
      */
     boolean deleteByTargetId(String targetId);
}
