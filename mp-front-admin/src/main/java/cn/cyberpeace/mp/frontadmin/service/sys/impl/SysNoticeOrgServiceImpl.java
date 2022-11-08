package cn.cyberpeace.mp.frontadmin.service.sys.impl;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeOrgModel;
import cn.cyberpeace.mp.frontadmin.repository.SysNoticeOrgMapper;
import cn.cyberpeace.mp.frontadmin.service.fields.SysNoticeOrgFields;
import cn.cyberpeace.mp.frontadmin.service.sys.SysNoticeOrgService;
import cn.hutool.core.collection.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author shgang
 * @Date 2022/9/22
 */
@Service
public class SysNoticeOrgServiceImpl extends BaseModelServiceImpl<SysNoticeOrgModel, SysNoticeOrgMapper, SysNoticeOrgFields, QueryParams>
    implements SysNoticeOrgService {

    @Autowired
    private SysNoticeOrgServiceImpl(SysNoticeOrgMapper sysNoticeOrgMapper) {
        setModelMapper(sysNoticeOrgMapper);
    }

    @Override
    public List<OrgModel> listByNoticeIds(List<String> noticeIds) {
        return getModelMapper().listByNoticeIds(noticeIds);
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
