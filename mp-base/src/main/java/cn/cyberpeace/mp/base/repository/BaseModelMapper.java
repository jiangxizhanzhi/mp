package cn.cyberpeace.mp.base.repository;

import cn.cyberpeace.mp.base.annotation.Recommendation;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.Condition;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.base.util.ModelUtils;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public interface BaseModelMapper<M extends BaseModel> {

    String pkFieldName = BeanUtils.getBeanGetterFieldName(BaseModel::getPk);

    Logger log = LoggerFactory.getLogger(BaseModelMapper.class);

    Class<?> modelClass = null;

    private static CommonMapper getCommonMapper() {
        return BeanCommon.getBean(CommonMapper.class);
    }

    private static Class<?> getMapperInterface(Proxy proxy) {
        return ClassUtils.getObjFieldValue(Proxy.getInvocationHandler(proxy),
                "mapperInterface", Class.class);
    }

    private static Object handleRawFieldValue(Class<?> fieldType, Object fieldValue) {
        if (fieldType == LocalDateTime.class && fieldValue instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) fieldValue).toLocalDateTime();
        } else if (fieldType == LocalDate.class && fieldValue instanceof java.sql.Date) {
            return ((java.sql.Date) fieldValue).toLocalDate();
        } else if (fieldType == LocalTime.class && fieldValue instanceof java.sql.Time) {
            return ((java.sql.Time) fieldValue).toLocalTime();
        } else {
            return fieldValue;
        }
    }

    private static <T> T mapToObj(Map<String, Object> map, Class<T> objClass) {
        if (map == null) {
            return null;
        }

        T obj;
        try {
            obj = objClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("Convert {} to {} instance failed: {}", map, objClass, e);
            return null;
        }

        Map<String, Class<?>> fieldTypes = ClassUtils.getBeanClassFieldTypes(objClass);
        final BeanWrapper objWrapper = new BeanWrapperImpl(obj);
        fieldTypes.forEach((fieldName, fieldType) -> {
            String mapFieldName = null;
            if (map.containsKey(fieldName)) {
                mapFieldName = fieldName;
            } else {
                String columnName = TextUtils.humpToLine(fieldName);
                if (map.containsKey(columnName)) {
                    mapFieldName = columnName;
                }
            }

            if (mapFieldName != null) {
                Object fieldValue = handleRawFieldValue(fieldType, map.get(mapFieldName));
                objWrapper.setPropertyValue(fieldName, fieldValue);
            }
        });

        return obj;
    }

    private Class<?> getMapperInterface() {
        return getMapperInterface((Proxy) this);
    }

    private String getTableName() {
        Class<M> actualModelClass = getModelClass();
        return actualModelClass != null ? ModelUtils.getTableName(actualModelClass) : null;
    }

    private String getTableName(HumanCondition humanCondition) {
        String tableName = humanCondition.toTableName();
        if (tableName == null) {
            tableName = getTableName();
        }
        return tableName;
    }

    private MappedStatement getMappedStatement(String humanName) {
        String mapperId = getMapperInterface().getName() + "." + humanName;
        MappedStatement mappedStatement;
        try {
            mappedStatement = BeanCommon.getBean(SqlSessionFactory.class).getConfiguration().getMappedStatement(
                    mapperId);
        } catch (Exception e) {
            return null;
        }

        return mappedStatement;
    }

    private String[] getSelectFields(HumanCondition humanCondition) {
        String[] selectFields = humanCondition.toSelectFields();
        if (selectFields.length == 0) {
            selectFields = humanCondition.toDefaultSelectFields(getModelClass());
            if (selectFields.length == 0) {
                selectFields = null;
            }
        }

        return selectFields;
    }

    private M mapToObj(Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        Class<M> actualModelClass = getModelClass();
        if (actualModelClass == null) {
            return null;
        }

        return mapToObj(map, actualModelClass);
    }

    private Object[] convertCreateParams(List<M> objs) {
        var allModelFields = new DynamicFields(ClassUtils.getBeanClassFields(getModelClass()));
        String[] createFields = Arrays.stream(allModelFields.getFields())
                .filter(fieldName -> !fieldName.equals(pkFieldName)).toArray(String[]::new);
        var createColumnFields = new String[createFields.length];
        for (var i = 0; i < createFields.length; i++) {
            createColumnFields[i] = TextUtils.humpToLine(createFields[i]);
        }

        var createFieldValuesList = new Object[objs.size()][createFields.length];
        for (var i = 0; i < objs.size(); i++) {
            M obj = objs.get(i);
            obj.initDefault();

            var createFieldValues = new Object[createFields.length];
            for (var j = 0; j < createFields.length; j++) {
                Object fieldValue = ClassUtils.getObjFieldValue(obj, createFields[j]);
                if (fieldValue == null) {
                    return new Object[0];
                }
                createFieldValues[j] = fieldValue;
            }
            createFieldValuesList[i] = createFieldValues;
        }

        return new Object[]{ createColumnFields, createFieldValuesList };
    }

    private Object[] convertUpdateParams(List<M> objs) {
        var sampleObj = objs.get(0);
        sampleObj.setUpdateTime(null);
        sampleObj.initDefault(BaseModel::getUpdateTime);

        var allModelFields = new DynamicFields(ClassUtils.getBeanClassFields(getModelClass()));
        String[] availableFields = Arrays.stream(allModelFields.getFields())
                .filter(fieldName -> !fieldName.equals(pkFieldName)).toArray(String[]::new);

        List<String> updateFields = new ArrayList<>();
        List<String> updateColumnFields = new ArrayList<>();
        for (String fieldName: availableFields) {
            Object fieldValue = ClassUtils.getObjFieldValue(sampleObj, fieldName);
            if (fieldValue != null) {
                updateFields.add(fieldName);
                updateColumnFields.add(TextUtils.humpToLine(fieldName));
            }
        }

        var updateFieldValuesList = new Object[objs.size()][updateFields.size()];
        for (var i = 0; i < objs.size(); i++) {
            M obj = objs.get(i);
            sampleObj.setUpdateTime(null);
            obj.initDefault(BaseModel::getUpdateTime);

            var updateFieldValues = new Object[updateFields.size()];
            for (var j = 0; j < updateFields.size(); j++) {
                Object fieldValue = ClassUtils.getObjFieldValue(obj, updateFields.get(j));
                if (fieldValue == null) {
                    return new Object[0];
                }
                updateFieldValues[j] = fieldValue;
            }
            updateFieldValuesList[i] = updateFieldValues;
        }

        return new Object[]{ updateColumnFields.toArray(new String[0]), updateFieldValuesList };
    }

    /**
     * 按有效字段分组对象
     * @param objs 数据对象
     * @return 有效字段分组对象
     */
    private Map<String, List<M>> groupValidFieldsObjs(List<M> objs) {
        Map<String, List<M>> validFieldsObjs = new HashMap<>();
        objs.forEach(obj -> {
            String key = DigestUtils.md5Hex(StringUtils.joinWith(":", BeanUtils.getNotNullPropertyNames(obj)));
            var keyObjs = validFieldsObjs.getOrDefault(key, new ArrayList<>());
            keyObjs.add(obj);
            validFieldsObjs.put(key, keyObjs);
        });
        return validFieldsObjs;
    }

    @SuppressWarnings("unchecked")
    default Class<M> getModelClass() {
        Class<?> actualModelClassSource;
        Class<?> mapperInterface = getMapperInterface();
        var modelClassAnnotation = mapperInterface.getAnnotation(ModelClass.class);
        if (modelClassAnnotation != null) {
            actualModelClassSource = modelClassAnnotation.value();
        } else {
            actualModelClassSource = ClassUtils.getStaticFieldValue(mapperInterface, "modelClass",
                    Class.class);
        }

        Class<M> actualModelClass;
        try {
            actualModelClass = (Class<M>) actualModelClassSource;
        } catch (Exception e) {
            log.error("Invalid model class[{}] for {}", actualModelClassSource, getClass());
            return null;
        }

        return actualModelClass;
    }

    @Recommendation
    default List<M> list(HumanCondition humanCondition) {
        if (humanCondition.getLimit() != null && humanCondition.getLimit() == 0) {
            return new ArrayList<>();
        }

        if (getMappedStatement("list") == null) {
            return commonList(getTableName(humanCondition), humanCondition.toCondition(), getSelectFields(humanCondition));
        } else {
            return list(getTableName(humanCondition), humanCondition.toCondition(), getSelectFields(humanCondition));
        }
    }

    /**
     * 返回原始map数据，适用于关联查询跨表结果
     */
    @Recommendation
    default List<Map<String, Object>> originList(HumanCondition humanCondition) {
        if (humanCondition.getLimit() != null && humanCondition.getLimit() == 0) {
            return new ArrayList<>();
        }

        return getCommonMapper().list(getTableName(humanCondition), humanCondition.toCondition(),
                getSelectFields(humanCondition));
    }

    /**
     * 返回自定义数据，适用于关联查询跨表结果
     */
    @Recommendation
    default <T> List<T> list(HumanCondition humanCondition, Class<T> objClass) {
        List<Map<String, Object>> mapList = originList(humanCondition);
        List<T> objs = new ArrayList<>();
        mapList.forEach(map -> {
            var obj = mapToObj(map, objClass);
            if (obj != null) {
                objs.add(obj);
            }
        });
        return objs;
    }

    @Recommendation
    default int total(HumanCondition humanCondition) {
        if (getMappedStatement("total") == null) {
            return commonTotal(getTableName(humanCondition), humanCondition.toCondition());
        } else {
            return total(getTableName(humanCondition), humanCondition.toCondition());
        }
    }

    @Recommendation
    default M first(HumanCondition humanCondition) {
        humanCondition.slice(0, 1);
        List<M> objs = list(humanCondition);
        if (objs.isEmpty()) {
            return null;
        } else {
            return objs.get(0);
        }
    }

    /**
     * 返回原始map数据，适用于关联查询跨表结果
     */
    @Recommendation
    default Map<String, Object> originFirst(HumanCondition humanCondition) {
        humanCondition.slice(0, 1);
        List<Map<String, Object>> objs = originList(humanCondition);
        if (objs.isEmpty()) {
            return null;
        } else {
            return objs.get(0);
        }
    }

    @Recommendation
    default boolean exist(HumanCondition humanCondition) {
        if (getMappedStatement("exist") == null) {
            return commonExist(getTableName(humanCondition), humanCondition.toCondition());
        } else {
            return exist(getTableName(humanCondition), humanCondition.toCondition());
        }
    }

    @Recommendation
    default M get(String id, HumanCondition humanSelectCondition) {
        Map<String, Object> map = getCommonMapper().get(getTableName(), id, getSelectFields(humanSelectCondition));
        return mapToObj(map);
    }

    @Recommendation
    default boolean create(M obj) {
        Object[] createParams = convertCreateParams(List.of(obj));
        if (createParams.length == 0) {
            log.error("Create {} has empty field", obj);
            return false;
        }

        return create((String[]) createParams[0], ((Object[][]) createParams[1])[0]);
    }

    /**
     * 更新，obj必须要有id
     */
    @Recommendation
    default boolean update(M obj) {
        Object[] updateParams = convertUpdateParams(List.of(obj));
        if (updateParams.length == 0) {
            log.error("Update {} has empty field", obj);
            return false;
        }

        return update(obj.getId(), (String[]) updateParams[0], ((Object[][]) updateParams[1])[0]);
    }

    /**
     * 保存，obj没有id则创建，有id则更新
     */
    @Recommendation
    default boolean save(M obj) {
        if (StringUtils.isEmpty(obj.getId())) {
            return create(obj);
        } else {
            return update(obj);
        }
    }

    @Recommendation
    default int bulkCreate(List<M> objs) {
        if (objs.isEmpty()) {
            return 0;
        }

        Object[] createParams = convertCreateParams(objs);
        if (createParams.length == 0) {
            log.error("Bulk create {} has empty field", objs);
            return 0;
        }

        return bulkCreate((String[]) createParams[0], (Object[][]) createParams[1]);
    }

    /**
     * 批量更新，所有objs必须要有id, 更新数据字段必须一致
     */
    @Recommendation
    default int bulkUpdate(List<M> objs) {
        if (objs.isEmpty()) {
            return 0;
        }

        var successCount = 0;
        for (var validFieldsObjs: groupValidFieldsObjs(objs).values()) {
            Object[] updateParams = convertUpdateParams(validFieldsObjs);
            if (updateParams.length == 0) {
                log.error("Service bulk update {} has empty field", objs);
                continue;
            }
            successCount += bulkUpdate((String[]) updateParams[0], (Object[][]) updateParams[1]);
        }

        return successCount;
    }

    default List<M> commonList(String tableName, @Param("condition") Condition condition, String[] selectFields) {
        List<Map<String, Object>> mapList = getCommonMapper().list(tableName, condition, selectFields);
        List<M> objs = new ArrayList<>();
        mapList.forEach(map -> {
            var obj = mapToObj(map);
            if (obj != null) {
                objs.add(obj);
            }
        });
        return objs;
    }

    default int commonTotal(String tableName, @Param("condition") Condition condition) {
        return getCommonMapper().total(tableName, condition);
    }

    default boolean commonExist(String tableName, @Param("condition") Condition condition) {
        return getCommonMapper().exist(tableName, condition);
    }

    List<M> list(@Param("condition") Condition condition, String[] selectFields);
    int total(@Param("condition") Condition condition);
    boolean exist(@Param("condition") Condition condition);

    @Recommendation
    default M get(String id) {
        return get(id, new HumanCondition());
    }

    @Recommendation
    default M logicGet(String id) {
        var obj = get(id);
        return (obj != null && obj.hasDeleted()) ? null : obj;
    }

    default boolean create(String[] createFields, Object[] createFieldValues) {
        return getCommonMapper().create(getTableName(), createFields, createFieldValues);
    }

    default boolean update(String id, String[] updateFields, Object[] updateFieldValues) {
        return getCommonMapper().update(getTableName(), id, updateFields, updateFieldValues);
    }

    @Recommendation
    default boolean delete(String id) {
        return getCommonMapper().delete(getTableName(), id);
    }

    @Recommendation
    default boolean logicDelete(String id) {
        Class<M> actualModelClass = getModelClass();
        if (actualModelClass == null) {
            return false;
        }

        M obj;
        try {
            obj = actualModelClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("Create model obj by {} failed: {}", actualModelClass, e);
            return false;
        }
        obj.setId(id);
        if (obj.prepareDeleteFlag()) {
            return update(obj);
        } else {
            return delete(id);
        }
    }

    default int bulkCreate(String[] createFields, Object[][] createFieldValuesList) {
        return getCommonMapper().bulkCreate(getTableName(), createFields, createFieldValuesList);
    }

    default int bulkUpdate(String[] updateFields, Object[][] updateFieldValuesList) {
        return getCommonMapper().bulkUpdate(getTableName(), updateFields, updateFieldValuesList);
    }

    @Recommendation
    default int bulkDelete(String[] ids) {
        if (ids.length == 0) {
            return 0;
        }

        return getCommonMapper().bulkDelete(getTableName(), ids);
    }

    @Recommendation
    default int bulkLogicDelete(String[] ids) {
        Class<M> actualModelClass = getModelClass();
        if (actualModelClass == null) {
            return 0;
        }

        List<M> logicObjs = new ArrayList<>();
        List<String> rawIds = new ArrayList<>();
        try {
            for (var id: ids) {
                M obj = actualModelClass.getDeclaredConstructor().newInstance();
                obj.setId(id);
                if (obj.prepareDeleteFlag()) {
                    logicObjs.add(obj);
                } else {
                    rawIds.add(id);
                }
            }
        } catch (Exception e) {
            log.error("Bulk logic delete {} on model {} failed: {}", ids, actualModelClass, e);
            return 0;
        }

        var successCount = 0;
        if (!logicObjs.isEmpty()) {
            successCount += bulkUpdate(logicObjs);
        }
        if (!rawIds.isEmpty()) {
            successCount += bulkDelete(rawIds.toArray(new String[0]));
        }

        return successCount;
    }

    /**
     * 条件更新
     * @param obj 需要更新的数据
     * @param humanCondition 更新条件
     * @return 更新数量
     */
    @Recommendation
    default int filterUpdate(M obj, HumanCondition humanCondition) {
        Object[] updateParams = convertUpdateParams(List.of(obj));
        return getCommonMapper().filterUpdate(getTableName(humanCondition), (String[]) updateParams[0],
                ((Object[][]) updateParams[1])[0], humanCondition.toCondition());
    }

    /**
     * 条件删除
     * @param humanCondition 删除条件
     * @return 删除数量
     */
    @Recommendation
    default int filterDelete(HumanCondition humanCondition) {
        return getCommonMapper().filterDelete(getTableName(humanCondition), humanCondition.toCondition());
    }

    List<M> list(String table, @Param("condition") Condition condition, String[] selectFields);
    int total(String table, @Param("condition") Condition condition);
    boolean exist(String table, @Param("condition") Condition condition);
    M get(String table, String id, String[] selectFields);
    boolean create(String table, String[] createFields, Object[] createFieldValues);
    boolean update(String table, String id, String[] updateFields, Object[] updateFieldValues);
    boolean delete(String table, String id);
    int bulkCreate(String table, String[] createFields, Object[][] createFieldValuesList);
    int bulkUpdate(String table, String[] updateFields, Object[][] updateFieldValuesList);
    int bulkDelete(String table, String[] ids);
    int filterUpdate(String table, String[] updateFields, Object[] updateFieldValues,
                     @Param("condition") Condition condition);
    int filterDelete(String table, @Param("condition") Condition condition);
}
