package cn.cyberpeace.mp.frontadmin.service;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeRoleModel;

import java.util.List;

/**
 * @Description
 * @Author shgang
 * @Date 2022/9/22
 */
public interface SysNoticeRoleService extends BaseModelService<SysNoticeRoleModel, QueryParams> {

    /**
     * listByNoticeIds
     *
     * @param noticeIds
     * @return
     */
    List<RoleModel> listByNoticeIds(List<String> noticeIds);

    /**
     * deleteByNoticeIds
     *
     * @param noticeIds
     * @return int
     */
    int deleteByNoticeIds(List<String> noticeIds);

    /**
     * deleteByRole
     *
     * @param roleId
     * @return int
     */
    int deleteByRole(String roleId);
}
