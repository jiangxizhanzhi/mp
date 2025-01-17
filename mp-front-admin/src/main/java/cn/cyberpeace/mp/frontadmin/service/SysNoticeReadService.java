 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysNoticeReadService.java.
 // File size 566.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeReadModel;

 /**
  * sys请注意读服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public interface SysNoticeReadService extends BaseModelService<SysNoticeReadModel, QueryParams> {

     /**
      * 根据用户id和通知id进行选择
      *
      * @param user     用户
      * @param noticeId 注意id
      * @return {@link SysNoticeReadModel }
      */
     SysNoticeReadModel selectByUserIdAndNoticeId(String user, String noticeId);

     /**
      * 增加读计数
      *
      * @param id id
      * @return int
      */
     int increaseReadCount(String id);

     /**
      * 计数被读取或被联合国推送
      *
      * @param user 用户
      * @return int
      */
     int countIsReadedOrIsUnPush(String user);
}
