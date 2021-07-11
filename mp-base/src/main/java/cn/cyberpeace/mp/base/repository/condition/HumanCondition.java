package cn.cyberpeace.mp.base.repository.condition;

import cn.cyberpeace.mp.base.model.common.ModelUtils;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Aggregation;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.define.SerializableFunction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class HumanCondition {
    public static final Integer MAX_LINE_NUMBER = 999999999;

    private Boolean distinct = false;
    private final Map<String, String> selectFields = new HashMap<>();
    private Integer limit;
    private Integer offset;
    private final List<String> groupFields = new ArrayList<>();
    private final List<String> orders = new ArrayList<>();
    private final AndCondition andCondition = new AndCondition();
    private CustomCondition customCondition;
    
    private final Map<String, Class<?>> linkedModels = new LinkedHashMap<>();
    
    private final Map<Class<?>, String> linkedModelNames = new LinkedHashMap<>();
    
    private String currentLinkName;

    public static String formatSqlElement(String sqlElement) {
        return String.format("`%s`", sqlElement);
    }

    public String genLinkFieldName(String linkName, String fieldName) {
        return StringUtils.joinWith(".", linkName, fieldName);
    }

    public String genLinkFieldName(Class<?> model, String fieldName) {
        String linkName = linkedModelNames.get(model);
        return genLinkFieldName(linkName, fieldName);
    }

    public String currentFieldName(String fieldName) {
        if (currentLinkName == null) {
            return formatSqlElement(fieldName);
        } else {
            return genLinkFieldName(currentLinkName, fieldName);
        }
    }

    public String currentColumnName(String fieldName) {
        return currentFieldName(TextUtils.humpToLine(fieldName));
    }

    
    public String[] toSelectFields() {
        List<String> selectFieldTexts = new ArrayList<>();
        selectFields.forEach((fieldName, outputFieldName) -> {
            if (StringUtils.isEmpty(outputFieldName)) {
                selectFieldTexts.add(TextUtils.humpToLine(fieldName));
            } else {
                selectFieldTexts.add(StringUtils.joinWith(" ", TextUtils.humpToLine(fieldName),
                        formatSqlElement(outputFieldName)));
            }
        });
        return selectFieldTexts.toArray(new String[0]);
    }

    
    public String[] toDefaultSelectFields(Class<?> model) {
        String linkName = linkedModelNames.getOrDefault(model, null);
        if (linkName == null) {
            return new String[0];
        }

        String[] fieldNames = ClassUtils.getBeanClassFields(model);
        var selectFieldNames = new String[fieldNames.length];
        for (var i = 0; i < fieldNames.length; i++) {
            selectFieldNames[i] = TextUtils.humpToLine(genLinkFieldName(linkName, fieldNames[i]));
        }

        return selectFieldNames;
    }

    
    public Condition toCondition() {
        var condition = new Condition();
        condition.setDistinct(distinct);
        condition.setPagination(new PaginationCondition(limit, offset));
        condition.setGroup(new GroupCondition(TextUtils.humpToLine(groupFields.toArray(new String[0]))));
        condition.setOrder(new OrderCondition(TextUtils.humpToLine(orders.toArray(new String[0]))));
        condition.setAndi(andCondition);
        condition.setCustom(customCondition);
        return condition;
    }

    
    public String toTableName() {
        if (linkedModels.isEmpty()) {
            return null;
        }
        List<String> tableNames = new ArrayList<>();
        linkedModels.forEach((linkName, model) -> tableNames.add(
                StringUtils.joinWith(" ", ModelUtils.getTableName(model), linkName)));
        return StringUtils.joinWith(", ", tableNames.toArray());
    }

    
    public HumanCondition linkAs(Class<?> model, String linkName) {
        linkedModels.put(linkName, model);
        linkedModelNames.put(model, linkName);
        currentLinkName = linkName;
        return this;
    }

    
    public HumanCondition link(Class<?>... models) {
        for (Class<?> model: models) {
            String linkName = "m" + linkedModels.size();
            linkAs(model, linkName);
        }
        return this;
    }

    
    public HumanCondition currentLink(String linkName) {
        currentLinkName = linkName;
        return this;
    }

    
    public HumanCondition currentLink(Class<?> model) {
        String linkName = linkedModelNames.get(model);
        return currentLink(linkName);
    }

    
    public HumanCondition selectAs(String fieldName, String outputFieldName) {
        selectFields.put(currentFieldName(fieldName), outputFieldName);
        return this;
    }

    
    public <O> HumanCondition selectAs(String fieldName, SerializableFunction<O> outputFieldGetter) {
        return selectAs(fieldName, BeanUtils.getBeanGetterFieldName(outputFieldGetter));
    }

    
    public <T> HumanCondition selectAs(SerializableFunction<T> fieldGetter, String outputFieldName) {
        return selectAs(BeanUtils.getBeanGetterFieldName(fieldGetter), outputFieldName);
    }

    
    public <T, O> HumanCondition selectAs(SerializableFunction<T> fieldGetter,
                                          SerializableFunction<O> outputFieldGetter) {
        return selectAs(fieldGetter, BeanUtils.getBeanGetterFieldName(outputFieldGetter));
    }

    
    public HumanCondition select(String... fieldNames) {
        for (String fieldName: fieldNames) {
            selectFields.put(currentFieldName(fieldName), null);
        }
        return this;
    }

    
    public <T> HumanCondition select(SerializableFunction<T>... fieldGetters) {
        return select(Arrays.stream(fieldGetters).map(BeanUtils::getBeanGetterFieldName).toArray(String[]::new));
    }

    
    public HumanCondition selectAs(Aggregation<?> aggregation, String outputFieldName) {
        selectFields.put(aggregation.toSqlField(currentFieldName(aggregation.getFieldName())), outputFieldName);
        return this;
    }

    
    public <O> HumanCondition selectAs(Aggregation<?> aggregation, SerializableFunction<O> outputFieldGetter) {
        return selectAs(aggregation, BeanUtils.getBeanGetterFieldName(outputFieldGetter));
    }

    
    public HumanCondition select(Aggregation<?>... aggregations) {
        for (Aggregation<?> aggregation: aggregations) {
            String fieldName = aggregation.getFieldName();
            String funcName = aggregation.getFuncName();
            String outputFieldNameSuffix = funcName.substring(0, 1).toUpperCase() + funcName.substring(1);
            var outputFieldName = StringUtils.join(fieldName, outputFieldNameSuffix);
            selectFields.put(aggregation.toSqlField(currentFieldName(fieldName)), outputFieldName);
        }
        return this;
    }

    
    public HumanCondition distinct() {
        distinct = true;
        return this;
    }

    
    public HumanCondition pagination(Integer page, Integer pageSize) {
        
        if (pageSize == null || pageSize < 0) {
            limit = null;
            offset = null;
            return this;
        }

        limit = pageSize;
        if (page == null || page <= 0) {
            offset = null;
        } else {
            offset = (page - 1) * limit;
        }

        return this;
    }

    
    public HumanCondition slice(Integer startIndex, Integer endIndex) {
        offset = (startIndex == null || startIndex < 0) ? 0 : startIndex;
        limit = (endIndex == null || endIndex - offset < 0) ? MAX_LINE_NUMBER : endIndex - offset;
        return this;
    }

    
    public HumanCondition groupBy(String... fieldNames) {
        for (String fieldName: fieldNames) {
            groupFields.add(currentFieldName(fieldName));
        }
        return this;
    }

    
    public <T> HumanCondition groupBy(SerializableFunction<T>... fieldGetters) {
        return groupBy(Arrays.stream(fieldGetters).map(BeanUtils::getBeanGetterFieldName).toArray(String[]::new));
    }

    
    public HumanCondition orderBy(String... orders) {
        for (String order: orders) {
            String linkedOrder;
            if (order.startsWith("-")) {
                linkedOrder = currentFieldName(order.substring(1)) + " desc";
            } else {
                linkedOrder = currentFieldName(order);
            }
            this.orders.add(linkedOrder);
        }
        return this;
    }

    
    public <T> HumanCondition orderBy(SerializableFunction<T> fieldGetter, boolean reverse) {
        String fieldName = BeanUtils.getBeanGetterFieldName(fieldGetter);
        return orderBy(reverse ? ("-" + fieldName) : fieldName);
    }

    
    public <T> HumanCondition orderBy(SerializableFunction<T> fieldGetter) {
        return orderBy(fieldGetter, false);
    }

    
    public <T> HumanCondition reverseOrderBy(SerializableFunction<T> fieldGetter) {
        return orderBy(fieldGetter, true);
    }

    
    public HumanCondition filterLink(Class<?> sourceModel, String sourceFieldName,
                                     Class<?> targetModel, String targetFieldName) {
        andCondition.addLink(genLinkFieldName(sourceModel, TextUtils.humpToLine(sourceFieldName)),
                genLinkFieldName(targetModel, TextUtils.humpToLine(targetFieldName)));
        return this;
    }

    
    public <S, T> HumanCondition filterLink(Class<?> sourceModel, SerializableFunction<S> sourceFieldGetter,
                                         Class<?> targetModel, SerializableFunction<T> targetFieldGetter) {
        return filterLink(sourceModel, BeanUtils.getBeanGetterFieldName(sourceFieldGetter),
                targetModel, BeanUtils.getBeanGetterFieldName(targetFieldGetter));
    }

    
    public HumanCondition filterLink(String sourceLinkName, String sourceFieldName,
                                     String targetLinkName, String targetFieldName) {
        andCondition.addLink(genLinkFieldName(sourceLinkName, TextUtils.humpToLine(sourceFieldName)),
                genLinkFieldName(targetLinkName, TextUtils.humpToLine(targetFieldName)));
        return this;
    }

    
    public <S, T> HumanCondition filterLink(String sourceLinkName, SerializableFunction<S> sourceFieldGetter,
                                         String targetLinkName, SerializableFunction<T> targetFieldGetter) {
        return filterLink(sourceLinkName, BeanUtils.getBeanGetterFieldName(sourceFieldGetter),
                targetLinkName, BeanUtils.getBeanGetterFieldName(targetFieldGetter));
    }

    
    public HumanCondition filterIn(String fieldName, Object[] fieldValue) {
        andCondition.addIn(currentColumnName(fieldName), fieldValue);
        return this;
    }

    
    public <T> HumanCondition filterIn(SerializableFunction<T> fieldGetter, Object[] fieldValue) {
        return filterIn(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    
    public HumanCondition filterNotIn(String fieldName, Object[] fieldValue) {
        andCondition.addNotIn(currentColumnName(fieldName), fieldValue);
        return this;
    }

    
    public <T> HumanCondition filterNotIn(SerializableFunction<T> fieldGetter, Object[] fieldValue) {
        return filterNotIn(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    
    public HumanCondition filterEqual(String fieldName, Object fieldValue) {
        andCondition.addEqual(currentColumnName(fieldName), fieldValue);
        return this;
    }

    
    public <T> HumanCondition filterEqual(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterEqual(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    
    public HumanCondition filterNotEqual(String fieldName, Object fieldValue) {
        andCondition.addNotEqual(currentColumnName(fieldName), fieldValue);
        return this;
    }

    
    public <T> HumanCondition filterNotEqual(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterNotEqual(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    
    public HumanCondition filterGreaterThan(String fieldName, Object fieldValue) {
        andCondition.addGreaterThan(currentColumnName(fieldName), fieldValue);
        return this;
    }

    
    public <T> HumanCondition filterGreaterThan(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterGreaterThan(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    
    public HumanCondition filterGreaterThanOrEqual(String fieldName, Object fieldValue) {
        andCondition.addGreaterThanOrEqual(currentColumnName(fieldName), fieldValue);
        return this;
    }

    
    public <T> HumanCondition filterGreaterThanOrEqual(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterGreaterThanOrEqual(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    
    public HumanCondition filterLessThan(String fieldName, Object fieldValue) {
        andCondition.addLessThan(currentColumnName(fieldName), fieldValue);
        return this;
    }

    
    public <T> HumanCondition filterLessThan(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterLessThan(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    
    public HumanCondition filterLessThanOrEqual(String fieldName, Object fieldValue) {
        andCondition.addLessThanOrEqual(currentColumnName(fieldName), fieldValue);
        return this;
    }

    
    public <T> HumanCondition filterLessThanOrEqual(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterLessThanOrEqual(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    
    public HumanCondition filterContains(String fieldName, Object fieldValue) {
        andCondition.addContains(currentColumnName(fieldName), fieldValue);
        return this;
    }

    
    public <T> HumanCondition filterContains(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterContains(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    
    public HumanCondition filterIContains(String fieldName, Object fieldValue) {
        andCondition.addIContains(currentColumnName(fieldName), fieldValue);
        return this;
    }

    
    public <T> HumanCondition filterIContains(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterIContains(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    
    public HumanCondition filterStartsWith(String fieldName, Object fieldValue) {
        andCondition.addStartsWith(currentColumnName(fieldName), fieldValue);
        return this;
    }

    
    public <T> HumanCondition filterStartsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterStartsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    
    public HumanCondition filterIStartsWith(String fieldName, Object fieldValue) {
        andCondition.addIStartsWith(currentColumnName(fieldName), fieldValue);
        return this;
    }

    
    public <T> HumanCondition filterIStartsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterIStartsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    
    public HumanCondition filterEndsWith(String fieldName, Object fieldValue) {
        andCondition.addEndsWith(currentColumnName(fieldName), fieldValue);
        return this;
    }

    
    public <T> HumanCondition filterEndsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterEndsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    
    public HumanCondition filterIEndsWith(String fieldName, Object fieldValue) {
        andCondition.addIEndsWith(currentColumnName(fieldName), fieldValue);
        return this;
    }

    
    public <T> HumanCondition filterIEndsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterIEndsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    
    public HumanCondition filterNotContains(String fieldName, Object fieldValue) {
        andCondition.addNotContains(currentColumnName(fieldName), fieldValue);
        return this;
    }

    
    public <T> HumanCondition filterNotContains(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterNotContains(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    
    public HumanCondition filterNotIContains(String fieldName, Object fieldValue) {
        andCondition.addNotIContains(currentColumnName(fieldName), fieldValue);
        return this;
    }

    
    public <T> HumanCondition filterNotIContains(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterNotIContains(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    
    public HumanCondition filterNotStartsWith(String fieldName, Object fieldValue) {
        andCondition.addNotStartsWith(currentColumnName(fieldName), fieldValue);
        return this;
    }

    
    public <T> HumanCondition filterNotStartsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterNotStartsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    
    public HumanCondition filterNotIStartsWith(String fieldName, Object fieldValue) {
        andCondition.addNotIStartsWith(currentColumnName(fieldName), fieldValue);
        return this;
    }

    
    public <T> HumanCondition filterNotIStartsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterNotIStartsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    
    public HumanCondition filterNotEndsWith(String fieldName, Object fieldValue) {
        andCondition.addNotEndsWith(currentColumnName(fieldName), fieldValue);
        return this;
    }

    
    public <T> HumanCondition filterNotEndsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterNotEndsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    
    public HumanCondition filterNotIEndsWith(String fieldName, Object fieldValue) {
        andCondition.addNotIEndsWith(currentColumnName(fieldName), fieldValue);
        return this;
    }

    
    public <T> HumanCondition filterNotIEndsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterNotIEndsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrConditionProxy {
        private HumanCondition humanCondition;

        private final OrCondition orCondition = new OrCondition();

        public OrConditionProxy currentLink(Class<?> model) {
            humanCondition.currentLink(model);
            return this;
        }

        public OrConditionProxy orLink(Class<?> sourceModel, String sourceFieldName,
                                       Class<?> targetModel, String targetFieldName) {
            orCondition.addLink(humanCondition.genLinkFieldName(sourceModel, TextUtils.humpToLine(sourceFieldName)),
                    humanCondition.genLinkFieldName(targetModel, TextUtils.humpToLine(targetFieldName)));
            return this;
        }

        public <S, T> OrConditionProxy orLink(Class<?> sourceModel, SerializableFunction<S> sourceFieldGetter,
                                              Class<?> targetModel, SerializableFunction<T> targetFieldGetter) {
            return orLink(sourceModel, BeanUtils.getBeanGetterFieldName(sourceFieldGetter),
                    targetModel, BeanUtils.getBeanGetterFieldName(targetFieldGetter));
        }

        public OrConditionProxy orLink(String sourceLinkName, String sourceFieldName,
                                       String targetLinkName, String targetFieldName) {
            orCondition.addLink(humanCondition.genLinkFieldName(sourceLinkName, TextUtils.humpToLine(sourceFieldName)),
                    humanCondition.genLinkFieldName(targetLinkName, TextUtils.humpToLine(targetFieldName)));
            return this;
        }

        public <S, T> OrConditionProxy orLink(String sourceLinkName, SerializableFunction<S> sourceFieldGetter,
                                              String targetLinkName, SerializableFunction<T> targetFieldGetter) {
            return orLink(sourceLinkName, BeanUtils.getBeanGetterFieldName(sourceFieldGetter),
                    targetLinkName, BeanUtils.getBeanGetterFieldName(targetFieldGetter));
        }

        public OrConditionProxy orIn(String fieldName, Object[] fieldValue) {
            orCondition.addIn(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionProxy orIn(SerializableFunction<T> fieldGetter, Object[] fieldValue) {
            return orIn(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionProxy orNotIn(String fieldName, Object[] fieldValue) {
            orCondition.addNotIn(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionProxy orNotIn(SerializableFunction<T> fieldGetter, Object[] fieldValue) {
            return orNotIn(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionProxy orEqual(String fieldName, Object fieldValue) {
            orCondition.addEqual(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionProxy orEqual(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orEqual(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionProxy orNotEqual(String fieldName, Object fieldValue) {
            orCondition.addNotEqual(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionProxy orNotEqual(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orNotEqual(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionProxy orGreaterThan(String fieldName, Object fieldValue) {
            orCondition.addGreaterThan(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionProxy orGreaterThan(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orGreaterThan(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionProxy orGreaterThanOrEqual(String fieldName, Object fieldValue) {
            orCondition.addGreaterThanOrEqual(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionProxy orGreaterThanOrEqual(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orGreaterThanOrEqual(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionProxy orLessThan(String fieldName, Object fieldValue) {
            orCondition.addLessThan(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionProxy orLessThan(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orLessThan(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionProxy orLessThanOrEqual(String fieldName, Object fieldValue) {
            orCondition.addLessThanOrEqual(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionProxy orLessThanOrEqual(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orLessThanOrEqual(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionProxy orContains(String fieldName, Object fieldValue) {
            orCondition.addContains(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionProxy orContains(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orContains(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionProxy orIContains(String fieldName, Object fieldValue) {
            orCondition.addIContains(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionProxy orIContains(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orIContains(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionProxy orStartsWith(String fieldName, Object fieldValue) {
            orCondition.addStartsWith(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionProxy orStartsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orStartsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionProxy orIStartsWith(String fieldName, Object fieldValue) {
            orCondition.addIStartsWith(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionProxy orIStartsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orIStartsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionProxy orEndsWith(String fieldName, Object fieldValue) {
            orCondition.addEndsWith(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionProxy orEndsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orEndsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionProxy orIEndsWith(String fieldName, Object fieldValue) {
            orCondition.addIEndsWith(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionProxy orIEndsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orIEndsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionProxy orNotContains(String fieldName, Object fieldValue) {
            orCondition.addNotContains(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionProxy orNotContains(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orNotContains(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionProxy orNotIContains(String fieldName, Object fieldValue) {
            orCondition.addNotIContains(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionProxy orNotIContains(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orNotIContains(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionProxy orNotStartsWith(String fieldName, Object fieldValue) {
            orCondition.addNotStartsWith(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionProxy orNotStartsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orNotStartsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionProxy orNotIStartsWith(String fieldName, Object fieldValue) {
            orCondition.addNotIStartsWith(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionProxy orNotIStartsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orNotIStartsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionProxy orNotEndsWith(String fieldName, Object fieldValue) {
            orCondition.addNotEndsWith(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionProxy orNotEndsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orNotEndsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionProxy orNotIEndsWith(String fieldName, Object fieldValue) {
            orCondition.addNotIEndsWith(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionProxy orNotIEndsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orNotIEndsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        
        public HumanCondition filterOrEnd() {
            humanCondition.getAndCondition().addCondition(orCondition);
            return humanCondition;
        }

        
        public AndConditionProxy orAndStart() {
            return new AndConditionProxy(this);
        }

        public static class AndConditionProxy {
            private HumanCondition humanCondition;

            private OrConditionProxy orConditionProxy;

            private AndCondition andCondition;

            private AndConditionProxy(OrConditionProxy orConditionProxy) {
                this.orConditionProxy = orConditionProxy;
                this.humanCondition = orConditionProxy.humanCondition;
                this.andCondition = new AndCondition();
            }

            public OrConditionProxy orAndEnd() {
                this.orConditionProxy.getOrCondition().addCondition(this.andCondition);
                return this.orConditionProxy;
            }

            public AndConditionProxy currentLink(Class<?> model) {
                humanCondition.currentLink(model);
                return this;
            }

            public AndConditionProxy andLink(Class<?> sourceModel, String sourceFieldName,
                                            Class<?> targetModel, String targetFieldName) {
                this.andCondition.addLink(humanCondition.genLinkFieldName(sourceModel, TextUtils.humpToLine(sourceFieldName)),
                        humanCondition.genLinkFieldName(targetModel, TextUtils.humpToLine(targetFieldName)));
                return this;
            }

            public <S, T> AndConditionProxy andLink(Class<?> sourceModel, SerializableFunction<S> sourceFieldGetter,
                                                   Class<?> targetModel, SerializableFunction<T> targetFieldGetter) {
                return this.andLink(sourceModel, BeanUtils.getBeanGetterFieldName(sourceFieldGetter),
                        targetModel, BeanUtils.getBeanGetterFieldName(targetFieldGetter));
            }

            public AndConditionProxy andLink(String sourceLinkName, String sourceFieldName,
                                            String targetLinkName, String targetFieldName) {
                this.andCondition.addLink(humanCondition.genLinkFieldName(sourceLinkName, TextUtils.humpToLine(sourceFieldName)),
                        humanCondition.genLinkFieldName(targetLinkName, TextUtils.humpToLine(targetFieldName)));
                return this;
            }

            public <S, T> AndConditionProxy andLink(String sourceLinkName, SerializableFunction<S> sourceFieldGetter,
                                                   String targetLinkName, SerializableFunction<T> targetFieldGetter) {
                return this.andLink(sourceLinkName, BeanUtils.getBeanGetterFieldName(sourceFieldGetter),
                        targetLinkName, BeanUtils.getBeanGetterFieldName(targetFieldGetter));
            }

            public AndConditionProxy andIn(String fieldName, Object[] fieldValue) {
                this.andCondition.addCondition(humanCondition.currentColumnName(fieldName), Connector.IN, fieldValue);
                return this;
            }

            public <T> AndConditionProxy andIn(SerializableFunction<T> fieldGetter, Object[] fieldValue) {
                return andIn(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
            }

            public AndConditionProxy andNotIn(String fieldName, Object[] fieldValue) {
                this.andCondition.addCondition(humanCondition.currentColumnName(fieldName), Connector.NOT_IN, fieldValue);
                return this;
            }

            public <T> AndConditionProxy andNotIn(SerializableFunction<T> fieldGetter, Object[] fieldValue) {
                return andNotIn(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
            }

            public AndConditionProxy andEqual(String fieldName, Object fieldValue) {
                this.andCondition.addCondition(humanCondition.currentColumnName(fieldName), Connector.EQUAL, fieldValue);
                return this;
            }

            public <T> AndConditionProxy andEqual(SerializableFunction<T> fieldGetter, Object fieldValue) {
                return andEqual(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
            }

            public AndConditionProxy andNotEqual(String fieldName, Object fieldValue) {
                this.andCondition.addCondition(humanCondition.currentColumnName(fieldName), Connector.NOT_EQUAL, fieldValue);
                return this;
            }

            public <T> AndConditionProxy andNotEqual(SerializableFunction<T> fieldGetter, Object fieldValue) {
                return andNotEqual(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
            }

            public AndConditionProxy andGreaterThan(String fieldName, Object fieldValue) {
                this.andCondition.addCondition(humanCondition.currentColumnName(fieldName), Connector.GREATER_THAN, fieldValue);
                return this;
            }

            public <T> AndConditionProxy andGreaterThan(SerializableFunction<T> fieldGetter, Object fieldValue) {
                return andGreaterThan(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
            }

            public AndConditionProxy andGreaterThanOrEqual(String fieldName, Object fieldValue) {
                this.andCondition.addCondition(humanCondition.currentColumnName(fieldName), Connector.GREATER_THAN_OR_EQUAL, fieldValue);
                return this;
            }

            public <T> AndConditionProxy andGreaterThanOrEqual(SerializableFunction<T> fieldGetter, Object fieldValue) {
                return andGreaterThanOrEqual(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
            }

            public AndConditionProxy andLessThan(String fieldName, Object fieldValue) {
                this.andCondition.addCondition(humanCondition.currentColumnName(fieldName), Connector.LESS_THAN, fieldValue);
                return this;
            }

            public <T> AndConditionProxy andLessThan(SerializableFunction<T> fieldGetter, Object fieldValue) {
                return andLessThan(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
            }

            public AndConditionProxy andLessThanOrEqual(String fieldName, Object fieldValue) {
                this.andCondition.addCondition(humanCondition.currentColumnName(fieldName), Connector.LESS_THAN_OR_EQUAL, fieldValue);
                return this;
            }

            public <T> AndConditionProxy andLessThanOrEqual(SerializableFunction<T> fieldGetter, Object fieldValue) {
                return andLessThanOrEqual(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
            }

            public AndConditionProxy andContains(String fieldName, Object fieldValue) {
                this.andCondition.addCondition(humanCondition.currentColumnName(fieldName), Connector.CONTAINS, fieldValue);
                return this;
            }

            public <T> AndConditionProxy andContains(SerializableFunction<T> fieldGetter, Object fieldValue) {
                return andContains(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
            }

            public AndConditionProxy andIContains(String fieldName, Object fieldValue) {
                this.andCondition.addCondition(humanCondition.currentColumnName(fieldName), Connector.I_CONTAINS, fieldValue);
                return this;
            }

            public <T> AndConditionProxy andIContains(SerializableFunction<T> fieldGetter, Object fieldValue) {
                return andIContains(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
            }

            public AndConditionProxy andStartsWith(String fieldName, Object fieldValue) {
                this.andCondition.addCondition(humanCondition.currentColumnName(fieldName), Connector.STARTS_WITH, fieldValue);
                return this;
            }

            public <T> AndConditionProxy andStartsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
                return andStartsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
            }


            public AndConditionProxy andIStartsWith(String fieldName, Object fieldValue) {
                this.andCondition.addCondition(humanCondition.currentColumnName(fieldName), Connector.I_STARTS_WITH, fieldValue);
                return this;
            }

            public <T> AndConditionProxy andIStartsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
                return andIStartsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
            }

            public AndConditionProxy andEndsWith(String fieldName, Object fieldValue) {
                this.andCondition.addCondition(humanCondition.currentColumnName(fieldName), Connector.ENDS_WITH, fieldValue);
                return this;
            }

            public <T> AndConditionProxy andEndsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
                return andEndsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
            }

            public AndConditionProxy andIEndsWith(String fieldName, Object fieldValue) {
                this.andCondition.addCondition(humanCondition.currentColumnName(fieldName), Connector.I_ENDS_WITH, fieldValue);
                return this;
            }

            public <T> AndConditionProxy andIEndsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
                return andIEndsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
            }

            public AndConditionProxy andNotContains(String fieldName, Object fieldValue) {
                this.andCondition.addCondition(humanCondition.currentColumnName(fieldName), Connector.NOT_CONTAINS, fieldValue);
                return this;
            }

            public <T> AndConditionProxy andNotContains(SerializableFunction<T> fieldGetter, Object fieldValue) {
                return andNotContains(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
            }

            public AndConditionProxy andNotIContains(String fieldName, Object fieldValue) {
                this.andCondition.addCondition(humanCondition.currentColumnName(fieldName), Connector.NOT_I_CONTAINS, fieldValue);
                return this;
            }

            public <T> AndConditionProxy andNotIContains(SerializableFunction<T> fieldGetter, Object fieldValue) {
                return andNotIContains(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
            }

            public AndConditionProxy andNotStartsWith(String fieldName, Object fieldValue) {
                this.andCondition.addCondition(humanCondition.currentColumnName(fieldName), Connector.NOT_STARTS_WITH, fieldValue);
                return this;
            }

            public <T> AndConditionProxy andNotStartsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
                return andNotStartsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
            }

            public AndConditionProxy andNotIStartsWith(String fieldName, Object fieldValue) {
                this.andCondition.addCondition(humanCondition.currentColumnName(fieldName), Connector.NOT_I_STARTS_WITH, fieldValue);
                return this;
            }

            public <T> AndConditionProxy andNotIStartsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
                return andNotIStartsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
            }

            public AndConditionProxy andNotEndsWith(String fieldName, Object fieldValue) {
                this.andCondition.addCondition(humanCondition.currentColumnName(fieldName), Connector.NOT_ENDS_WITH, fieldValue);
                return this;
            }

            public <T> AndConditionProxy andNotEndsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
                return andNotEndsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
            }

            public AndConditionProxy andNotIEndsWith(String fieldName, Object fieldValue) {
                this.andCondition.addCondition(humanCondition.currentColumnName(fieldName), Connector.NOT_I_ENDS_WITH, fieldValue);
                return this;
            }

            public <T> AndConditionProxy andNotIEndsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
                return andNotIEndsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
            }
        }
    }

    
    public OrConditionProxy filterOrStart() {
        return new OrConditionProxy(this);
    }

}
