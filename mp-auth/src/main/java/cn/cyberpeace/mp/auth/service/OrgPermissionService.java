 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OrgPermissionService.java.
 // File size 822.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.OrgPermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.util.List;
import java.util.Set;

 /**
  * org许可服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public interface OrgPermissionService extends BaseModelService<OrgPermissionModel, QueryParams> {

     /**
      * 按org id列出目标
      *
      * @param orgId org id
      * @return {@link Set }<{@link TargetModel }>
      */
     Set<TargetModel> listTargetsByOrgId(String orgId);

     /**
      * 按组织id列出目标，包括所有父组织
      *
      * @param orgId org id
      * @return {@link Set }<{@link TargetModel }>
      */
     Set<TargetModel> listTargetsByOrgIdIncludeAllParentsOrg(String orgId);

     /**
      * 绑定org目标
      *
      * @param orgId           org id
      * @param targetIds       目标id
      * @param cleanOldTargets 清除旧目标
      * @return boolean
      */
     boolean bindOrgTargets(String orgId, List<String> targetIds, boolean cleanOldTargets);

     /**
      * @param orgId org id
      * @return boolean
      */
     boolean deleteByOrgId(String orgId);

     /**
      * @param targetId 目标id
      * @return boolean
      */
     boolean deleteByTargetId(String targetId);


}
