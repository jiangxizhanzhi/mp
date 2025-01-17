 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysNoticeUserService.java.
 // File size 534.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service.sys;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeUserModel;

import java.util.List;

 /**
  * sys请注意用户服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public interface SysNoticeUserService extends BaseModelService<SysNoticeUserModel, QueryParams> {

     /**
      * 按通知id删除
      *
      * @param noticeIds 注意id
      * @return int
      */
     int deleteByNoticeIds(List<String> noticeIds);

     /**
      * 按用户删除
      *
      * @param userId 用户id
      * @return int
      */
     int deleteByUser(String userId);

}
