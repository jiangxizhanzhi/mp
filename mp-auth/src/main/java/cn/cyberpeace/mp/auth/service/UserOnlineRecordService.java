package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.user.UserOnlineCntDto;
import cn.cyberpeace.mp.auth.dto.user.UserOnlineRecordSummaryDto;
import cn.cyberpeace.mp.auth.dto.user.UserOnlineRecordSummaryPageRequest;
import cn.cyberpeace.mp.auth.model.UserOnlineRecordModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public interface UserOnlineRecordService extends BaseModelService<UserOnlineRecordModel, QueryParams> {

    PageInfo<UserOnlineRecordSummaryDto> recordSummaryPage(UserOnlineRecordSummaryPageRequest req);

    /**
     * 判断在线的依据：
     * end_time > (now() - 5m) && status = 0
     *
     * @return
     */
    List<UserOnlineRecordModel> listCurrentOnlined(String userId);

    /**
     * 判断离线的依据:
     * end_time <= (now() - 5m) && status = 0
     *
     * @return
     */
    List<UserOnlineRecordModel> listCurrentOfflined(String userId);


    /**
     * 在线用户
     *
     * @return
     */
    List<UserOnlineRecordModel> listCurrentOnlinedUser();


    UserOnlineCntDto onlineUserCnt();

}
