package cn.cyberpeace.mp.base.repository.condition;

import cn.cyberpeace.mp.base.repository.condition.aggregation.Aggregation;
import cn.cyberpeace.mp.base.util.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 查询条件构造器
 * 单表查询例：
 * new HumanCondition().filterEqual(Model::getType, 1)
 *                     .filterContains(Model::getName, "test")
 *                     .filterOrStart()
 *                     .orEqual(Model::getTestType, 1)
 *                     .orEqual(Model::getTestType, 2)
 *                     .filterOrEnd()
 *                     .select(Model::getId, Model::getName);
 *
 * 多表查询例：
 * new HumanCondition().link(Model1.class, Model2.class)
 *                     .filterLink(Model1.class, Model1::getM2Id, Model2.class, Model2::getId)
 *                     .currentLink(Model2.class)
 *                     .filterEqual(Model2::getType, 1)
 *                     .filterContains(Model2::getName, "test")
 *                     .currentLink(Model1.class)
 *                     .select(Model1::getId, Model1::getName);
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HumanCondition {
    private final Map<String, String> selectFields = new HashMap<>();
    private Integer limit;
    private Integer offset;
    private final List<String> groupFields = new ArrayList<>();
    private final List<String> orders = new ArrayList<>();
    private final AndCondition andCondition = new AndCondition();
    private CustomCondition customCondition;
    /** 简单关联的表别名和对应model */
    private final Map<String, Class<?>> linkedModels = new LinkedHashMap<>();
    /** 简单关联的model和对应表别名，自关联情况不要使用 */
    private final Map<Class<?>, String> linkedModelNames = new LinkedHashMap<>();
    /** 当前表别名，设置后构造条件对应于该关联表 */
    private String currentLinkName;

    public String genLinkFieldName(String linkName, String fieldName) {
        return StringUtils.joinWith(".",linkName, fieldName);
    }

    public String genLinkFieldName(Class<?> model, String fieldName) {
        String linkName = linkedModelNames.get(model);
        return genLinkFieldName(linkName, fieldName);
    }

    public String currentFieldName(String fieldName) {
        if (currentLinkName == null) {
            return fieldName;
        } else {
            return genLinkFieldName(currentLinkName, fieldName);
        }
    }

    public String currentColumnName(String fieldName) {
        return currentFieldName(TextUtils.humpToLine(fieldName));
    }

    /**
     * 转换成sql需要的select字段
     * @return select字段
     */
    public String[] toSelectFields() {
        List<String> selectFieldTexts = new ArrayList<>();
        selectFields.forEach((fieldName, outputFieldName) -> {
            if (StringUtils.isEmpty(outputFieldName)) {
                selectFieldTexts.add(TextUtils.humpToLine(fieldName));
            } else {
                selectFieldTexts.add(StringUtils.joinWith(" ", TextUtils.humpToLine(fieldName), outputFieldName));
            }
        });
        return selectFieldTexts.toArray(new String[0]);
    }

    /**
     * 转换成对应model的默认select字段，默认全部字段
     * @param model 数据model
     * @return select字段
     */
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

    /**
     * 转换成mapper识别的condition对象
     * @return mapper识别的condition对象
     */
    public Condition toCondition() {
        var condition = new Condition();
        condition.setPagination(new PaginationCondition(limit, offset));
        condition.setGroup(new GroupCondition(TextUtils.humpToLine(groupFields.toArray(new String[0]))));
        condition.setOrder(new OrderCondition(TextUtils.humpToLine(orders.toArray(new String[0]))));
        condition.setAndi(andCondition);
        condition.setCustom(customCondition);
        return condition;
    }

    /**
     * 转换对应跟随FROM语句的表名，表关联情况下形如 table1 m0, table2 m1, table3 m2
     * @return 表名
     */
    public String toTableName() {
        if (linkedModels.isEmpty()) {
            return null;
        }
        List<String> tableNames = new ArrayList<>();
        linkedModels.forEach((linkName, model) -> tableNames.add(
                StringUtils.joinWith(" ", ModelUtils.getTableName(model), linkName)));
        return StringUtils.joinWith(", ", tableNames.toArray());
    }

    /**
     * 添加关联表
     * @param model 数据model
     * @param linkName 表别名
     * @return this
     */
    public HumanCondition linkAs(Class<?> model, String linkName) {
        linkedModels.put(linkName, model);
        linkedModelNames.put(model, linkName);
        currentLinkName = linkName;
        return this;
    }

    /**
     * 添加关联表, 别名默认如：m0, m1, m2
     * @param models 数据model...
     * @return this
     */
    public HumanCondition link(Class<?>... models) {
        for (Class<?> model: models) {
            String linkName = "m" + linkedModels.size();
            linkAs(model, linkName);
        }
        return this;
    }

    /**
     * 指定当前表，后续条件基于此表
     * @param linkName 表关联别名
     * @return this
     */
    public HumanCondition currentLink(String linkName) {
        currentLinkName = linkName;
        return this;
    }

    /**
     * 指定当前表，后续条件基于此表
     * @param model 数据model
     * @return this
     */
    public HumanCondition currentLink(Class<?> model) {
        String linkName = linkedModelNames.get(model);
        return currentLink(linkName);
    }

    /**
     * select字段并设置别名
     * @param fieldName 字段名称
     * @param outputFieldName 返回别名
     * @return this
     */
    public HumanCondition selectAs(String fieldName, String outputFieldName) {
        selectFields.put(currentFieldName(fieldName), outputFieldName);
        return this;
    }

    /**
     * select字段并设置别名
     * @param fieldGetter 字段get方法引用
     * @param outputFieldName 返回别名
     * @return this
     */
    public <T> HumanCondition selectAs(SerializableFunction<T> fieldGetter, String outputFieldName) {
        return selectAs(BeanUtils.getBeanGetterFieldName(fieldGetter), outputFieldName);
    }

    /**
     * select字段不设置别名
     * @param fieldNames 字段名称...
     * @return this
     */
    public HumanCondition select(String... fieldNames) {
        for (String fieldName: fieldNames) {
            selectFields.put(currentFieldName(fieldName), null);
        }
        return this;
    }

    /**
     * select字段不设置别名
     * @param fieldGetters 字段get方法引用...
     * @return this
     */
    public <T> HumanCondition select(SerializableFunction<T>... fieldGetters) {
        return select(Arrays.stream(fieldGetters).map(BeanUtils::getBeanGetterFieldName).toArray(String[]::new));
    }

    /**
     * select聚合字段并设置别名
     * @param aggregation 聚合字段
     * @return this
     */
    public HumanCondition selectAs(Aggregation aggregation, String outputFieldName) {
        selectFields.put(aggregation.toSqlField(currentFieldName(aggregation.getFieldName())), outputFieldName);
        return this;
    }

    /**
     * select聚合字段不设置别名
     * @param aggregations 聚合字段...
     * @return this
     */
    public HumanCondition select(Aggregation... aggregations) {
        for (Aggregation aggregation: aggregations) {
            String fieldName = aggregation.getFieldName();
            String funcName = aggregation.getFuncName();
            String outputFieldNameSuffix = funcName.substring(0, 1).toUpperCase() + funcName.substring(1);
            var outputFieldName = StringUtils.join(fieldName, outputFieldNameSuffix);
            selectFields.put(aggregation.toSqlField(currentFieldName(fieldName)), outputFieldName);
        }
        return this;
    }

    /**
     * 分页
     * @param page 页码
     * @param pageSize 每页数量
     * @return this
     */
    public HumanCondition pagination(int page, int pageSize) {
        if (pageSize >= 0) {
            limit = pageSize;
        } else {
            limit = null;
            offset = null;
            return this;
        }

        if (page > 0) {
            offset = (page - 1) * limit;
        } else {
            offset = null;
        }

        return this;
    }

    /**
     * 切片
     * @param startIndex 开始下标
     * @param endIndex 结束下标
     * @return this
     */
    public HumanCondition slice(int startIndex, int endIndex) {
        offset = Math.max(startIndex, 0);
        limit = Math.max(endIndex - offset, 0);
        return this;
    }

    /**
     * 分组
     * @param fieldNames 字段名称...
     * @return this
     */
    public HumanCondition groupBy(String... fieldNames) {
        for (String fieldName: fieldNames) {
            groupFields.add(currentFieldName(fieldName));
        }
        return this;
    }

    /**
     * 分组
     * @param fieldGetters 字段get方法引用...
     * @param <T> T
     * @return this
     */
    public <T> HumanCondition groupBy(SerializableFunction<T>... fieldGetters) {
        return groupBy(Arrays.stream(fieldGetters).map(BeanUtils::getBeanGetterFieldName).toArray(String[]::new));
    }

    /**
     * 排序
     * @param orders 排序字段
     *               如：field1, -field2
     * @return this
     */
    public HumanCondition orderBy(String... orders) {
        for (String order: orders) {
            String linkedOrder;
            if (order.startsWith("-")) {
                linkedOrder = "-" + currentFieldName(order.substring(1));
            } else {
                linkedOrder = currentFieldName(order);
            }
            this.orders.add(linkedOrder);
        }
        return this;
    }

    /**
     * 排序
     * @param fieldGetter 字段get方法引用
     * @param reverse 是否逆序
     * @param <T> T
     * @return this
     */
    public <T> HumanCondition orderBy(SerializableFunction<T> fieldGetter, boolean reverse) {
        String fieldName = BeanUtils.getBeanGetterFieldName(fieldGetter);
        return orderBy(reverse ? ("-" + fieldName) : fieldName);
    }

    /**
     * 正排序
     * @param fieldGetter 字段get方法引用
     * @param <T> T
     * @return this
     */
    public <T> HumanCondition orderBy(SerializableFunction<T> fieldGetter) {
        return orderBy(fieldGetter, false);
    }

    /**
     * 逆排序
     * @param fieldGetter 字段get方法引用
     * @param <T> T
     * @return this
     */
    public <T> HumanCondition reverseOrderBy(SerializableFunction<T> fieldGetter) {
        return orderBy(fieldGetter, true);
    }

    /**
     * 添加表关联条件，sql形如：source.targetId = target.id
     * @param sourceModel 源数据model
     * @param sourceFieldName 源表对应字段
     * @param targetModel 目标数据model
     * @param targetFieldName 目标表对应字段
     * @return this
     */
    public HumanCondition filterLink(Class<?> sourceModel, String sourceFieldName,
                                     Class<?> targetModel, String targetFieldName) {
        andCondition.addLink(genLinkFieldName(sourceModel, TextUtils.humpToLine(sourceFieldName)),
                genLinkFieldName(targetModel, TextUtils.humpToLine(targetFieldName)));
        return this;
    }

    /**
     * 添加表关联条件，sql形如：source.targetId = target.id
     * @param sourceModel 源数据model
     * @param sourceFieldGetter 源表对应字段get方法引用
     * @param targetModel 目标数据model
     * @param targetFieldGetter 目标表对应字段get方法引用
     * @param <S> S
     * @param <T> T
     * @return this
     */
    public <S, T> HumanCondition filterLink(Class<?> sourceModel, SerializableFunction<S> sourceFieldGetter,
                                         Class<?> targetModel, SerializableFunction<T> targetFieldGetter) {
        return filterLink(sourceModel, BeanUtils.getBeanGetterFieldName(sourceFieldGetter),
                targetModel, BeanUtils.getBeanGetterFieldName(targetFieldGetter));
    }

    /**
     * 添加表关联条件，sql形如：source.targetId = target.id
     * @param sourceLinkName 源表别名
     * @param sourceFieldName 源表对应字段
     * @param targetLinkName 目标表别名
     * @param targetFieldName 目标表对应字段
     * @return this
     */
    public HumanCondition filterLink(String sourceLinkName, String sourceFieldName,
                                     String targetLinkName, String targetFieldName) {
        andCondition.addLink(genLinkFieldName(sourceLinkName, TextUtils.humpToLine(sourceFieldName)),
                genLinkFieldName(targetLinkName, TextUtils.humpToLine(targetFieldName)));
        return this;
    }

    /**
     * 添加表关联条件，sql形如：source.targetId = target.id
     * @param sourceLinkName 源表别名
     * @param sourceFieldGetter 源表对应字段get方法引用
     * @param targetLinkName 目标表别名
     * @param targetFieldGetter 目标表对应字段get方法引用
     * @return this
     */
    public <S, T> HumanCondition filterLink(String sourceLinkName, SerializableFunction<S> sourceFieldGetter,
                                         String targetLinkName, SerializableFunction<T> targetFieldGetter) {
        return filterLink(sourceLinkName, BeanUtils.getBeanGetterFieldName(sourceFieldGetter),
                targetLinkName, BeanUtils.getBeanGetterFieldName(targetFieldGetter));
    }

    /**
     * 添加IN条件，sql形如 field1 IN (1, 2)
     * @param fieldName 字段名称
     * @param fieldValue IN条件可选值
     * @return this
     */
    public HumanCondition filterIn(String fieldName, Object[] fieldValue) {
        andCondition.addIn(currentColumnName(fieldName), fieldValue);
        return this;
    }

    /**
     * 添加IN条件，sql形如 field1 IN (1, 2)
     * @param fieldGetter 字段名称方法引用
     * @param fieldValue IN条件可选值
     * @return this
     */
    public <T> HumanCondition filterIn(SerializableFunction<T> fieldGetter, Object[] fieldValue) {
        return filterIn(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    /**
     * 添加NOT IN条件，sql形如 field1 NOT IN (1, 2)
     * @param fieldName 字段名称
     * @param fieldValue NOT IN条件可选值
     * @return this
     */
    public HumanCondition filterNotIn(String fieldName, Object[] fieldValue) {
        andCondition.addNotIn(currentColumnName(fieldName), fieldValue);
        return this;
    }

    /**
     * 添加NOT IN条件，sql形如 field1 IN (1, 2)
     * @param fieldGetter 字段名称方法引用
     * @param fieldValue NOT IN条件可选值
     * @return this
     */
    public <T> HumanCondition filterNotIn(SerializableFunction<T> fieldGetter, Object[] fieldValue) {
        return filterNotIn(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    /**
     * 添加等于条件，sql形如 field1 = 1
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     * @return this
     */
    public HumanCondition filterEqual(String fieldName, Object fieldValue) {
        andCondition.addEqual(currentColumnName(fieldName), fieldValue);
        return this;
    }

    /**
     * 添加等于条件，sql形如 field1 = 1
     * @param fieldGetter 字段名称方法引用
     * @param fieldValue 字段值
     * @return this
     */
    public <T> HumanCondition filterEqual(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterEqual(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    /**
     * 添加不等于条件，sql形如 field1 <> 1
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     * @return this
     */
    public HumanCondition filterNotEqual(String fieldName, Object fieldValue) {
        andCondition.addNotEqual(currentColumnName(fieldName), fieldValue);
        return this;
    }

    /**
     * 添加不等于条件，sql形如 field1 <> 1
     * @param fieldGetter 字段名称方法引用
     * @param fieldValue 字段值
     * @return this
     */
    public <T> HumanCondition filterNotEqual(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterNotEqual(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    /**
     * 添加大于条件，sql形如 field1 > 1
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     * @return this
     */
    public HumanCondition filterGreaterThan(String fieldName, Object fieldValue) {
        andCondition.addGreaterThan(currentColumnName(fieldName), fieldValue);
        return this;
    }

    /**
     * 添加大于条件，sql形如 field1 > 1
     * @param fieldGetter 字段名称方法引用
     * @param fieldValue 字段值
     * @return this
     */
    public <T> HumanCondition filterGreaterThan(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterGreaterThan(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    /**
     * 添加大于等于条件，sql形如 field1 >= 1
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     * @return this
     */
    public HumanCondition filterGreaterThanOrEqual(String fieldName, Object fieldValue) {
        andCondition.addGreaterThanOrEqual(currentColumnName(fieldName), fieldValue);
        return this;
    }

    /**
     * 添加大于等于条件，sql形如 field1 >= 1
     * @param fieldGetter 字段名称方法引用
     * @param fieldValue 字段值
     * @return this
     */
    public <T> HumanCondition filterGreaterThanOrEqual(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterGreaterThanOrEqual(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    /**
     * 添加小于条件，sql形如 field1 < 1
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     * @return this
     */
    public HumanCondition filterLessThan(String fieldName, Object fieldValue) {
        andCondition.addLessThan(currentColumnName(fieldName), fieldValue);
        return this;
    }

    /**
     * 添加小于条件，sql形如 field1 < 1
     * @param fieldGetter 字段名称方法引用
     * @param fieldValue 字段值
     * @return this
     */
    public <T> HumanCondition filterLessThan(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterLessThan(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    /**
     * 添加小于等于条件，sql形如 field1 <= 1
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     * @return this
     */
    public HumanCondition filterLessThanOrEqual(String fieldName, Object fieldValue) {
        andCondition.addLessThanOrEqual(currentColumnName(fieldName), fieldValue);
        return this;
    }

    /**
     * 添加小于等于条件，sql形如 field1 <= 1
     * @param fieldGetter 字段名称方法引用
     * @param fieldValue 字段值
     * @return this
     */
    public <T> HumanCondition filterLessThanOrEqual(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterLessThanOrEqual(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    /**
     * 添加包含条件，sql形如 field1 LIKE BINARY "%value%"
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     * @return this
     */
    public HumanCondition filterContains(String fieldName, Object fieldValue) {
        andCondition.addContains(currentColumnName(fieldName), fieldValue);
        return this;
    }

    /**
     * 添加包含条件，sql形如 field1 LIKE BINARY "%value%"
     * @param fieldGetter 字段名称方法引用
     * @param fieldValue 字段值
     * @return this
     */
    public <T> HumanCondition filterContains(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterContains(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    /**
     * 添加包含条件，sql形如 field1 LIKE "%value%"
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     * @return this
     */
    public HumanCondition filterIContains(String fieldName, Object fieldValue) {
        andCondition.addIContains(currentColumnName(fieldName), fieldValue);
        return this;
    }

    /**
     * 添加包含条件，sql形如 field1 LIKE "%value%"
     * @param fieldGetter 字段名称方法引用
     * @param fieldValue 字段值
     * @return this
     */
    public <T> HumanCondition filterIContains(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterIContains(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    /**
     * 添加开始匹配条件，sql形如 field1 LIKE BINARY "value%"
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     * @return this
     */
    public HumanCondition filterStartsWith(String fieldName, Object fieldValue) {
        andCondition.addStartsWith(currentColumnName(fieldName), fieldValue);
        return this;
    }

    /**
     * 添加开始匹配条件，sql形如 field1 LIKE BINARY "value%"
     * @param fieldGetter 字段名称方法引用
     * @param fieldValue 字段值
     * @return this
     */
    public <T> HumanCondition filterStartsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterStartsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    /**
     * 添加开始匹配条件，sql形如 field1 LIKE "value%"
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     * @return this
     */
    public HumanCondition filterIStartsWith(String fieldName, Object fieldValue) {
        andCondition.addIStartsWith(currentColumnName(fieldName), fieldValue);
        return this;
    }

    /**
     * 添加开始匹配条件，sql形如 field1 LIKE "value%"
     * @param fieldGetter 字段名称方法引用
     * @param fieldValue 字段值
     * @return this
     */
    public <T> HumanCondition filterIStartsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterIStartsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    /**
     * 添加结束匹配条件，sql形如 field1 LIKE BINARY "%value"
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     * @return this
     */
    public HumanCondition filterEndsWith(String fieldName, Object fieldValue) {
        andCondition.addEndsWith(currentColumnName(fieldName), fieldValue);
        return this;
    }

    /**
     * 添加结束匹配条件，sql形如 field1 LIKE BINARY "%value"
     * @param fieldGetter 字段名称方法引用
     * @param fieldValue 字段值
     * @return this
     */
    public <T> HumanCondition filterEndsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterEndsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    /**
     * 添加结束匹配条件，sql形如 field1 LIKE "%value"
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     * @return this
     */
    public HumanCondition filterIEndsWith(String fieldName, Object fieldValue) {
        andCondition.addIEndsWith(currentColumnName(fieldName), fieldValue);
        return this;
    }

    /**
     * 添加结束匹配条件，sql形如 field1 LIKE "%value"
     * @param fieldGetter 字段名称方法引用
     * @param fieldValue 字段值
     * @return this
     */
    public <T> HumanCondition filterIEndsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterIEndsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    /**
     * 添加不包含条件，sql形如 field1 NOT LIKE BINARY "%value%"
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     * @return this
     */
    public HumanCondition filterNotContains(String fieldName, Object fieldValue) {
        andCondition.addNotContains(currentColumnName(fieldName), fieldValue);
        return this;
    }

    /**
     * 添加不包含条件，sql形如 field1 NOT LIKE BINARY "%value%"
     * @param fieldGetter 字段名称方法引用
     * @param fieldValue 字段值
     * @return this
     */
    public <T> HumanCondition filterNotContains(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterNotContains(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    /**
     * 添加不包含条件，sql形如 field1 NOT LIKE "%value%"
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     * @return this
     */
    public HumanCondition filterNotIContains(String fieldName, Object fieldValue) {
        andCondition.addNotIContains(currentColumnName(fieldName), fieldValue);
        return this;
    }

    /**
     * 添加不包含条件，sql形如 field1 NOT LIKE "%value%"
     * @param fieldGetter 字段名称方法引用
     * @param fieldValue 字段值
     * @return this
     */
    public <T> HumanCondition filterNotIContains(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterNotIContains(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    /**
     * 添加开始不匹配条件，sql形如 field1 NOT LIKE BINARY "value%"
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     * @return this
     */
    public HumanCondition filterNotStartsWith(String fieldName, Object fieldValue) {
        andCondition.addNotStartsWith(currentColumnName(fieldName), fieldValue);
        return this;
    }

    /**
     * 添加开始不匹配条件，sql形如 field1 NOT LIKE BINARY "value%"
     * @param fieldGetter 字段名称方法引用
     * @param fieldValue 字段值
     * @return this
     */
    public <T> HumanCondition filterNotStartsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterNotStartsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    /**
     * 添加开始不匹配条件，sql形如 field1 NOT LIKE "value%"
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     * @return this
     */
    public HumanCondition filterNotIStartsWith(String fieldName, Object fieldValue) {
        andCondition.addNotIStartsWith(currentColumnName(fieldName), fieldValue);
        return this;
    }

    /**
     * 添加开始不匹配条件，sql形如 field1 NOT LIKE "value%"
     * @param fieldGetter 字段名称方法引用
     * @param fieldValue 字段值
     * @return this
     */
    public <T> HumanCondition filterNotIStartsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterNotIStartsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    /**
     * 添加结束不匹配条件，sql形如 field1 NOT LIKE BINARY "%value"
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     * @return this
     */
    public HumanCondition filterNotEndsWith(String fieldName, Object fieldValue) {
        andCondition.addNotEndsWith(currentColumnName(fieldName), fieldValue);
        return this;
    }

    /**
     * 添加结束不匹配条件，sql形如 field1 NOT LIKE BINARY "%value"
     * @param fieldGetter 字段名称方法引用
     * @param fieldValue 字段值
     * @return this
     */
    public <T> HumanCondition filterNotEndsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterNotEndsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    /**
     * 添加结束不匹配条件，sql形如 field1 NOT LIKE "%value"
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     * @return this
     */
    public HumanCondition filterNotIEndsWith(String fieldName, Object fieldValue) {
        andCondition.addNotIEndsWith(currentColumnName(fieldName), fieldValue);
        return this;
    }

    /**
     * 添加结束不匹配条件，sql形如 field1 NOT LIKE "%value"
     * @param fieldGetter 字段名称方法引用
     * @param fieldValue 字段值
     * @return this
     */
    public <T> HumanCondition filterNotIEndsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
        return filterNotIEndsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrConditionPoxy {

        private HumanCondition humanCondition;

        private final OrCondition orCondition = new OrCondition();

        public OrConditionPoxy orLink(Class<?> sourceModel, String sourceFieldName,
                                      Class<?> targetModel, String targetFieldName) {
            orCondition.addLink(humanCondition.genLinkFieldName(sourceModel, TextUtils.humpToLine(sourceFieldName)),
                    humanCondition.genLinkFieldName(targetModel, TextUtils.humpToLine(targetFieldName)));
            return this;
        }

        public <S, T> OrConditionPoxy orLink(Class<?> sourceModel, SerializableFunction<S> sourceFieldGetter,
                                          Class<?> targetModel, SerializableFunction<T> targetFieldGetter) {
            return orLink(sourceModel, BeanUtils.getBeanGetterFieldName(sourceFieldGetter),
                    targetModel, BeanUtils.getBeanGetterFieldName(targetFieldGetter));
        }

        public OrConditionPoxy orLink(String sourceLinkName, String sourceFieldName,
                                      String targetLinkName, String targetFieldName) {
            orCondition.addLink(humanCondition.genLinkFieldName(sourceLinkName, TextUtils.humpToLine(sourceFieldName)),
                    humanCondition.genLinkFieldName(targetLinkName, TextUtils.humpToLine(targetFieldName)));
            return this;
        }

        public <S, T> OrConditionPoxy orLink(String sourceLinkName, SerializableFunction<S> sourceFieldGetter,
                                          String targetLinkName, SerializableFunction<T> targetFieldGetter) {
            return orLink(sourceLinkName, BeanUtils.getBeanGetterFieldName(sourceFieldGetter),
                    targetLinkName, BeanUtils.getBeanGetterFieldName(targetFieldGetter));
        }

        public OrConditionPoxy orIn(String fieldName, Object[] fieldValue) {
            orCondition.addIn(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionPoxy orIn(SerializableFunction<T> fieldGetter, Object[] fieldValue) {
            return orIn(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionPoxy orNotIn(String fieldName, Object[] fieldValue) {
            orCondition.addNotIn(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionPoxy orNotIn(SerializableFunction<T> fieldGetter, Object[] fieldValue) {
            return orNotIn(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionPoxy orEqual(String fieldName, Object fieldValue) {
            orCondition.addEqual(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionPoxy orEqual(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orEqual(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionPoxy orNotEqual(String fieldName, Object fieldValue) {
            orCondition.addNotEqual(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionPoxy orNotEqual(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orNotEqual(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionPoxy orGreaterThan(String fieldName, Object fieldValue) {
            orCondition.addGreaterThan(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionPoxy orGreaterThan(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orGreaterThan(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionPoxy orGreaterThanOrEqual(String fieldName, Object fieldValue) {
            orCondition.addGreaterThanOrEqual(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionPoxy orGreaterThanOrEqual(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orGreaterThanOrEqual(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionPoxy orLessThan(String fieldName, Object fieldValue) {
            orCondition.addLessThan(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionPoxy orLessThan(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orLessThan(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionPoxy orLessThanOrEqual(String fieldName, Object fieldValue) {
            orCondition.addLessThanOrEqual(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionPoxy orLessThanOrEqual(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orLessThanOrEqual(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionPoxy orContains(String fieldName, Object fieldValue) {
            orCondition.addContains(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionPoxy orContains(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orContains(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionPoxy orIContains(String fieldName, Object fieldValue) {
            orCondition.addIContains(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionPoxy orIContains(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orIContains(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionPoxy orStartsWith(String fieldName, Object fieldValue) {
            orCondition.addStartsWith(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionPoxy orStartsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orStartsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionPoxy orIStartsWith(String fieldName, Object fieldValue) {
            orCondition.addIStartsWith(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionPoxy orIStartsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orIStartsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionPoxy orEndsWith(String fieldName, Object fieldValue) {
            orCondition.addEndsWith(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionPoxy orEndsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orEndsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionPoxy orIEndsWith(String fieldName, Object fieldValue) {
            orCondition.addIEndsWith(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionPoxy orIEndsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orIEndsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionPoxy orNotContains(String fieldName, Object fieldValue) {
            orCondition.addNotContains(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionPoxy orNotContains(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orNotContains(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionPoxy orNotIContains(String fieldName, Object fieldValue) {
            orCondition.addNotIContains(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionPoxy orNotIContains(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orNotIContains(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionPoxy orNotStartsWith(String fieldName, Object fieldValue) {
            orCondition.addNotStartsWith(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionPoxy orNotStartsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orNotStartsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionPoxy orNotIStartsWith(String fieldName, Object fieldValue) {
            orCondition.addNotIStartsWith(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionPoxy orNotIStartsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orNotIStartsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionPoxy orNotEndsWith(String fieldName, Object fieldValue) {
            orCondition.addNotEndsWith(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionPoxy orNotEndsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orNotEndsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        public OrConditionPoxy orNotIEndsWith(String fieldName, Object fieldValue) {
            orCondition.addNotIEndsWith(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public <T> OrConditionPoxy orNotIEndsWith(SerializableFunction<T> fieldGetter, Object fieldValue) {
            return orNotIEndsWith(BeanUtils.getBeanGetterFieldName(fieldGetter), fieldValue);
        }

        /**
         * 结束组合OR条件作为一个AND条件，sql形如 ...AND (id = 1 OR id = 2) AND...
         * 例：
         * humanCondition.filterOrStart()
         *               .orEqual(Model::getId, 1)
         *               .orEqual(Model::getId, 2)
         *               .filterOrEnd()
         * @return HumanCondition
         */
        public HumanCondition filterOrEnd() {
            humanCondition.getAndCondition().addCondition(orCondition);
            return humanCondition;
        }
    }

    /**
     * 开始组合OR条件作为一个AND条件，sql形如 ...AND (id = 1 OR id = 2) AND...
     * 例：
     * humanCondition.filterOrStart()
     *               .orEqual(Model::getId, 1)
     *               .orEqual(Model::getId, 2)
     *               .filterOrEnd()
     * @return this
     */
    public OrConditionPoxy filterOrStart() {
        return new OrConditionPoxy(this);
    }

}
