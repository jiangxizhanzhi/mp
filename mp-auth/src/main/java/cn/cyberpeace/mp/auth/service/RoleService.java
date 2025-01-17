 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RoleService.java.
 // File size 1337.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.role.RoleBo;
import cn.cyberpeace.mp.auth.bo.role.RolePermBo;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.request.role.RolePageRequest;
import cn.cyberpeace.mp.auth.request.role.RoleRequest;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;

import java.util.List;

 /**
  * 角色服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public interface RoleService extends BaseModelService<RoleModel, QueryParams> {

     /**
      * 按租户id列出
      *
      * @param tenantId 承租者id
      * @return {@link List }<{@link RoleModel }>
      */
     List<RoleModel> listByTenantId(String tenantId);

     /**
      * 列出所有
      *
      * @param rolePageRequest 角色页面请求
      * @return {@link List }<{@link RoleModel }>
      */
     List<RoleModel> listAll(RolePageRequest rolePageRequest);

     /**
      * 按组织编号列出
      *
      * @param orgId org id
      * @return {@link List }<{@link RoleModel }>
      */
     List<RoleModel> listByOrgId(String orgId);

     /**
      * 获得认证
      *
      * @param identifier 标识符
      * @return {@link RoleModel }
      */
     RoleModel getByIdent(String identifier);

     /**
      * @param name
      * @return {@link RoleModel }
      */
     RoleModel getByName(String name);

     /**
      * @param names 名字
      * @return {@link List }<{@link RoleModel }>
      */
     List<RoleModel> listByNames(List<String> names);

     /**
      * 存在
      *
      * @param id id
      * @return boolean
      */
     boolean exists(String id);

     /**
      * 获取用户为CNT页面
      *
      * @param rolePageRequest 角色页面请求
      * @return {@link ListObjServiceResult }
      */
     ListObjServiceResult getPageWithUserCnt(RolePageRequest rolePageRequest);

     /**
      * 获取角色dto
      *
      * @param id id
      * @return {@link RoleBo }
      */
     RoleBo getRoleDto(String id);

     /**
      * 获得角色烫发
      *
      * @param id id
      * @return {@link RolePermBo }
      */
     RolePermBo getRolePermDto(String id);

     /**
      * 添加
      *
      * @param createRequest 创建请求
      * @return {@link RoleModel }
      */
     RoleModel add(RoleRequest createRequest);

     /**
      * 修改
      *
      * @param modifyRequest
      * @return {@link RoleModel }
      */
     RoleModel modify(RoleRequest modifyRequest);

     /**
      * @return {@link List }<{@link String }>
      */
     List<String> getSystemUnVisibleRoleIds();
}
