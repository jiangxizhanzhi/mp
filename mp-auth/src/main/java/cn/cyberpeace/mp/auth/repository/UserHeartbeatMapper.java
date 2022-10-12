package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.bo.user.UserOlReportCntBo;
import cn.cyberpeace.mp.auth.model.UserHeartbeatModel;
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
@ModelClass(UserHeartbeatModel.class)
public interface UserHeartbeatMapper extends BaseModelMapper<UserHeartbeatModel> {


    /**
     * 一定时间内的心跳次数
     * @param userId
     * @param time
     * @return
     */
    int getCntByUserId(@Param("userId") String userId, @Param("time") LocalDateTime time);

    /**
     * 一定时间内的心跳次数
     * @param time
     * @return
     */
    List<UserOlReportCntBo> groupCntByUserId(@Param("time") LocalDateTime time);


}
