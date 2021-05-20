package cn.cyberpeace.mp.base.repository;

import cn.cyberpeace.mp.base.annotation.Recommendation;
import cn.cyberpeace.mp.base.constant.BaseFieldName;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.Condition;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.base.util.ModelUtils;
import cn.cyberpeace.mp.base.util.TextUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface BaseModelMapper<M extends BaseModel> {

    Logger log = LoggerFactory.getLogger(BaseModelMapper.class);

    Class<?> modelClass = null;

    private static CommonMapper getCommonMapper() {
        return BeanUtils.getBean(CommonMapper.class);
    }

    private static Class<?> getMapperInterface(Proxy proxy) {
        return ClassUtils.getObjFieldValue(Proxy.getInvocationHandler(proxy),
                "mapperInterface", Class.class);
    }

    private Class<?> getMapperInterface() {
        return getMapperInterface((Proxy) this);
    }

    private Class<M> getModelClass() {
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

    private String getTableName() {
        Class<M> actualModelClass = getModelClass();
        return actualModelClass != null ? ModelUtils.getTableName(actualModelClass) : null;
    }

    private MappedStatement getMappedStatement(String humanName) {
        String mapperId = getMapperInterface().getName() + "." + humanName;
        MappedStatement mappedStatement;
        try {
            mappedStatement = BeanUtils.getBean(SqlSessionFactory.class).getConfiguration().getMappedStatement(
                    mapperId);
        } catch (Exception e) {
            return null;
        }

        return mappedStatement;
    }

    private M mapToObj(Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        Class<M> actualModelClass = getModelClass();
        if (actualModelClass == null) {
            return null;
        }

        M obj;
        try {
            obj = actualModelClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("Convert {} to {} instance failed: {}", map, actualModelClass, e);
            return null;
        }

        String[] fieldNames = ClassUtils.getBeanClassFields(actualModelClass);
        final BeanWrapper objWrapper = new BeanWrapperImpl(obj);
        for (String fieldName: fieldNames) {
            String columnName = TextUtils.humpToLine(fieldName);
            if (map.containsKey(columnName)) {
                objWrapper.setPropertyValue(fieldName, map.get(columnName));
            }
        }

        return obj;
    }

    private Object[] convertCreateParams(List<M> objs) {
        var allModelFields = new DynamicFields(ClassUtils.getBeanClassFields(getModelClass()));
        String[] createFields = allModelFields.getFields();
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
        sampleObj.initDefault(BaseFieldName.UPDATE_TIME);

        var allModelFields = new DynamicFields(ClassUtils.getBeanClassFields(getModelClass()));
        List<String> updateFields = new ArrayList<>();
        List<String> updateColumnFields = new ArrayList<>();
        for (String fieldName: allModelFields.getFields()) {
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
            obj.initDefault(BaseFieldName.UPDATE_TIME);

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

    @Recommendation
    default List<M> list(HumanCondition humanCondition, String[] selectFields) {
        if (humanCondition.getLimit() != null && humanCondition.getLimit() == 0) {
            return new ArrayList<>();
        }

        if (getMappedStatement("list") == null) {
            return commonList(humanCondition.toCondition(), selectFields);
        } else {
            return list(getTableName(), humanCondition.toCondition(), selectFields);
        }
    }

    @Recommendation
    default List<M> list(HumanCondition humanCondition) {
        String[] selectFields = humanCondition.toSelectFields();
        if (selectFields.length == 0) {
            selectFields = null;
        }
        return list(humanCondition, selectFields);
    }

    @Recommendation
    default int total(HumanCondition humanCondition) {
        if (getMappedStatement("total") == null) {
            return commonTotal(humanCondition.toCondition());
        } else {
            return total(getTableName(), humanCondition.toCondition());
        }
    }

    @Recommendation
    default M first(HumanCondition humanCondition, String[] selectFields) {
        humanCondition.slice(0, 1);
        List<M> objs = list(humanCondition, selectFields);
        if (objs.isEmpty()) {
            return null;
        } else {
            return objs.get(0);
        }
    }

    @Recommendation
    default M first(HumanCondition humanCondition) {
        String[] selectFields = humanCondition.toSelectFields();
        if (selectFields.length == 0) {
            selectFields = null;
        }
        return first(humanCondition, selectFields);
    }

    @Recommendation
    default boolean exist(HumanCondition humanCondition) {
        if (getMappedStatement("exist") == null) {
            return commonExist(humanCondition.toCondition());
        } else {
            return exist(getTableName(), humanCondition.toCondition());
        }
    }

    @Recommendation
    default M get(String id, HumanCondition humanSelectCondition) {
        String[] selectFields = humanSelectCondition.toSelectFields();
        if (selectFields.length == 0) {
            selectFields = null;
        }
        return get(id, selectFields);
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

    @Recommendation
    default boolean update(M obj) {
        Object[] updateParams = convertUpdateParams(List.of(obj));
        if (updateParams.length == 0) {
            log.error("Update {} has empty field", obj);
            return false;
        }

        return update(obj.getId(), (String[]) updateParams[0], ((Object[][]) updateParams[1])[0]);
    }

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

    @Recommendation
    default int bulkUpdate(List<M> objs) {
        if (objs.isEmpty()) {
            return 0;
        }

        Object[] updateParams = convertUpdateParams(objs);
        if (updateParams.length == 0) {
            log.error("Service bulk update {} has empty field", objs);
            return 0;
        }

        return bulkUpdate((String[]) updateParams[0], (Object[][]) updateParams[1]);
    }

    default List<M> commonList(@Param("condition") Condition condition, String[] selectFields) {
        List<Map<String, Object>> mapList = getCommonMapper().list(getTableName(), condition, selectFields);
        List<M> objs = new ArrayList<>();
        mapList.forEach(map -> {
            var obj = mapToObj(map);
            if (obj != null) {
                objs.add(obj);
            }
        });
        return objs;
    }

    default int commonTotal(@Param("condition") Condition condition) {
        return getCommonMapper().total(getTableName(), condition);
    }

    default boolean commonExist(@Param("condition") Condition condition) {
        return getCommonMapper().exist(getTableName(), condition);
    }

    List<M> list(@Param("condition") Condition condition, String[] selectFields);
    int total(@Param("condition") Condition condition);
    boolean exist(@Param("condition") Condition condition);

    @Recommendation
    default M get(String id, String[] selectFields) {
        Map<String, Object> map = getCommonMapper().get(getTableName(), id, selectFields);
        return mapToObj(map);
    }

    @Recommendation
    default M get(String id) {
        return get(id, (String[]) null);
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
}
