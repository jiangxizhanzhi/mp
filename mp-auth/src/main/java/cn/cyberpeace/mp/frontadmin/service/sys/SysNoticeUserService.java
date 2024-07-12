 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysNoticeUserService.java.
 // File size 798.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service.sys;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeUserModel;

import java.util.List;

/**
 * @ClassName SysNoticeUserService
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/9/20 13:55
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface SysNoticeUserService extends BaseModelService<SysNoticeUserModel, QueryParams> {

    /**
     * deleteByNoticeIds
     *
     * @param noticeIds
     * @return int
     */
    int deleteByNoticeIds(List<String> noticeIds);

    /**
     * deleteByUser
     *
     * @param userId
     * @return int
     */
    int deleteByUser(String userId);

}
