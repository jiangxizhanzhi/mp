package cn.cyberpeace.mp.devopscluster.service.impl;

import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devopscluster.model.AlarmStatisticsModel;
import cn.cyberpeace.mp.devopscluster.repository.AlarmStatisticsMapper;
import cn.cyberpeace.mp.devopscluster.service.AlarmStatisticsService;
import cn.cyberpeace.mp.devopscluster.service.fields.AlarmStatisticsFields;
import cn.cyberpeace.mp.devopscluster.vo.AlarmStatisticsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        return this.getModelMapper().todayAlarmStatistics(LocalDateTime.now());
    }

    /**
     * 服务告警趋势
     * @return
     */
    @Override
    public List<AlarmStatisticsVO> serviceAlarmStatistics(int days) {
        var list = this.getModelMapper().serviceAlarmStatistics(LocalDateTime.now(), days);

        return processTrend(list, days);
    }

    /**
     * 设备告警趋势
     * @return
     */
    @Override
    public List<AlarmStatisticsVO> serverAlarmStatistics(int days) {
        var list = this.getModelMapper().serverAlarmStatistics(LocalDateTime.now(), days);
        return processTrend(list, days);
    }

    private List<AlarmStatisticsVO> processTrend(List<AlarmStatisticsVO> voList, int days) {
        LocalDate nowDate = LocalDate.now();
        LocalDate startDate = nowDate.minusDays(days);
        List<AlarmStatisticsVO> finalList = new ArrayList<>();
        LocalDate date = startDate.plusDays(1);
        while (!date.isAfter(nowDate)) {
            LocalDate finalDate = date;
            Optional<AlarmStatisticsVO> optional = voList.stream().filter(vo -> vo.getAlarmDate().isEqual(finalDate)).findFirst();
            if (optional.isPresent()) {
                finalList.add(optional.get());
            } else {
                AlarmStatisticsVO vo = new AlarmStatisticsVO();
                vo.setAlarmDate(date);
                vo.setAlarmCount(0);
                finalList.add(vo);
            }
            date = date.plusDays(1);
        }

        return finalList;
    }
}
