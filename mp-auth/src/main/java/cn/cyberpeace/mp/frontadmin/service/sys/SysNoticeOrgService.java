 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysNoticeOrgService.java.
 // File size 1077.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service.sys;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeOrgModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName SysNoticeOrgService
 * @Description
 * @Author shgang
 * @Date 2022/9/22
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface SysNoticeOrgService extends BaseModelService<SysNoticeOrgModel, QueryParams> {

    /**
     * listByNoticeIds
     *
     * @param noticeIds
     * @return
     */
    List<OrgModel> listByNoticeIds(@Param("noticeIds") List<String> noticeIds);

    /**
     * deleteByNoticeIds
     *
     * @param noticeIds
     * @return int
     */
    int deleteByNoticeIds(@Param("noticeIds") List<String> noticeIds);

    /**
     * deleteByOrg
     *
     * @param orgId
     * @return int
     */
    int deleteByOrg(@Param("orgId") String orgId);
}
