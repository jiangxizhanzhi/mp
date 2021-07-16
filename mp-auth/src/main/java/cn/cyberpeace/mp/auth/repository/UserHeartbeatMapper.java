package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.SysConfigModel;
import cn.cyberpeace.mp.auth.model.UserHeartbeatModel;
import cn.cyberpeace.mp.auth.model.UserOnlineReportModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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
    @Select("select count(*) from mp_auth_user_heartbeat where user = #{userId} and createtime > #{time}")
    List<UserOnlineReportModel> getCntByUserId(@Param("userId") String userId, @Param("time") LocalDateTime time);


}
