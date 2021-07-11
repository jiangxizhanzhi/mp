package cn.cyberpeace.mp.devopscluster.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devopscluster.model.AlarmStatisticsModel;
import cn.cyberpeace.mp.devopscluster.vo.AlarmStatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Mapper
@Repository
@ModelClass(AlarmStatisticsModel.class)
public interface AlarmStatisticsMapper extends BaseModelMapper<AlarmStatisticsModel> {

    
    List<AlarmStatisticsModel> todayAlarmStatistics(@Param("currentTime") LocalDateTime currentTime);

    
    List<AlarmStatisticsVO> serviceAlarmStatistics(@Param("currentTime") LocalDateTime currentTime, @Param("days") int days);

    
    List<AlarmStatisticsVO> serverAlarmStatistics(@Param("currentTime") LocalDateTime currentTime, @Param("days") int days);

}
