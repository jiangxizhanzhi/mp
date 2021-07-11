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


public interface SysNoticeService extends BaseModelService<SysNoticeModel, QueryParams> {


    void unpush(List<String> noticeIds);

    void read(String noticeId);

    void top(String noticeId, Integer type);

    SysNoticeModel add(SysNoticeRequest sysNoticeRequest);

    SysNoticeModel modify(String id, SysNoticeRequest sysNoticeRequest);

    
    ServiceResult page(SysNoticePageRequest pageRequest);

    
    ListObjServiceResult getCurrentUserNoticeMessagesPage(SysNoticePageRequest pageRequest);

    int getCurrentUserNoticeMessagesUnreadCount();

    SysNoticeDto noticeModel2NoticeDto(SysNoticeModel noticeModel, boolean checkCurrentUserIsRead);

    int unReadCount();
}
