package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devops.cluster.model.AlarmStatisticsModel;
import cn.cyberpeace.mp.devops.cluster.vo.AlarmStatisticsVO;

import java.util.List;

/**
 * @Description 告警统计信息service
 * @Author qiaokang
 * @Date 2022-07-19 16:17:26
 */
public interface AlarmStatisticsService extends BaseModelService<AlarmStatisticsModel, QueryParams> {

    /**
     * 查询今天告警统计信息
     * @return
     */
    List<AlarmStatisticsModel> todayAlarmStatistics();

    /**
     * 服务告警趋势
     * @return
     */
    List<AlarmStatisticsVO> serviceAlarmStatistics();

    /**
     * 设备告警趋势
     * @return
     */
    List<AlarmStatisticsVO> serverAlarmStatistics();

}

