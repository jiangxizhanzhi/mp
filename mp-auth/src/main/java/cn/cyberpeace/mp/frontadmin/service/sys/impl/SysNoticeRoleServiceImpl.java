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
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName SysNoticeRoleServiceImpl
 * @Description
 * @Author shgang
 * @Date 2022/9/22
 * @Version 1.0.0
 * @see BaseModelServiceImpl
 * @see SysNoticeRoleService
 */
@Service
public class SysNoticeRoleServiceImpl extends BaseModelServiceImpl<SysNoticeRoleModel, SysNoticeRoleMapper, SysNoticeRoleFields, QueryParams>
    implements SysNoticeRoleService {

    /**
     * SysNoticeRoleServiceImpl
     *
     * @param sysNoticeRoleMapper
     */
    @Autowired
    public SysNoticeRoleServiceImpl(SysNoticeRoleMapper sysNoticeRoleMapper) {
        setModelMapper(sysNoticeRoleMapper);
    }

    @Autowired
    private RoleMapper roleMapper;

    /**
     * listByNoticeIds
     *
     * @param noticeIds
     * @return
     */
    @Override
    public List<RoleModel> listByNoticeIds(List<String> noticeIds) {
        HumanCondition humanCondition = new HumanCondition();
        if (CollectionUtils.isNotEmpty(noticeIds)) {
            humanCondition.link(RoleModel.class, SysNoticeRoleModel.class)
                    .filterLink(RoleModel.class, RoleModel::getId, SysNoticeRoleModel.class, SysNoticeRoleModel::getRole)
                    .currentLink(SysNoticeRoleModel.class)
                    .filterIn(SysNoticeRoleModel::getNoticeId, noticeIds.toArray());
        }
        humanCondition.distinct();
        List<RoleModel> models = roleMapper.list(humanCondition);
        return models;
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
            condition.filterIn(SysNoticeRoleModel::getNoticeId, noticeIds.toArray());
        }
        int rows = getModelMapper().filterDelete(condition);
        return rows;
    }

    /**
     * deleteByRole
     *
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
