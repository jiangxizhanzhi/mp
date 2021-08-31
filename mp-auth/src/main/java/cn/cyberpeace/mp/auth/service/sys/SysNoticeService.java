package cn.cyberpeace.mp.auth.service.sys;

import cn.cyberpeace.mp.auth.dto.notice.SysNoticeDto;
import cn.cyberpeace.mp.auth.dto.notice.SysNoticePageRequest;
import cn.cyberpeace.mp.auth.dto.notice.SysNoticeRequest;
import cn.cyberpeace.mp.auth.model.SysNoticeModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import com.github.pagehelper.PageInfo;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public interface SysNoticeService extends BaseModelService<SysNoticeModel, QueryParams> {


    SysNoticeModel add(SysNoticeRequest sysNoticeRequest);

    SysNoticeModel modify(SysNoticeRequest sysNoticeRequest);

    /**
     * 分页查询所有公告
     * @param pageRequest
     * @return
     */
    PageInfo page(SysNoticePageRequest pageRequest);

    /**
     * 分页查询当前用户有权看到的公告
     * @param pageRequest
     * @return
     */
    PageInfo currentUserPage(SysNoticePageRequest pageRequest);

    SysNoticeDto noticeModel2NoticeDto(SysNoticeModel noticeModel);
}
