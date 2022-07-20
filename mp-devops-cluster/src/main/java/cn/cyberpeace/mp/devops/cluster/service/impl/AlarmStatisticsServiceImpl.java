package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devops.cluster.model.AlarmStatisticsModel;
import cn.cyberpeace.mp.devops.cluster.repository.AlarmStatisticsMapper;
import cn.cyberpeace.mp.devops.cluster.service.AlarmStatisticsService;
import cn.cyberpeace.mp.devops.cluster.service.fields.AlarmStatisticsFields;
import cn.cyberpeace.mp.devops.cluster.vo.AlarmStatisticsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AlarmStatisticsServiceImpl extends BaseModelServiceImpl<AlarmStatisticsModel, AlarmStatisticsMapper, AlarmStatisticsFields,
        QueryParams> implements AlarmStatisticsService {

    public AlarmStatisticsServiceImpl(AlarmStatisticsMapper stuMapper, AlarmStatisticsFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    /**
     * 查询今天告警统计信息
     * @return
     */
    @Override
    public List<AlarmStatisticsModel> todayAlarmStatistics() {
        return this.getModelMapper().todayAlarmStatistics();
    }

    /**
     * 服务告警趋势
     * @return
     */
    @Override
    public List<AlarmStatisticsVO> serviceAlarmStatistics() {
        return this.getModelMapper().serviceAlarmStatistics();
    }

    /**
     * 设备告警趋势
     * @return
     */
    @Override
    public List<AlarmStatisticsVO> serverAlarmStatistics() {
        return this.getModelMapper().serverAlarmStatistics();
    }
}