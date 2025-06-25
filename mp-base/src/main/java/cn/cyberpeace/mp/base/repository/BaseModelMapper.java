 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseModelMapper.java.
 // File size 33675.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.repository;

import cn.cyberpeace.mp.base.annotation.Recommendation;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.common.ModelUtils;
import cn.cyberpeace.mp.base.repository.condition.Condition;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.base.util.basic.CollectionUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.thread.taskpool.DefaultPoolTask;
import lombok.Data;
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
import java.util.stream.Collectors;

/**
 * @ClassName BaseModelMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface BaseModelMapper<M extends BaseModel> {

    String pkFieldName = BeanUtils.getBeanGetterFieldName(BaseModel::getPk);

    Logger log = LoggerFactory.getLogger(BaseModelMapper.class);

    Class<?> modelClass = null;

    /**
     * getCommonMapper
     *
     * @return
     */
    private static CommonMapper getCommonMapper() {
        return BeanCommon.getBean(CommonMapper.class);
    }

    /**
     * getMapperInterface
     *
     * @param proxy
     * @return
     */
    private static Class<?> getMapperInterface(Proxy proxy) {
        return ClassUtils.getObjFieldValue(Proxy.getInvocationHandler(proxy),
                "mapperInterface", Class.class);
    }

    /**
     * handleRawFieldValue
     *
     * @param fieldType
     * @param fieldValue
     * @return
     */
    private static Object handleRawFieldValue(Class<?> fieldType, Object fieldValue) {
        if (LocalDateTime.class.equals(fieldType) && fieldValue instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) fieldValue).toLocalDateTime();
        } else if (LocalDate.class.equals(fieldType) && fieldValue instanceof java.sql.Date) {
            return ((java.sql.Date) fieldValue).toLocalDate();
        } else if (LocalTime.class.equals(fieldType) && fieldValue instanceof java.sql.Time) {
            return ((java.sql.Time) fieldValue).toLocalTime();
        } else {
            return fieldValue;
        }
    }

    /**
     * mapToObj
     *
     * @param map
     * @param objClass
     * @return
     */
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
        for (Map.Entry<String, Class<?>> entry : fieldTypes.entrySet()) {
            String fieldName = entry.getKey();
            Class<?> fieldType = entry.getValue();
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
        }

        return obj;
    }

    /**
     * getMapperInterface
     *
     * @return
     */
    private Class<?> getMapperInterface() {
        return getMapperInterface((Proxy) this);
    }

    /**
     * getTableName
     *
     * @return
     */
    private String getTableName() {
        Class<M> actualModelClass = getModelClass();
        return actualModelClass != null ? ModelUtils.getTableName(actualModelClass) : null;
    }

    /**
     * getTableName
     *
     * @param humanCondition
     * @return
     */
    private String getTableName(HumanCondition humanCondition) {
        String tableName = humanCondition.toTableName();
        if (tableName == null) {
            tableName = getTableName();
        }
        return tableName;
    }

    /**
     * getMappedStatement
     *
     * @param humanName
     * @return
     */
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

    /**
     * getSelectFields
     *
     * @param humanCondition
     * @return
     */
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

    /**
     * mapToObj
     *
     * @param map
     * @return
     */
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

    /**
     * convertCreateParams
     *
     * @param objs
     * @return
     */
    private Object[] convertCreateParams(List<M> objs) {
        DynamicFields allModelFields = new DynamicFields(ClassUtils.getBeanClassFields(getModelClass()));
        List<String> list = new ArrayList<>();
        for (String fieldName : allModelFields.getFields()) {
            if (!fieldName.equals(pkFieldName)) {
                list.add(fieldName);
            }
        }
        String[] createFields = list.toArray(new String[0]);
        String[] createColumnFields = new String[createFields.length];
        for (int i = 0; i < createFields.length; i++) {
            createColumnFields[i] = TextUtils.humpToLine(createFields[i]);
        }

        Object[][] createFieldValuesList = new Object[objs.size()][createFields.length];
        for (int i = 0; i < objs.size(); i++) {
            M obj = objs.get(i);
            obj.initDefault();

            Object[] createFieldValues = new Object[createFields.length];
            for (int j = 0; j < createFields.length; j++) {
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

    /**
     * convertUpdateParams
     *
     * @param objs
     * @return
     */
    private Object[] convertUpdateParams(List<M> objs) {
        M sampleObj = objs.get(0);
        sampleObj.setUpdateTime(null);
        sampleObj.initDefault(BaseModel::getUpdateTime);

        DynamicFields allModelFields = new DynamicFields(ClassUtils.getBeanClassFields(getModelClass()));
        List<String> list = new ArrayList<>();
        for (String s : allModelFields.getFields()) {
            if (!s.equals(pkFieldName)) {
                list.add(s);
            }
        }
        String[] availableFields = list.toArray(new String[0]);

        List<String> updateFields = new ArrayList<>();
        List<String> updateColumnFields = new ArrayList<>();
        for (String fieldName: availableFields) {
            Object fieldValue = ClassUtils.getObjFieldValue(sampleObj, fieldName);
            if (fieldValue != null) {
                updateFields.add(fieldName);
                updateColumnFields.add(TextUtils.humpToLine(fieldName));
            }
        }

        Object[][] updateFieldValuesList = new Object[objs.size()][updateFields.size()];
        for (int i = 0; i < objs.size(); i++) {
            M obj = objs.get(i);
            sampleObj.setUpdateTime(null);
            obj.initDefault(BaseModel::getUpdateTime);

            Object[] updateFieldValues = new Object[updateFields.size()];
            for (int j = 0; j < updateFields.size(); j++) {
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
        for (M obj : objs) {
            String key = DigestUtils.md5Hex(StringUtils.joinWith(":", (Object[]) BeanUtils.getNotNullPropertyNames(obj)));
            List<M> keyObjs = validFieldsObjs.getOrDefault(key, new ArrayList<>());
            keyObjs.add(obj);
            validFieldsObjs.put(key, keyObjs);
        }
        return validFieldsObjs;
    }

    /**
     * getModelClass
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    default Class<M> getModelClass() {
        Class<?> actualModelClassSource;
        Class<?> mapperInterface = getMapperInterface();
        ModelClass modelClassAnnotation = mapperInterface.getAnnotation(ModelClass.class);
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

    /**
     * list
     *
     * @param humanCondition
     * @return
     */
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
        for (Map<String, Object> map : mapList) {
            T obj = mapToObj(map, objClass);
            if (obj != null) {
                objs.add(obj);
            }
        }
        return objs;
    }

    /**
     * total
     *
     * @param humanCondition
     * @return int
     */
    @Recommendation
    default int total(HumanCondition humanCondition) {
        if (getMappedStatement("total") == null) {
            return commonTotal(getTableName(humanCondition), humanCondition.toCondition());
        } else {
            return total(getTableName(humanCondition), humanCondition.toCondition());
        }
    }

    /**
     * first
     *
     * @param humanCondition
     * @return
     */
    @Recommendation
    default M first(HumanCondition humanCondition) {
        humanCondition = humanCondition.slice(0, 1);
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
        humanCondition = humanCondition.slice(0, 1);
        List<Map<String, Object>> objs = originList(humanCondition);
        if (objs.isEmpty()) {
            return null;
        } else {
            return objs.get(0);
        }
    }

    /**
     * exist
     *
     * @param humanCondition
     * @return boolean
     */
    @Recommendation
    default boolean exist(HumanCondition humanCondition) {
        if (getMappedStatement("exist") == null) {
            return commonExist(getTableName(humanCondition), humanCondition.toCondition());
        } else {
            return exist(getTableName(humanCondition), humanCondition.toCondition());
        }
    }

    /**
     * get
     *
     * @param id
     * @param humanSelectCondition
     * @return
     */
    @Recommendation
    default M get(String id, HumanCondition humanSelectCondition) {
        Map<String, Object> map = getCommonMapper().get(getTableName(), id, getSelectFields(humanSelectCondition));
        return mapToObj(map);
    }

    /**
     * create
     *
     * @param obj
     * @return boolean
     */
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

    /**
     * bulkCreate
     *
     * @param objs
     * @return
     */
    @Recommendation
    default BulkResult<M> bulkCreate(List<M> objs) {
        BulkResult<M> result = new BulkResult<>(0);
        if (objs.isEmpty()) {
            return result;
        }

        Object[] createParams = convertCreateParams(objs);
        if (createParams.length == 0) {
            log.error("Bulk create {} has empty field", objs);
            return result.addFail(objs);
        }

        int successCount = bulkCreate((String[]) createParams[0], (Object[][]) createParams[1]);
        if (successCount == 0) {
            return result.addFail(objs);
        } else {
            return result.addSuccessCount(successCount);
        }
    }

    /**
     * bulkCreate
     *
     * @param objs
     * @param batchSize
     * @param concurrent
     * @return
     */
    @Recommendation
    default BulkResult<M> bulkCreate(List<M> objs, int batchSize, boolean concurrent) {
        BulkResult<M> result = new BulkResult<>(0);
        if (objs.isEmpty()) {
            return result;
        }

        List<Runnable> tasks = new ArrayList<>();
        CollectionUtils.batchHandle(objs, batchSize, batchList -> {
            tasks.add(() -> {
                BulkResult<M> batchResult = bulkCreate(batchList);
                synchronized (result) {
                    result.add(batchResult);
                }
            });
        });
        if (concurrent) {
            final List<DefaultPoolTask> defaultPoolTasks = TaskCommon.runTasks(tasks);
        } else {
            for (Runnable task : tasks) {
                task.run();
            }
        }
        return result;
    }

    /**
     * 批量更新，所有objs必须要有id, 更新数据字段最好一致
     */
    @Recommendation
    default BulkResult<M> bulkUpdate(List<M> objs) {
        BulkResult<M> result = new BulkResult<>(0);
        if (objs.isEmpty()) {
            return result;
        }

        for (List<M> validFieldsObjs: groupValidFieldsObjs(objs).values()) {
            Collections.sort(validFieldsObjs, Comparator.comparing(BaseModel::getId));
            Object[] updateParams = convertUpdateParams(validFieldsObjs);
            if (updateParams.length == 0) {
                result.addFail(objs);
                log.error("Service bulk update {} has empty field", objs);
                continue;
            }
            int successCount = bulkUpdate((String[]) updateParams[0], (Object[][]) updateParams[1]);
            if (successCount == 0) {
                result.addFail(objs);
            } else {
                result.addSuccessCount(successCount);
            }
        }

        return result;
    }

    /**
     * 批量更新，所有objs必须要有id, 更新数据字段最好一致
     */
    @Recommendation
    default BulkResult<M> bulkUpdate(List<M> objs, int batchSize, boolean concurrent) {
        BulkResult<M> result = new BulkResult<>(0);
        if (objs.isEmpty()) {
            return result;
        }

        List<Runnable> tasks = new ArrayList<>();
        CollectionUtils.batchHandle(objs, batchSize, batchList -> {
            tasks.add(() -> {
                BulkResult<M> batchResult = bulkUpdate(batchList);
                synchronized (result) {
                    result.add(batchResult);
                }
            });
        });
        if (concurrent) {
            final List<DefaultPoolTask> defaultPoolTasks = TaskCommon.runTasks(tasks);
        } else {
            for (Runnable task : tasks) {
                task.run();
            }
        }
        return result;
    }

    default List<M> commonList(String tableName, @Param("condition") Condition condition, String[] selectFields) {
        List<Map<String, Object>> mapList = getCommonMapper().list(tableName, condition, selectFields);
        List<M> objs = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            M obj = mapToObj(map);
            if (obj != null) {
                objs.add(obj);
            }
        }
        return objs;
    }

    /**
     * commonTotal
     *
     * @param tableName
     * @param condition
     * @return int
     */
    default int commonTotal(String tableName, @Param("condition") Condition condition) {
        return getCommonMapper().total(tableName, condition);
    }

    /**
     * commonExist
     *
     * @param tableName
     * @param condition
     * @return boolean
     */
    default boolean commonExist(String tableName, @Param("condition") Condition condition) {
        return getCommonMapper().exist(tableName, condition);
    }

    /**
     * list
     *
     * @param condition
     * @param selectFields
     * @return
     */
    List<M> list(@Param("condition") Condition condition, String[] selectFields);

    /**
     * total
     *
     * @param condition
     * @return int
     */
    int total(@Param("condition") Condition condition);

    /**
     * exist
     *
     * @param condition
     * @return boolean
     */
    boolean exist(@Param("condition") Condition condition);

    /**
     * get
     *
     * @param id
     * @return
     */
    @Recommendation
    default M get(String id) {
        return get(id, new HumanCondition());
    }

    /**
     * logicGet
     *
     * @param id
     * @return
     */
    @Recommendation
    default M logicGet(String id) {
        M obj = get(id);
        return (obj != null && obj.hasDeleted()) ? null : obj;
    }

    /**
     * create
     *
     * @param createFields
     * @param createFieldValues
     * @return boolean
     */
    default boolean create(String[] createFields, Object[] createFieldValues) {
        return getCommonMapper().create(getTableName(), createFields, createFieldValues);
    }

    /**
     * update
     *
     * @param id
     * @param updateFields
     * @param updateFieldValues
     * @return boolean
     */
    default boolean update(String id, String[] updateFields, Object[] updateFieldValues) {
        return getCommonMapper().update(getTableName(), id, updateFields, updateFieldValues);
    }

    /**
     * delete
     *
     * @param id
     * @return boolean
     */
    @Recommendation
    default boolean delete(String id) {
        return getCommonMapper().delete(getTableName(), id);
    }

    /**
     * logicDelete
     *
     * @param id
     * @return boolean
     */
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

    /**
     * bulkCreate
     *
     * @param createFields
     * @param createFieldValuesList
     * @return int
     */
    default int bulkCreate(String[] createFields, Object[][] createFieldValuesList) {
        return getCommonMapper().bulkCreate(getTableName(), createFields, createFieldValuesList);
    }

    /**
     * bulkUpdate
     *
     * @param updateFields
     * @param updateFieldValuesList
     * @return int
     */
    default int bulkUpdate(String[] updateFields, Object[][] updateFieldValuesList) {
        return getCommonMapper().bulkUpdate(getTableName(), updateFields, updateFieldValuesList);
    }

    /**
     * bulkDelete
     *
     * @param ids
     * @return
     */
    @Recommendation
    default BulkResult<String> bulkDelete(String[] ids) {
        BulkResult<String> result = new BulkResult<>(0);
        if (ids.length == 0) {
            return result;
        }

        int successCount = getCommonMapper().bulkDelete(getTableName(), ids);
        if (successCount == 0) {
            return result.addFail(Arrays.asList(ids));
        } else {
            return result.addSuccessCount(successCount);
        }
    }

    /**
     * bulkDelete
     *
     * @param ids
     * @param batchSize
     * @param concurrent
     * @return
     */
    @Recommendation
    default BulkResult<String> bulkDelete(String[] ids, int batchSize, boolean concurrent) {
        BulkResult<String> result = new BulkResult<>(0);
        if (ids.length == 0) {
            return result;
        }

        List<Runnable> tasks = new ArrayList<>();
        CollectionUtils.batchHandle(Arrays.asList(ids), batchSize, batchList -> {
            tasks.add(() -> {
                BulkResult<String> batchResult = bulkDelete(batchList.toArray(new String[0]));
                synchronized (result) {
                    result.add(batchResult);
                }
            });
        });
        if (concurrent) {
            final List<DefaultPoolTask> defaultPoolTasks = TaskCommon.runTasks(tasks);
        } else {
            for (Runnable task : tasks) {
                task.run();
            }
        }
        return result;
    }

    /**
     * bulkLogicDelete
     *
     * @param ids
     * @return
     */
    @Recommendation
    default BulkResult<String> bulkLogicDelete(String[] ids) {
        BulkResult<String> result = new BulkResult<>(0);
        if (ids.length == 0) {
            return result;
        }

        Class<M> actualModelClass = getModelClass();
        if (actualModelClass == null) {
            return result.addFail(Arrays.asList(ids));
        }

        List<M> logicObjs = new ArrayList<>();
        List<String> rawIds = new ArrayList<>();
        try {
            for (String id: ids) {
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
            return result.addFail(Arrays.asList(ids));
        }

        if (!logicObjs.isEmpty()) {
            BulkResult<M> updateResult = bulkUpdate(logicObjs);
            result.addSuccessCount(updateResult.getSuccessCount());
            if (updateResult.getFail() != null) {
                List<String> list = new ArrayList<>();
                for (M m : updateResult.getFail()) {
                    String id = m.getId();
                    list.add(id);
                }
                result.addFail(list);
            }
        }
        if (!rawIds.isEmpty()) {
            result.add(bulkDelete(rawIds.toArray(new String[0])));
        }

        return result;
    }

    /**
     * bulkLogicDelete
     *
     * @param ids
     * @param batchSize
     * @param concurrent
     * @return
     */
    @Recommendation
    default BulkResult<String> bulkLogicDelete(String[] ids, int batchSize, boolean concurrent) {
        BulkResult<String> result = new BulkResult<>(0);
        if (ids.length == 0) {
            return result;
        }

        List<Runnable> tasks = new ArrayList<>();
        CollectionUtils.batchHandle(Arrays.asList(ids), batchSize, batchList -> {
            tasks.add(() -> {
                BulkResult<String> batchResult = bulkLogicDelete(batchList.toArray(new String[0]));
                synchronized (result) {
                    result.add(batchResult);
                }
            });
        });
        if (concurrent) {
            final List<DefaultPoolTask> defaultPoolTasks = TaskCommon.runTasks(tasks);
        } else {
            for (Runnable task : tasks) {
                task.run();
            }
        }
        return result;
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

    /**
     * list
     *
     * @param table
     * @param condition
     * @param selectFields
     * @return
     */
    List<M> list(String table, @Param("condition") Condition condition, String[] selectFields);

    /**
     * total
     *
     * @param table
     * @param condition
     * @return int
     */
    int total(String table, @Param("condition") Condition condition);

    /**
     * exist
     *
     * @param table
     * @param condition
     * @return boolean
     */
    boolean exist(String table, @Param("condition") Condition condition);

    /**
     * get
     *
     * @param table
     * @param id
     * @param selectFields
     * @return
     */
    M get(String table, String id, String[] selectFields);

    /**
     * create
     *
     * @param table
     * @param createFields
     * @param createFieldValues
     * @return boolean
     */
    boolean create(String table, String[] createFields, Object[] createFieldValues);

    /**
     * update
     *
     * @param table
     * @param id
     * @param updateFields
     * @param updateFieldValues
     * @return boolean
     */
    boolean update(String table, String id, String[] updateFields, Object[] updateFieldValues);

    /**
     * delete
     *
     * @param table
     * @param id
     * @return boolean
     */
    boolean delete(String table, String id);

    /**
     * bulkCreate
     *
     * @param table
     * @param createFields
     * @param createFieldValuesList
     * @return int
     */
    int bulkCreate(String table, String[] createFields, Object[][] createFieldValuesList);

    /**
     * bulkUpdate
     *
     * @param table
     * @param updateFields
     * @param updateFieldValuesList
     * @return int
     */
    int bulkUpdate(String table, String[] updateFields, Object[][] updateFieldValuesList);

    /**
     * bulkDelete
     *
     * @param table
     * @param ids
     * @return int
     */
    int bulkDelete(String table, String[] ids);

    /**
     * filterUpdate
     *
     * @param table
     * @param updateFields
     * @param updateFieldValues
     * @param condition
     * @return int
     */
    int filterUpdate(String table, String[] updateFields, Object[] updateFieldValues,
                     @Param("condition") Condition condition);

    /**
     * filterDelete
     *
     * @param table
     * @param condition
     * @return int
     */
    int filterDelete(String table, @Param("condition") Condition condition);

    @Data
    class BulkResult<T> {
        private int successCount;
        private List<T> fail;

        public BulkResult() {}

        /**
         * BulkResult
         *
         * @param successCount
         */
        public BulkResult(int successCount) {
            this.successCount = successCount;
        }

        /**
         * BulkResult
         *
         * @param successCount
         * @param fail
         */
        public BulkResult(int successCount, List<T> fail) {
            this.successCount = successCount;
            this.fail = fail;
        }

        /**
         * add
         *
         * @param result
         * @return
         */
        public BulkResult<T> add(BulkResult<T> result) {
            this.addSuccessCount(result.getSuccessCount());
            if (result.getFail() != null) {
                addFail(result.getFail());
            }
            return this;
        }

        /**
         * addSuccessCount
         *
         * @param successCount
         * @return
         */
        public BulkResult<T> addSuccessCount(int successCount) {
            return this;
        }

        /**
         * addFail
         *
         * @param fail
         * @return
         */
        public BulkResult<T> addFail(List<T> fail) {
            if (this.fail == null) {
                this.fail = new ArrayList<>();
            }
            this.fail.addAll(fail);
            return this;
        }
    }
}
