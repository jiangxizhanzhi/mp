 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserHeartbeatService.java.
 // File size 810.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.user.UserOlReportCntBo;
import cn.cyberpeace.mp.auth.model.UserHeartbeatModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

 /**
  * 用户心跳服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public interface UserHeartbeatService extends BaseModelService<UserHeartbeatModel, QueryParams> {

     /**
      * 心跳
      */
     void heartbeat();

     /**
      * 获取心跳图
      *
      * @return {@link ConcurrentHashMap }<{@link UserModel }, {@link CopyOnWriteArrayList }<{@link LocalDateTime }>>
      */
     ConcurrentHashMap<UserModel, CopyOnWriteArrayList<LocalDateTime>> getHeartbeatMap();

     /**
      * 明确heartbeatmap
      */
     void clearHeartbeatmap();

}
