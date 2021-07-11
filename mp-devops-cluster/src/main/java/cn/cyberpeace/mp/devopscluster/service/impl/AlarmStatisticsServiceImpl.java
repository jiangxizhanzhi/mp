package cn.cyberpeace.mp.devopscluster.service.impl;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Aggregation;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Sum;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.define.SerializableFunction;
import cn.cyberpeace.mp.devopscluster.enums.TimeSlotEnums;
import cn.cyberpeace.mp.devopscluster.model.AlarmStatisticsModel;
import cn.cyberpeace.mp.devopscluster.repository.AlarmStatisticsMapper;
import cn.cyberpeace.mp.devopscluster.service.AlarmStatisticsService;
import cn.cyberpeace.mp.devopscluster.service.fields.AlarmStatisticsFields;
import cn.cyberpeace.mp.devopscluster.vo.AlarmStatisticsTrendVO;
import cn.cyberpeace.mp.devopscluster.vo.AlarmStatisticsVO;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
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

    private static final Map<String, TimeUnitProcessor> DATE_PROCESSOR_MAP = new HashMap<>();

    public AlarmStatisticsServiceImpl(AlarmStatisticsMapper stuMapper, AlarmStatisticsFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    @PostConstruct
    public void init() {
        DATE_PROCESSOR_MAP.put(TimeSlotEnums.HOUR.getUnit(), TimeUnitProcessor.builder().dateFormatter("yyyy-MM-dd HH").sqlDateFormatter("%Y-%m-%d %H").previousTimeCalcFunc(date -> date.minusHours(1)).build());
        DATE_PROCESSOR_MAP.put(TimeSlotEnums.DAY.getUnit(), TimeUnitProcessor.builder().dateFormatter("yyyy-MM-dd").sqlDateFormatter("%Y-%m-%d").previousTimeCalcFunc(date -> date.minusDays(1)).build());
        
        DATE_PROCESSOR_MAP.put(TimeSlotEnums.MONTH.getUnit(), TimeUnitProcessor.builder().dateFormatter("yyyy-MM").sqlDateFormatter("%Y-%m").previousTimeCalcFunc(date -> date.minusMonths(1)).build());
        DATE_PROCESSOR_MAP.put(TimeSlotEnums.YEAR.getUnit(), TimeUnitProcessor.builder().dateFormatter("yyyy").sqlDateFormatter("%Y").previousTimeCalcFunc(date -> date.minusYears(1)).build());
    }

    
    @Override
    public List<AlarmStatisticsModel> todayAlarmStatistics() {
        return this.getModelMapper().todayAlarmStatistics(LocalDateTime.now());
    }

    
    @Override
    public List<AlarmStatisticsVO> serviceAlarmStatistics(int days) {
        var list = this.getModelMapper().serviceAlarmStatistics(LocalDateTime.now(), days);

        return processTrend(list, days);
    }

    
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


    
    @Override
    public List<AlarmStatisticsTrendVO> alarmTrend(Integer[] types, String unit, Integer count, LocalDateTime startTime, LocalDateTime endTime) {
        TimeSlotEnums timeSlotEnum = TimeSlotEnums.fromUnit(unit);
        if (Objects.isNull(timeSlotEnum)) {
            log.info("unit:{} invalid!", unit);
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }

        if (Objects.isNull(count) || count < 1 || count > 1000) {
            count = timeSlotEnum.getCount();
        }

        
        if (Objects.nonNull(startTime) && Objects.nonNull(endTime) && startTime.isAfter(endTime)) {
            log.error("start time is not before end time!");
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }

        if (Objects.isNull(endTime)) {
            endTime = LocalDateTime.now();
        }

        if (Objects.isNull(startTime)) {
            LocalDateTime processedStartTime = processStartTime(endTime, timeSlotEnum, count);
            startTime = processedStartTime;
        }

        TimeUnitProcessor timeUnitProcessor = DATE_PROCESSOR_MAP.get(unit);
        HumanCondition condition = new HumanCondition()
                .selectAs(Sum.f(AlarmStatisticsModel::getAlarmCount), AlarmStatisticsVO::getAlarmCount)
                .selectAs(DateFormatAggr.f(AlarmStatisticsModel::getCreateTime, timeUnitProcessor.getSqlDateFormatter()), StrUtil.toUnderlineCase(BeanUtils.getBeanGetterFieldName(AlarmStatisticsTrendVO::getTimeBucket)))
                .filterIn(AlarmStatisticsModel::getAlarmType, types)
                .filterGreaterThanOrEqual(AlarmStatisticsModel::getCreateTime, startTime)
                .filterLessThanOrEqual(AlarmStatisticsModel::getCreateTime, endTime)
                .groupBy(AlarmStatisticsTrendVO::getTimeBucket);

        List<AlarmStatisticsTrendVO> trendVOList = getModelMapper().list(condition, AlarmStatisticsTrendVO.class);
        return processTrendByDateProcessor(trendVOList, timeUnitProcessor, startTime, endTime);
    }


    private LocalDateTime processStartTime(LocalDateTime processedEndTime, TimeSlotEnums timeSlotEnum, Integer count) {
        LocalDateTime processedStartTime = null;

        if (Objects.equals(timeSlotEnum, TimeSlotEnums.HOUR)) {
            
            LocalDateTime startHour = processedEndTime.minusHours(count - 1);
            processedStartTime = startHour.toLocalDate().atTime(startHour.getHour(), 0, 0);
        } else if (Objects.equals(timeSlotEnum, TimeSlotEnums.DAY)) {
            
            processedStartTime = LocalDateTime.of(processedEndTime.minusDays(count).toLocalDate(), LocalTime.MAX);
        
        
        
        
        } else if (Objects.equals(timeSlotEnum, TimeSlotEnums.MONTH)) {
            
            LocalDate firstDayOfBeginMonth = processedEndTime.minusMonths(count).toLocalDate().with(TemporalAdjusters.lastDayOfMonth());
            processedStartTime = LocalDateTime.of(firstDayOfBeginMonth, LocalTime.MAX);
        } else if (Objects.equals(timeSlotEnum, TimeSlotEnums.YEAR)) {
            
            LocalDate firstDayOfBeginYear = processedEndTime.minusYears(count).toLocalDate().with(TemporalAdjusters.lastDayOfYear());
            processedStartTime = LocalDateTime.of(firstDayOfBeginYear, LocalTime.MAX);
        }

        return processedStartTime;
    }


    
    private LocalDateTime processEndTime(LocalDateTime endTime, TimeSlotEnums timeSlotEnum) {

        LocalDateTime processedEndTime = null;

        if (Objects.equals(timeSlotEnum, TimeSlotEnums.DAY)) {
            
            processedEndTime = LocalDateTime.of(endTime.plusDays(1).toLocalDate(), LocalTime.MIN);
        
        
        
        
        } else if (Objects.equals(timeSlotEnum, TimeSlotEnums.MONTH)) {
            
            LocalDate firstDayOfNextMonth = endTime.toLocalDate().with(TemporalAdjusters.firstDayOfNextMonth());
            processedEndTime = LocalDateTime.of(firstDayOfNextMonth, LocalTime.MIN);
        } else if (Objects.equals(timeSlotEnum, TimeSlotEnums.YEAR)) {
            
            LocalDate firstDayOfNextYear = endTime.toLocalDate().with(TemporalAdjusters.firstDayOfNextYear());
            processedEndTime = LocalDateTime.of(firstDayOfNextYear, LocalTime.MIN);
        }

        return processedEndTime;
    }


    private List<AlarmStatisticsTrendVO> processTrendByDateProcessor(List<AlarmStatisticsTrendVO> trendVOList, TimeUnitProcessor timeUnitProcessor, LocalDateTime startTime, LocalDateTime endTime) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(timeUnitProcessor.getDateFormatter());
        List<AlarmStatisticsTrendVO> finalList = new ArrayList<>();
        while (!endTime.isBefore(startTime)) {
            String calculatedTimeBucket = dtf.format(endTime);
            Optional<AlarmStatisticsTrendVO> optional = trendVOList.stream().filter(vo -> Objects.equals(vo.getTimeBucket(), calculatedTimeBucket)).findFirst();
            if (optional.isPresent()) {
                finalList.add(optional.get());
            } else {
                AlarmStatisticsTrendVO vo = new AlarmStatisticsTrendVO();
                vo.setTimeBucket(calculatedTimeBucket);
                vo.setAlarmCount(0);
                finalList.add(vo);
            }
            endTime = timeUnitProcessor.getPreviousTimeCalcFunc().apply(endTime);
        }

        finalList = CollectionUtil.reverse(finalList);
        return finalList;
    }

    @Data
    @Builder
    static class TimeUnitProcessor {
        private String sqlDateFormatter;

        private String dateFormatter;

        Function<LocalDateTime, LocalDateTime> previousTimeCalcFunc;
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
