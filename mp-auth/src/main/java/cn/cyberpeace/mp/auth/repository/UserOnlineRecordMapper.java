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
     * 判断在线的依据：
     * end_time > (now() - 5m) && status = 0
     *
     * @param deadline
     * @return
     */
    List<UserOnlineRecordModel> listCurrentOnlinedUser(@Param("deadline") LocalDateTime deadline);

}
