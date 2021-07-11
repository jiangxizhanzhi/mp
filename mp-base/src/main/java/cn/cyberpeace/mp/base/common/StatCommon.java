package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Count;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Sum;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class StatCommon {

    private StatCommon() {}

    
    public static <T> Map<String, Map<T, Long>> groupCount(BaseModelMapper<?> mapper, HumanCondition condition,
                                                           String groupFieldName, List<String> countFieldNames,
                                                           boolean distinct) {
        List<AggStatField> countFields = countFieldNames.stream()
                .map(fieldName -> CountField.f(fieldName, distinct)).collect(Collectors.toList());
        GroupStatResult<T, ?> statResult = groupAggStat(mapper, condition, groupFieldName, countFields);
        return statResult.getCountResult();
    }

    
    public static <T, S extends Number> Map<String, Map<T, S>> groupSum(
            BaseModelMapper<?> mapper, HumanCondition condition, String groupFieldName, List<String> sumFieldNames) {
        List<AggStatField> sumFields = sumFieldNames.stream().map(SumField::f).collect(Collectors.toList());
        GroupStatResult<T, S> statResult = groupAggStat(mapper, condition, groupFieldName, sumFields);
        return statResult.getSumResult();
    }

    
    public static <T, S extends Number> GroupStatResult<T, S> groupAggStat(
            BaseModelMapper<?> mapper, HumanCondition condition, String groupFieldName, List<AggStatField> statFields) {
        condition.selectAs(groupFieldName, groupFieldName).groupBy(groupFieldName);

        Map<String, Map<T, Long>> countResult = new HashMap<>();
        Map<String, Map<T, S>> sumResult = new HashMap<>();
        List<CountField> countFields = new ArrayList<>();
        List<SumField> sumFields = new ArrayList<>();
        for (var statField: statFields) {
            statField.loadToCondition(condition);

            if (statField instanceof CountField) {
                countFields.add((CountField) statField);
                countResult.put(statField.getName(), new HashMap<>());
            } else if (statField instanceof SumField) {
                sumFields.add((SumField) statField);
                sumResult.put(statField.getName(), new HashMap<>());
            }
        }

        var statLines = mapper.originList(condition);
        for (var statLine: statLines) {
            T groupValue = (T) statLine.get(groupFieldName);
            for (var coundField: countFields) {
                countResult.get(coundField.getName())
                        .put(groupValue, Long.valueOf(statLine.get(coundField.getQueryKey()).toString()));
            }
            for (var sumField: sumFields) {
                sumResult.get(sumField.getName())
                        .put(groupValue, (S) statLine.get(sumField.getQueryKey()));
            }
        }
        GroupStatResult<T, S> statResult = new GroupStatResult<>();
        statResult.setCountResult(countResult);
        statResult.setSumResult(sumResult);
        return statResult;
    }

    
    public static <T> Map<String, Map<T, Object>> groupCalc(
            BaseModelMapper<?> mapper, HumanCondition condition, String groupFieldName,
            Map<String, Function<List<Object>, Object>> fieldCalcMap) {
        condition.selectAs(groupFieldName, groupFieldName).groupBy(groupFieldName);
        var fieldNames = fieldCalcMap.keySet();
        fieldNames.forEach(fieldName -> condition.selectAs(fieldName, fieldName));

        var result = mapper.originList(condition);
        Map<String, Map<T, List<Object>>> fieldGroupValuesMap = new HashMap<>();
        result.forEach(resultItem -> {
            fieldNames.forEach(fieldName -> {
                var fieldGroupValues = fieldGroupValuesMap.getOrDefault(fieldName, new HashMap<>());
                var groupValue = (T) resultItem.get(groupFieldName);
                var groupValues = fieldGroupValues.getOrDefault(groupValue, new ArrayList<>());
                var fieldValue = resultItem.get(fieldName);
                groupValues.add(fieldValue);
                fieldGroupValues.put(groupValue, groupValues);
                fieldGroupValuesMap.put(fieldName, fieldGroupValues);
            });
        });

        Map<String, Map<T, Object>> fieldGroupCalcValueMap = new HashMap<>();
        fieldNames.forEach(fieldName -> {
            fieldGroupCalcValueMap.put(fieldName, new HashMap<>());
        });
        fieldGroupValuesMap.forEach((fieldName, fieldGroupValues) -> {
            var fieldGroupCalcValue = fieldGroupCalcValueMap.get(fieldName);
            var calc = fieldCalcMap.get(fieldName);
            fieldGroupValues.forEach((groupValue, groupValues) -> {
                fieldGroupCalcValue.put(groupValue, calc.apply(groupValues));
            });
        });
        return fieldGroupCalcValueMap;
    }

    
    public static Map<String, Long> count(BaseModelMapper<?> mapper, HumanCondition condition,
                                          List<String> fieldNames, boolean distinct) {
        List<AggStatField> countFields = fieldNames.stream()
                .map(fieldName -> CountField.f(fieldName, distinct)).collect(Collectors.toList());
        StatResult<?> statResult = aggStat(mapper, condition, countFields);
        return statResult.getCountResult();
    }

    
    public static <S extends Number> Map<String, S> sum(
            BaseModelMapper<?> mapper, HumanCondition condition, List<String> fieldNames) {
        List<AggStatField> sumFields = fieldNames.stream().map(SumField::f).collect(Collectors.toList());
        StatResult<S> statResult = aggStat(mapper, condition, sumFields);
        return statResult.getSumResult();
    }

    
    public static <S extends Number> StatResult<S> aggStat(
            BaseModelMapper<?> mapper, HumanCondition condition, List<AggStatField> statFields) {
        Map<String, Long> countResult = new HashMap<>();
        Map<String, S> sumResult = new HashMap<>();
        List<CountField> countFields = new ArrayList<>();
        List<SumField> sumFields = new ArrayList<>();
        for (var statField: statFields) {
            statField.loadToCondition(condition);

            if (statField instanceof CountField) {
                countFields.add((CountField) statField);
                countResult.put(statField.getName(), 0L);
            } else if (statField instanceof SumField) {
                sumFields.add((SumField) statField);
                sumResult.put(statField.getName(), null);
            }
        }

        var statLines = mapper.originList(condition);

        for (var statLine: statLines) {
            for (var coundField: countFields) {
                countResult.put(coundField.getName(), Long.valueOf(statLine.get(coundField.getQueryKey()).toString()));
            }
            for (var sumField: sumFields) {
                sumResult.put(sumField.getName(), (S) statLine.get(sumField.getQueryKey()));
            }
        }
        StatResult<S> statResult = new StatResult<>();
        statResult.setCountResult(countResult);
        statResult.setSumResult(sumResult);
        return statResult;
    }

    
    public static Map<String, Object> calc(BaseModelMapper<?> mapper, HumanCondition condition,
                                           Map<String, Function<List<Object>, Object>> fieldCalcMap) {
        var fieldNames = fieldCalcMap.keySet();
        fieldNames.forEach(fieldName -> condition.selectAs(fieldName, fieldName));
        var result = mapper.originList(condition);
        Map<String, List<Object>> fieldValuesMap = new HashMap<>();
        result.forEach(resultItem -> {
            fieldNames.forEach(fieldName -> {
                var fieldValue = resultItem.get(fieldName);
                var fieldValues = fieldValuesMap.getOrDefault(fieldName, new ArrayList<>());
                fieldValues.add(fieldValue);
                fieldValuesMap.put(fieldName, fieldValues);
            });
        });
        Map<String, Object> fieldCalcValueMap = new HashMap<>();
        fieldNames.forEach(fieldName -> {
            fieldCalcValueMap.put(fieldName, null);
        });
        fieldValuesMap.forEach((fieldName, fieldValues) -> {
            fieldCalcValueMap.put(fieldName, fieldCalcMap.get(fieldName).apply(fieldValues));
        });
        return fieldCalcValueMap;
    }

    @Data
    public static class GroupStatResult<T, S extends Number> {
        private Map<String, Map<T, Long>> countResult;
        private Map<String, Map<T, S>> sumResult;
    }

    @Data
    public static class StatResult<S extends Number> {
        private Map<String, Long> countResult;
        private Map<String, S> sumResult;
    }

    @Data
    public abstract static class AggStatField {
        private String name;
        private String queryKey = TextUtils.uuid();

        public abstract void loadToCondition(HumanCondition condition);
    }

    @Data
    public static class CountField extends AggStatField {
        private Boolean distinct;

        public static CountField f(String name) {
            return f(name, false);
        }

        public static CountField f(String name, boolean distinct) {
            var field = new CountField();
            field.setName(name);
            field.setDistinct(distinct);
            return field;
        }

        @Override
        public void loadToCondition(HumanCondition condition) {
            if (Boolean.TRUE.equals(getDistinct())) {
                condition.selectAs(Count.f(getName()).calcEmptyNull().distinct(), getQueryKey());
            } else {
                condition.selectAs(Count.f(getName()).calcEmptyNull(), getQueryKey());
            }
        }
    }

    @Data
    public static class SumField extends AggStatField {
        public static SumField f(String name) {
            var field = new SumField();
            field.setName(name);
            return field;
        }

        @Override
        public void loadToCondition(HumanCondition condition) {
            condition.selectAs(Sum.f(getName()), getQueryKey());
        }
    }

}
