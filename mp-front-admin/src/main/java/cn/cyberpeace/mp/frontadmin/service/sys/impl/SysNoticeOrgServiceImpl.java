 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysNoticeOrgServiceImpl.java.
 // File size 2633.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service.sys.impl;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.repository.OrgMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeOrgModel;
import cn.cyberpeace.mp.frontadmin.repository.SysNoticeOrgMapper;
import cn.cyberpeace.mp.frontadmin.service.fields.SysNoticeOrgFields;
import cn.cyberpeace.mp.frontadmin.service.sys.SysNoticeOrgService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

 /**
  * sys请注意org服务impl
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Service
public class SysNoticeOrgServiceImpl extends BaseModelServiceImpl<SysNoticeOrgModel, SysNoticeOrgMapper, SysNoticeOrgFields, QueryParams>
    implements SysNoticeOrgService {

     /**
      * sys请注意org服务impl
      *
      * @param sysNoticeOrgMapper 系统通知org映射器
      */
     @Autowired
    private SysNoticeOrgServiceImpl(SysNoticeOrgMapper sysNoticeOrgMapper) {
        setModelMapper(sysNoticeOrgMapper);
    }

     /**
      *
      */
     @Autowired
    private OrgMapper orgMapper;

     /**
      * 按通知编号列出
      *
      * @param noticeIds 注意id
      * @return {@link List }<{@link OrgModel }>
      */
     @Override
    public List<OrgModel> listByNoticeIds(List<String> noticeIds) {
        HumanCondition humanCondition = new HumanCondition();
        if (CollUtil.isNotEmpty(noticeIds)) {
            humanCondition = humanCondition.link(OrgModel.class, SysNoticeOrgModel.class)
                    .filterLink(OrgModel.class, OrgModel::getId, SysNoticeOrgModel.class, SysNoticeOrgModel::getOrg)
                    .currentLink(SysNoticeOrgModel.class)
                    .filterIn(SysNoticeOrgModel::getNoticeId, noticeIds.toArray());
        }
        humanCondition = humanCondition.distinct();
        List<OrgModel> orgModels = orgMapper.list(humanCondition);
        return orgModels;
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
            condition = condition.filterIn(SysNoticeOrgModel::getNoticeId, noticeIds.toArray());
        }
        int rows = getModelMapper().filterDelete(condition);
        return rows;
    }

     /**
      * 按组织删除
      *
      * @param orgId
      * @return int
      */
     @Override
    public int deleteByOrg(String orgId) {
        HumanCondition condition = new HumanCondition()
                .filterEqual(SysNoticeOrgModel::getOrg, orgId);
        int rows = getModelMapper().filterDelete(condition);
        return rows;
    }
}
