package cn.cyberpeace.mp.devopscluster.service.impl;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Aggregation;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Sum;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.base.util.define.SerializableFunction;
import cn.cyberpeace.mp.devopscluster.enums.TimeSlotEnums;
import cn.cyberpeace.mp.devopscluster.model.AlarmStatisticsModel;
import cn.cyberpeace.mp.devopscluster.repository.AlarmStatisticsMapper;
import cn.cyberpeace.mp.devopscluster.service.AlarmStatisticsService;
import cn.cyberpeace.mp.devopscluster.service.fields.AlarmStatisticsFields;
import cn.cyberpeace.mp.devopscluster.vo.AlarmStatisticsTrendVO;
import cn.cyberpeace.mp.devopscluster.vo.AlarmStatisticsVO;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@Service
@Slf4j
public class AlarmStatisticsServiceImpl extends BaseModelServiceImpl<AlarmStatisticsModel, AlarmStatisticsMapper, AlarmStatisticsFields,
        QueryParams> implements AlarmStatisticsService {

    private static final Map<String, String> TIME_UNIT_DATEFORMAT_MAP = new HashMap<>();

    public AlarmStatisticsServiceImpl(AlarmStatisticsMapper stuMapper, AlarmStatisticsFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    static void init() {
        TIME_UNIT_DATEFORMAT_MAP.put(TimeSlotEnums.DAY.getUnit(), "%Y-%m-%d");
        TIME_UNIT_DATEFORMAT_MAP.put(TimeSlotEnums.WEEK.getUnit(), "%u");
        TIME_UNIT_DATEFORMAT_MAP.put(TimeSlotEnums.MONTH.getUnit(), "%Y-%m");
        TIME_UNIT_DATEFORMAT_MAP.put(TimeSlotEnums.YEAR.getUnit(), "%Y");
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


    /**
     * 告警趋势
     * @param types 告警类型
     * @param unit 时间单位
     * @param count 时间分段数
     * @return
     */
    @Override
    public List<AlarmStatisticsTrendVO> alarmTrend(Integer[] types, String unit, Integer count, LocalDateTime startTime, LocalDateTime endTime) {
        //var list = this.getModelMapper().serverAlarmStatistics(LocalDateTime.now(), days);

        //getModelMapper().list(new HumanCondition().)

        //HumanCondition orgUserCondition = new HumanCondition()
        //        .filterIn(TenantUserModel::getUser, userIds)
        //        .select(TenantUserModel::getUser)
        //        .selectAs(DateFormatAggr.f(TenantUserModel::getOrg).distinct(), UserOrgRoleBo.FIELD_ORGS)
        //        .groupBy(TenantUserModel::getUser);

        TimeSlotEnums timeSlotEnum = TimeSlotEnums.fromUnit(unit);
        if (Objects.isNull(timeSlotEnum)) {
            log.info("unit:{} invalid!", unit);
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }

        if (Objects.isNull(count) || count < 1 || count > 1000) {
            count = timeSlotEnum.getCount();
        }

        startTime = TimeUtils.stringToLocalDateTime("2022-10-01 10:00:00");
        endTime = TimeUtils.stringToLocalDateTime("2022-11-02 20:00:00");

        HumanCondition condition = new HumanCondition()
                .selectAs(Sum.f(AlarmStatisticsModel::getAlarmCount), AlarmStatisticsVO::getAlarmCount)
                //.selectAs(AlarmStatisticsModel::getAlarmDate, AlarmStatisticsVO::getAlarmDate)
                .selectAs(DateFormatAggr.f(AlarmStatisticsModel::getAlarmDate, TIME_UNIT_DATEFORMAT_MAP.get(unit)), StrUtil.toUnderlineCase(BeanUtils.getBeanGetterFieldName(AlarmStatisticsTrendVO::getTimeBucket)))
                .filterGreaterThanOrEqual(AlarmStatisticsModel::getCreateTime, startTime)
                .filterLessThanOrEqual(AlarmStatisticsModel::getCreateTime, endTime)
                .groupBy(AlarmStatisticsTrendVO::getTimeBucket);


        var result = getModelMapper().list(condition, AlarmStatisticsTrendVO.class);
        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<>();
        }
        return result;
        //return processTrend(list, days);
    }



    static class DateFormatAggr extends Aggregation<DateFormatAggr> {

        public static Function<String, String> genCalcDateFormatter(Object value) {
            return column -> String.format("%s, '%s'", column, value.toString());
        }

        public static DateFormatAggr f(String fieldName, String dateFormatter) {
            var aggregation = new DateFormatAggr();
            aggregation.setFieldName(fieldName);
            aggregation.addCalc(genCalcDateFormatter(dateFormatter));
            return aggregation;
        }

        public static <T> DateFormatAggr f(SerializableFunction<T> getter, String dateFormatter) {
            String fieldName = BeanUtils.getBeanGetterFieldName(getter);
            return f(fieldName, dateFormatter);
        }

        @Override
        public String getFuncName() {
            return "date_format";
        }
    }

}
