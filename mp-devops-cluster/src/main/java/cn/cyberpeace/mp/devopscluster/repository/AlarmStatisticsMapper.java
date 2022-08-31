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

/**
 * @Description 告警统计信息mapper
 * @Author qiaokang
 * @Date 2022-07-19 16:17:26
 */
@Mapper
@Repository
@ModelClass(AlarmStatisticsModel.class)
public interface AlarmStatisticsMapper extends BaseModelMapper<AlarmStatisticsModel> {

    /**
     * 查询今天告警统计信息
     * @return
     */
    List<AlarmStatisticsModel> todayAlarmStatistics(@Param("currentTime")LocalDateTime currentTime);

    /**
     * 服务告警趋势
     * @return
     */
    List<AlarmStatisticsVO> serviceAlarmStatistics(@Param("currentTime")LocalDateTime currentTime);

    /**
     * 设备告警趋势
     * @return
     */
    List<AlarmStatisticsVO> serverAlarmStatistics(@Param("currentTime")LocalDateTime currentTime);

}
