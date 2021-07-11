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


@Mapper
@Repository
@ModelClass(UserOnlineRecordModel.class)
public interface UserOnlineRecordMapper extends BaseModelMapper<UserOnlineRecordModel> {

    List<UserOnlineRecordSummaryBo> listRecordSummary(@Param("userIds") List<String> userIds,
                                                      @Param("priorityUserIds") List<String> priorityUserIds,
                                                      @Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime);

}
