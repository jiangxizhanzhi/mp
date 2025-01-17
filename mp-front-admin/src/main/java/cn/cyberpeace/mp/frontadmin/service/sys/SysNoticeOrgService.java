 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysNoticeOrgService.java.
 // File size 722.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service.sys;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeOrgModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

 /**
  * sys请注意org服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public interface SysNoticeOrgService extends BaseModelService<SysNoticeOrgModel, QueryParams> {

     /**
      * 按通知编号列出
      *
      * @param noticeIds 注意id
      * @return {@link List }<{@link OrgModel }>
      */
     List<OrgModel> listByNoticeIds(@Param("noticeIds") List<String> noticeIds);

     /**
      * 按通知id删除
      *
      * @param noticeIds 注意id
      * @return int
      */
     int deleteByNoticeIds(@Param("noticeIds") List<String> noticeIds);

     /**
      * 按组织删除
      *
      * @param orgId org id
      * @return int
      */
     int deleteByOrg(@Param("orgId") String orgId);
}
