 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysNoticeService.java.
 // File size 1580.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service.sys;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.frontadmin.dto.notice.SysNoticeDto;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeModel;
import cn.cyberpeace.mp.frontadmin.request.notice.SysNoticePageRequest;
import cn.cyberpeace.mp.frontadmin.request.notice.SysNoticeRequest;

import java.util.List;

 /**
  * sys请注意服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public interface SysNoticeService extends BaseModelService<SysNoticeModel, QueryParams> {

     /**
      * unpush
      *
      * @param noticeIds 注意id
      */
     void unpush(List<String> noticeIds);

     /**
      * 读
      *
      * @param noticeId 注意id
      */
     void read(String noticeId);

     /**
      * 前
      *
      * @param noticeId 注意id
      * @param type     类型
      */
     void top(String noticeId, Integer type);

     /**
      * 添加
      *
      * @param sysNoticeRequest 系统通知请求
      * @return {@link SysNoticeModel }
      */
     SysNoticeModel add(SysNoticeRequest sysNoticeRequest);

     /**
      * 修改
      *
      * @param id               id
      * @param sysNoticeRequest 系统通知请求
      * @return {@link SysNoticeModel }
      */
     SysNoticeModel modify(String id, SysNoticeRequest sysNoticeRequest);

     /**
      * 分页查询所有公告
      *
      * @param pageRequest
      * @return
      */
     ServiceResult page(SysNoticePageRequest pageRequest);

     /**
      * 分页查询当前用户有权看到的公告消息列表
      *
      * @param pageRequest 页面请求
      * @return
      */
     ListObjServiceResult getCurrentUserNoticeMessagesPage(SysNoticePageRequest pageRequest);

     /**
      * 获取当前用户通知消息未读计数
      *
      * @return int
      */
     int getCurrentUserNoticeMessagesUnreadCount();

     /**
      * 通知model2通知dto
      *
      * @param noticeModel            注意模型
      * @param checkCurrentUserIsRead 查看当前用户是否被读取
      * @return {@link SysNoticeDto }
      */
     SysNoticeDto noticeModel2NoticeDto(SysNoticeModel noticeModel, boolean checkCurrentUserIsRead);

     /**
      * 未读计数
      *
      * @return int
      */
     int unReadCount();
}
