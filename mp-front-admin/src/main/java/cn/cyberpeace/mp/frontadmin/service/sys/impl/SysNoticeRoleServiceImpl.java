package cn.cyberpeace.mp.frontadmin.service.sys.impl;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeRoleModel;
import cn.cyberpeace.mp.frontadmin.repository.SysNoticeRoleMapper;
import cn.cyberpeace.mp.frontadmin.service.SysNoticeRoleService;
import cn.cyberpeace.mp.frontadmin.service.fields.SysNoticeRoleFields;
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
public class SysNoticeRoleServiceImpl extends BaseModelServiceImpl<SysNoticeRoleModel, SysNoticeRoleMapper, SysNoticeRoleFields, QueryParams>
    implements SysNoticeRoleService {

    @Autowired
    public SysNoticeRoleServiceImpl(SysNoticeRoleMapper sysNoticeRoleMapper) {
        setModelMapper(sysNoticeRoleMapper);
    }

    @Override
    public List<RoleModel> listByNoticeIds(List<String> noticeIds) {
        List<RoleModel> models = getModelMapper().listByNoticeIds(noticeIds);
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
