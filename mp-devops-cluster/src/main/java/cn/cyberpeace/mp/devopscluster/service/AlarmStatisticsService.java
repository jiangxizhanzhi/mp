package cn.cyberpeace.mp.devopscluster.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devopscluster.model.AlarmStatisticsModel;
import cn.cyberpeace.mp.devopscluster.vo.AlarmStatisticsTrendVO;
import cn.cyberpeace.mp.devopscluster.vo.AlarmStatisticsVO;

import java.time.LocalDateTime;
import java.util.List;


public interface AlarmStatisticsService extends BaseModelService<AlarmStatisticsModel, QueryParams> {

    
    List<AlarmStatisticsModel> todayAlarmStatistics();

    
    List<AlarmStatisticsVO> serviceAlarmStatistics(int days);

    
    List<AlarmStatisticsVO> serverAlarmStatistics(int days);

    
    List<AlarmStatisticsTrendVO> alarmTrend(Integer[] types, String unit, Integer count, LocalDateTime startTime, LocalDateTime endTime);

}

