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


@Service
public class SysNoticeRoleServiceImpl extends BaseModelServiceImpl<SysNoticeRoleModel, SysNoticeRoleMapper, SysNoticeRoleFields, QueryParams>
    implements SysNoticeRoleService {

    @Autowired
    public SysNoticeRoleServiceImpl(SysNoticeRoleMapper sysNoticeRoleMapper) {
        setModelMapper(sysNoticeRoleMapper);
    }

    @Autowired
    private RoleMapper roleMapper;
    @Override
    public List<RoleModel> listByNoticeIds(List<String> noticeIds) {
        HumanCondition humanCondition = new HumanCondition();
        if (CollUtil.isNotEmpty(noticeIds)) {
            humanCondition.link(RoleModel.class, SysNoticeRoleModel.class)
                    .filterLink(RoleModel.class, RoleModel::getId, SysNoticeRoleModel.class, SysNoticeRoleModel::getRole)
                    .currentLink(SysNoticeRoleModel.class)
                    .filterIn(SysNoticeRoleModel::getNoticeId, noticeIds.toArray());
        }
        humanCondition.distinct();
        List<RoleModel> models = roleMapper.list(humanCondition);
        return models;
    }

    @Override
    public int deleteByNoticeIds(List<String> noticeIds) {
        HumanCondition condition = new HumanCondition();
        if (CollectionUtil.isNotEmpty(noticeIds)) {
            condition.filterIn(SysNoticeRoleModel::getNoticeId, noticeIds.toArray());
        }
        int rows = getModelMapper().filterDelete(condition);
        return rows;
    }

    @Override
    public int deleteByRole(String roleId) {
        HumanCondition condition = new HumanCondition()
                .filterEqual(SysNoticeRoleModel::getRole, roleId);
        int rows = getModelMapper().filterDelete(condition);
        return rows;
    }
}
