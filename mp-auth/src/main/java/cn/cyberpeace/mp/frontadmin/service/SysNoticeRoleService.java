 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysNoticeRoleService.java.
 // File size 982.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeRoleModel;

import java.util.List;

/**
 * @ClassName SysNoticeRoleService
 * @Description
 * @Author shgang
 * @Date 2022/9/22
 * @Version 1.0.0
 * @see BaseModelService
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
