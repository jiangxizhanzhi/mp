package cn.cyberpeace.mp.frontadmin.service.sys;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.dto.notice.SysNoticeDto;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeModel;
import cn.cyberpeace.mp.frontadmin.request.notice.SysNoticePageRequest;
import cn.cyberpeace.mp.frontadmin.request.notice.SysNoticeRequest;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public interface SysNoticeService extends BaseModelService<SysNoticeModel, QueryParams> {


    void unpush(List<String> noticeIds);

    void read(String noticeId);

    void top(String noticeId, Integer type);

    SysNoticeModel add(SysNoticeRequest sysNoticeRequest);

    SysNoticeModel modify(String id, SysNoticeRequest sysNoticeRequest);

    /**
     * 分页查询所有公告
     * @param pageRequest
     * @return
     */
    PageInfo page(SysNoticePageRequest pageRequest);

    /**
     * 分页查询当前用户有权看到的公告消息列表
     *
     * @param pageRequest
     * @return
     */
    PageInfo getCurrentUserNoticeMessagesPage(SysNoticePageRequest pageRequest);

    int getCurrentUserNoticeMessagesUnreadCount();

    SysNoticeDto noticeModel2NoticeDto(SysNoticeModel noticeModel, boolean checkCurrentUserIsRead);

    int unReadCount();
}
