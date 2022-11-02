package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.bo.user.UserOnlineRecordSummaryBo;
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

    List<UserOnlineRecordSummaryBo> listRecordSummary(@Param("userIds") List<String> userIds,
                                                      @Param("priorityUserIds") List<String> priorityUserIds,
                                                      @Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime);




    /**
     * 查询一定时间段内的在线用户（group by user)
     * 如果是查询当前在线用户，则筛选时间段为 now()-5m < period < now()
     *
     * @return
     */
    //List<UserOnlineRecordModel> listOnlinedUserByPeriod(int minutes);


    /**
     * 查询一定时间段内的在线用户数（group by user)
     * 如果是查询当前在线用户，则筛选时间段为 now()-5m < period < now()
     *
     * @return
     */
    //int cntOnlinedUser(int minutes);
}
