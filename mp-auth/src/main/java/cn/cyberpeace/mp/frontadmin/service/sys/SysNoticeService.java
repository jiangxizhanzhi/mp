package cn.cyberpeace.mp.frontadmin.service.sys;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.frontadmin.dto.notice.SysNoticeDto;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeModel;
import cn.cyberpeace.mp.frontadmin.request.notice.DelayNoticeRequest;
import cn.cyberpeace.mp.frontadmin.request.notice.SysNoticePageRequest;
import cn.cyberpeace.mp.frontadmin.request.notice.SysNoticeRequest;

import java.util.List;

/**
 * @ClassName SysNoticeService
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface SysNoticeService extends BaseModelService<SysNoticeModel, QueryParams> {


    /**
     * unpush
     *
     * @param noticeIds
     */
    void unpush(List<String> noticeIds);

    /**
     * read
     *
     * @param noticeId
     */
    void read(String noticeId);

    /**
     * top
     *
     * @param noticeId
     * @param type
     */
    void top(String noticeId, Integer type);

    /**
     * add
     *
     * @param sysNoticeRequest
     * @return
     */
    SysNoticeModel add(SysNoticeRequest sysNoticeRequest);

    /**
     * modify
     *
     * @param id
     * @param sysNoticeRequest
     * @return
     */
    SysNoticeModel modify(String id, SysNoticeRequest sysNoticeRequest);

    /**
     * 分页查询所有公告
     * @param pageRequest
     * @return
     */
    ServiceResult page(SysNoticePageRequest pageRequest);

    /**
     * 分页查询当前用户有权看到的公告消息列表
     *
     * @param
     * @return
     */
    ListObjServiceResult getCurrentUserNoticeMessagesPage(SysNoticePageRequest pageRequest);

    /**
     * getCurrentUserNoticeMessagesUnreadCount
     *
     * @return int
     */
    int getCurrentUserNoticeMessagesUnreadCount();

    /**
     * noticeModel2NoticeDto
     *
     * @param noticeModel
     * @param checkCurrentUserIsRead
     * @return
     */
    SysNoticeDto noticeModel2NoticeDto(SysNoticeModel noticeModel, boolean checkCurrentUserIsRead);

    /**
     * unReadCount
     *
     * @return int
     */
    int unReadCount();

    /**
     * 延期提醒
     * @param delayNoticeRequest
     * @return
     */
    Boolean delayNotice(DelayNoticeRequest delayNoticeRequest);
}
