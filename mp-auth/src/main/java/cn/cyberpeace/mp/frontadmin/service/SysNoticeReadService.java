package cn.cyberpeace.mp.frontadmin.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeReadModel;

/**
 * @ClassName SysNoticeReadService
 * @Description
 * @Author shgang
 * @Date 2022/9/22
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface SysNoticeReadService extends BaseModelService<SysNoticeReadModel, QueryParams> {

    /**
     * selectByUserIdAndNoticeId
     *
     * @param user
     * @param noticeId
     * @return
     */
    SysNoticeReadModel selectByUserIdAndNoticeId(String user, String noticeId);

    /**
     * increaseReadCount
     *
     * @param id
     * @return int
     */
    int increaseReadCount(String id);

    /**
     * countIsReadedOrIsUnPush
     *
     * @param user
     * @return int
     */
    int countIsReadedOrIsUnPush(String user);
}
