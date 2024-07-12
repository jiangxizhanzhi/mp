 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserOnlineRecordService.java.
 // File size 2050.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.user.UserOnlineCntBo;
import cn.cyberpeace.mp.auth.request.user.UserOnlineRecordSummaryPageRequest;
import cn.cyberpeace.mp.auth.bo.user.UserOnlineReportBo;
import cn.cyberpeace.mp.auth.model.UserOnlineRecordModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import com.github.pagehelper.PageInfo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName UserOnlineRecordService
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface UserOnlineRecordService extends BaseModelService<UserOnlineRecordModel, QueryParams> {

    /**
     * recordSummaryPage
     *
     * @param req
     * @return
     */
    PageInfo<UserOnlineReportBo> recordSummaryPage(UserOnlineRecordSummaryPageRequest req);

    /**
     * 判断在线的依据：
     * end_time > (now() - 5m) && status = 0
     *
     * @param userIds
     * @return
     */
    List<UserOnlineRecordModel> listCurrentOnlined(List<String> userIds);

    /**
     * 判断离线的依据:
     * end_time <= (now() - 5m) && status = 0
     *
     * @return
     */
    List<UserOnlineRecordModel> listCurrentOfflined(List<String> userIds);


    /**
     * 当前离线用户
     *
     * @return
     */
    //List<UserOnlineRecordModel> listCurrentOnlinedUser();

    /**
     * 当前在线用户数
     * @return
     */
    //int cntCurrentOnlinedUser();

    /**
     * 根据时间段查找在线用户
     *
     * @return
     */
    //List<UserOnlineRecordModel> listOnlinedUserByPeriod();


    /**
     * 根据时间段查找在线用户数量
     *
     * @return
     */
    Integer getOnlinedUserCntByPeriod(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * onlineUserCnt
     *
     * @return
     */
    UserOnlineCntBo onlineUserCnt();

}
