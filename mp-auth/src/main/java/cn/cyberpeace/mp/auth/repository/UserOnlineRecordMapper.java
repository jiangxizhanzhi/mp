package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.dto.user.UserOnlineRecordSummaryDto;
import cn.cyberpeace.mp.auth.model.UserOnlineRecordModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Mapper
@Repository
@ModelClass(UserOnlineRecordModel.class)
public interface UserOnlineRecordMapper extends BaseModelMapper<UserOnlineRecordModel> {

    List<UserOnlineRecordSummaryDto> listRecordSummary(@Param("userId") String userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 判断在线的依据：
     * end_time > (now() - 5m) && status = 0
     *
     * @param deadline
     * @return
     */
    List<UserOnlineRecordModel> listCurrentOnlined(@Param("userId") String userId, @Param("deadline") LocalDateTime deadline);

    /**
     * 判断离线的依据:
     * end_time <= (now() - 5m) && status = 0
     *
     * @param deadline
     * @return
     */
    List<UserOnlineRecordModel> listCurrentOfflined(@Param("userId") String userId, @Param("deadline") LocalDateTime deadline);


    /**
     * 查询一定时间段内的在线用户（group by user)
     * 如果是查询当前在线用户，则筛选时间段为 now()-5m < period < now()
     *
     * @return
     */
    List<UserOnlineRecordModel> listOnlinedUserByPeriod(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);


    /**
     * 查询一定时间段内单个用户的在线记录（在线活动轨迹）
     * 如查询用户一个月内的活跃情况，now()-1M < period < now()
     *
     * @param
     * @return
     */
    List<UserOnlineRecordModel> listOnlineHistoryTrackByUser(@Param("userId") String userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);


}
