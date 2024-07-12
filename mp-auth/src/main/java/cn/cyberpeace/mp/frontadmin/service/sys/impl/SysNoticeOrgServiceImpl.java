 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysNoticeOrgServiceImpl.java.
 // File size 2996.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
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
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName SysNoticeOrgServiceImpl
 * @Description
 * @Author shgang
 * @Date 2022/9/22
 * @Version 1.0.0
 * @see BaseModelServiceImpl
 * @see SysNoticeOrgService
 */
@Service
public class SysNoticeOrgServiceImpl extends BaseModelServiceImpl<SysNoticeOrgModel, SysNoticeOrgMapper, SysNoticeOrgFields, QueryParams>
    implements SysNoticeOrgService {

    @Autowired
    private SysNoticeOrgServiceImpl(SysNoticeOrgMapper sysNoticeOrgMapper) {
        setModelMapper(sysNoticeOrgMapper);
    }

    @Autowired
    private OrgMapper orgMapper;

    /**
     * listByNoticeIds
     *
     * @param noticeIds
     * @return
     */
    @Override
    public List<OrgModel> listByNoticeIds(List<String> noticeIds) {
        HumanCondition humanCondition = new HumanCondition();
        if (CollectionUtils.isNotEmpty(noticeIds)) {
            humanCondition.link(OrgModel.class, SysNoticeOrgModel.class)
                    .filterLink(OrgModel.class, OrgModel::getId, SysNoticeOrgModel.class, SysNoticeOrgModel::getOrg)
                    .currentLink(SysNoticeOrgModel.class)
                    .filterIn(SysNoticeOrgModel::getNoticeId, noticeIds.toArray());
        }
        humanCondition.distinct();
        List<OrgModel> orgModels = orgMapper.list(humanCondition);
        return orgModels;
    }

    /**
     * deleteByNoticeIds
     *
     * @param noticeIds
     * @return int
     */
    @Override
    public int deleteByNoticeIds(List<String> noticeIds) {
        HumanCondition condition = new HumanCondition();
        if (CollectionUtils.isNotEmpty(noticeIds)) {
            condition.filterIn(SysNoticeOrgModel::getNoticeId, noticeIds.toArray());
        }
        int rows = getModelMapper().filterDelete(condition);
        return rows;
    }

    /**
     * deleteByOrg
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
