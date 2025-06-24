 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysNoticeRoleServiceImpl.java.
 // File size 2655.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service.sys.impl;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.repository.RoleMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeRoleModel;
import cn.cyberpeace.mp.frontadmin.repository.SysNoticeRoleMapper;
import cn.cyberpeace.mp.frontadmin.service.SysNoticeRoleService;
import cn.cyberpeace.mp.frontadmin.service.fields.SysNoticeRoleFields;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

 /**
  * sys请注意角色服务impl
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Service
public class SysNoticeRoleServiceImpl extends BaseModelServiceImpl<SysNoticeRoleModel, SysNoticeRoleMapper, SysNoticeRoleFields, QueryParams>
    implements SysNoticeRoleService {

     /**
      * sys请注意角色服务impl
      *
      * @param sysNoticeRoleMapper
      */
     @Autowired
    public SysNoticeRoleServiceImpl(SysNoticeRoleMapper sysNoticeRoleMapper) {
        setModelMapper(sysNoticeRoleMapper);
    }

     /**
      * 角色映射器
      */
     @Autowired
    private RoleMapper roleMapper;

     /**
      * 按通知编号列出
      *
      * @param noticeIds 注意id
      * @return {@link List }<{@link RoleModel }>
      */
     @Override
    public List<RoleModel> listByNoticeIds(List<String> noticeIds) {
        HumanCondition humanCondition = new HumanCondition();
        if (CollUtil.isNotEmpty(noticeIds)) {
            humanCondition = humanCondition.link(RoleModel.class, SysNoticeRoleModel.class)
                    .filterLink(RoleModel.class, RoleModel::getId, SysNoticeRoleModel.class, SysNoticeRoleModel::getRole)
                    .currentLink(SysNoticeRoleModel.class)
                    .filterIn(SysNoticeRoleModel::getNoticeId, noticeIds.toArray());
        }
        humanCondition = humanCondition.distinct();
        List<RoleModel> models = roleMapper.list(humanCondition);
        return models;
    }

     /**
      * 按通知id删除
      *
      * @param noticeIds 注意id
      * @return int
      */
     @Override
    public int deleteByNoticeIds(List<String> noticeIds) {
        HumanCondition condition = new HumanCondition();
        if (CollectionUtil.isNotEmpty(noticeIds)) {
            condition = condition.filterIn(SysNoticeRoleModel::getNoticeId, noticeIds.toArray());
        }
        int rows = getModelMapper().filterDelete(condition);
        return rows;
    }

     /**
      * @param roleId
      * @return int
      */
     @Override
    public int deleteByRole(String roleId) {
        HumanCondition condition = new HumanCondition()
                .filterEqual(SysNoticeRoleModel::getRole, roleId);
        int rows = getModelMapper().filterDelete(condition);
        return rows;
    }
}
