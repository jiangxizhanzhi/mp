package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.dto.user.UserOlReportCntDto;
import cn.cyberpeace.mp.auth.model.SysConfigModel;
import cn.cyberpeace.mp.auth.model.UserHeartbeatModel;
import cn.cyberpeace.mp.auth.model.UserOnlineReportModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    @Select("select count(*) from mp_auth_user_heartbeat where user = #{userId} and create_time > #{time}")
    int getCntByUserId(@Param("userId") String userId, @Param("time") LocalDateTime time);


    /**
     * 一定时间内的心跳次数
     * @param time
     * @return
     */
    @Select("select u.id uid, u.username username, u.name uname, count(h.id) cnt " +
            "from mp_auth_user u " +
            "         left join " +
            "     (select * from mp_auth_user_heartbeat where create_time > #{time}) h on h.user = u.id " +
            "group by u.id")
    List<UserOlReportCntDto> groupCntByUserId(@Param("time") LocalDateTime time);


}
