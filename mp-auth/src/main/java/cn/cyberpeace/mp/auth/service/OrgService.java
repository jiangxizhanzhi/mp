 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OrgService.java.
 // File size 2466.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.org.OrgBO;
import cn.cyberpeace.mp.auth.bo.org.OrgDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;

import java.util.List;

 /**
  * org服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public interface OrgService extends BaseModelService<OrgModel, QueryParams> {

     /**
      * 得到org bo
      *
      * @param id id
      * @return {@link OrgBO }
      */
     OrgBO getOrgBO(String id);

     /**
      * 获取组织模型
      *
      * @param id id
      * @return {@link OrgModel }
      */
     OrgModel getOrgModel(String id);

     /**
      * 列表父母
      *
      * @return {@link List }<{@link OrgBO }>
      */
     List<OrgBO> listParents();

     /**
      * 家长名单
      *
      * @param parentId 父id
      * @return {@link List }<{@link OrgBO }>
      */
     List<OrgBO> listByParent(String parentId);

     /**
      * 列出所有
      *
      * @param tenantId 承租者id
      * @return {@link List }<{@link OrgBO }>
      */
     List<OrgBO> listAll(String tenantId);

     /**
      * 让树
      *
      * @param orgId org id
      * @return {@link List }<{@link OrgDto }>
      */
     List<OrgDto> getTree(String orgId);

     /**
      * @param orgId org id
      * @return {@link OrgDto }
      */// 列出组织的层级关系
    OrgDto getHierarchicalOrgsByOrgId(String orgId);

     /**
      * @param orgId org id
      * @return {@link String }
      */// 列出组织的路径
    String calculatePathByOrgId(String orgId);

     /**
      * 按路径获取
      *
      * @param path 路径
      * @return {@link OrgBO }
      */// 根据路径查出组织
    OrgBO getByPath(String path);

     /**
      * @param id id
      * @return boolean
      */
     boolean exists(String id);

     /**
      * 列出所有的子组织，忽略层级限制
      * 注意！不包括传入的org本身
      *
      * @param orgId org id
      * @return {@link List }<{@link OrgDto }>
      */
     List<OrgDto> listAllChildIgnoreLevelExcludeSelf(String orgId);

     /**
      * 列出所有的父组织，忽略层级限制
      * 注意！不包括传入的org本身
      *
      * @param orgId org id
      * @return {@link List }<{@link OrgDto }>
      */
     List<OrgDto> listAllParentIgnoreLevelExcludeSelf(String orgId);

     /**
      * 更新
      *
      * @param obj    obj
      * @param fields 字段
      * @return {@link ObjServiceResult }<{@link OrgModel }>
      */
     @Override
    default ObjServiceResult<OrgModel> update(OrgModel obj, String[] fields) {
        // todo 如果是更新组织名称，那要更新关联的组织orgpath
        return BaseModelService.super.update(obj, fields);
    }

     /**
      * 获取组织完整path
      *
      * @param orgModel
      * @return
      */
     String getPath(OrgModel orgModel);

     /**
      * 获取组织完整path
      *
      * @param orgModel
      * @param orgModelList 对应租户下的所有部门列表
      * @return
      */
     String getPath(OrgModel orgModel, List<OrgModel> orgModelList);

     /**
      * Org model2 Org bo
      *
      * @param orgModel 组织模型
      * @return {@link OrgBO }
      */
     OrgBO orgModel2OrgBO(OrgModel orgModel);

     /**
      * 批量获取组织完整path
      *
      * @param orgModelList
      * @return
      */
     String getBatchPath(List<OrgModel> orgModelList);

     /**
      * 批量获取组织路径list
      *
      * @param orgModelList
      * @return
      */
     List<List<String>> getBathPathToList(List<OrgModel> orgModelList);

}
