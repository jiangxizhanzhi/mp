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


@Service
public class SysNoticeOrgServiceImpl extends BaseModelServiceImpl<SysNoticeOrgModel, SysNoticeOrgMapper, SysNoticeOrgFields, QueryParams>
    implements SysNoticeOrgService {

    @Autowired
    private SysNoticeOrgServiceImpl(SysNoticeOrgMapper sysNoticeOrgMapper) {
        setModelMapper(sysNoticeOrgMapper);
    }

    @Autowired
    private OrgMapper orgMapper;

    @Override
    public List<OrgModel> listByNoticeIds(List<String> noticeIds) {
        HumanCondition humanCondition = new HumanCondition();
        if (CollUtil.isNotEmpty(noticeIds)) {
            humanCondition.link(OrgModel.class, SysNoticeOrgModel.class)
                    .filterLink(OrgModel.class, OrgModel::getId, SysNoticeOrgModel.class, SysNoticeOrgModel::getOrg)
                    .currentLink(SysNoticeOrgModel.class)
                    .filterIn(SysNoticeOrgModel::getNoticeId, noticeIds.toArray());
        }
        humanCondition.distinct();
        List<OrgModel> orgModels = orgMapper.list(humanCondition);
        return orgModels;
    }

    @Override
    public int deleteByNoticeIds(List<String> noticeIds) {
        HumanCondition condition = new HumanCondition();
        if (CollectionUtil.isNotEmpty(noticeIds)) {
            condition.filterIn(SysNoticeOrgModel::getNoticeId, noticeIds.toArray());
        }
        int rows = getModelMapper().filterDelete(condition);
        return rows;
    }

    @Override
    public int deleteByOrg(String orgId) {
        HumanCondition condition = new HumanCondition()
                .filterEqual(SysNoticeOrgModel::getOrg, orgId);
        int rows = getModelMapper().filterDelete(condition);
        return rows;
    }
}
