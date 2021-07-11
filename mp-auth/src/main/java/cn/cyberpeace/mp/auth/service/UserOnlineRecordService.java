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


public interface UserOnlineRecordService extends BaseModelService<UserOnlineRecordModel, QueryParams> {

    PageInfo<UserOnlineReportBo> recordSummaryPage(UserOnlineRecordSummaryPageRequest req);

    
    List<UserOnlineRecordModel> listCurrentOnlined(List<String> userIds);

    
    List<UserOnlineRecordModel> listCurrentOfflined(List<String> userIds);


    
    

    
    

    
    


    
    Integer getOnlinedUserCntByPeriod(LocalDateTime startTime, LocalDateTime endTime);

    UserOnlineCntBo onlineUserCnt();

}
