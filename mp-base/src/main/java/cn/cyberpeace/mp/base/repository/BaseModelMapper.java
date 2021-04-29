package cn.cyberpeace.mp.base.repository;

import cn.cyberpeace.mp.base.annotation.Recommendation;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.Condition;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.base.util.ModelUtils;
import cn.cyberpeace.mp.base.util.TextUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface BaseModelMapper<Model extends BaseModel> {

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

    private Class<?> getModelClass() {
        return ClassUtils.getStaticFieldValue(getMapperInterface(), "modelClass",
                Class.class);
    }

    private String getTableName() {
        return ModelUtils.getTableName(getModelClass());
    }

    private MappedStatement getMappedStatement(String humanName) {
        String mapperId = getMapperInterface().getName() + "." + humanName;
        MappedStatement mappedStatement = null;
        try {
            mappedStatement = BeanUtils.getBean(SqlSessionFactory.class).getConfiguration().getMappedStatement(
                    mapperId);
        } catch (Exception ignored) {
        }

        return mappedStatement;
    }

    private Model mapToObj(Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        Class<?> modelClass = getModelClass();
        if (modelClass == null) {
            return null;
        }

        Model obj;
        try {
            obj = (Model) modelClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("Convert {} to {} instance failed: {}", map, modelClass, e);
            return null;
        }

        String[] fieldNames = ClassUtils.getBeanClassFields(modelClass);
        for (String fieldName: fieldNames) {
            String columnName = TextUtils.humpToLine(fieldName);
            if (map.containsKey(columnName)) {
                ClassUtils.setBeanObjFieldValue(obj, fieldName, map.get(columnName));
            }
        }

        return obj;
    }

    private Object[] convertCreateParams(List<Model> objs) {
        DynamicFields allModelFields = new DynamicFields(ClassUtils.getBeanClassFields(getModelClass()));
        String[] createFields = allModelFields.getFields();
        String[] createColumnFields = new String[createFields.length];
        for (int i = 0; i < createFields.length; i++) {
            createColumnFields[i] = TextUtils.humpToLine(createFields[i]);
        }

        Object[][] createFieldValuesList = new Object[objs.size()][createFields.length];
        for (int i = 0; i < objs.size(); i++) {
            Model obj = objs.get(i);
            obj.initDefault();

            Object[] createFieldValues = new Object[createFields.length];
            for (int j = 0; j < createFields.length; j++) {
                Object fieldValue = ClassUtils.getObjFieldValue(obj, createFields[j]);
                if (fieldValue == null) {
                    return null;
                }
                createFieldValues[j] = fieldValue;
            }
            createFieldValuesList[i] = createFieldValues;
        }

        return new Object[]{ createColumnFields, createFieldValuesList };
    }

    private Object[] convertUpdateParams(List<Model> objs) {
        String[] ids = new String[objs.size()];
        for (int i = 0; i < objs.size(); i++) {
            String id = objs.get(i).getId();
            if (id == null) {
                return null;
            }
            ids[i] = id;
        }

        Model sampleObj = objs.get(0);
        sampleObj.setUpdateTime(null);
        sampleObj.initDefaultUpdateTime();

        DynamicFields allModelFields = new DynamicFields(ClassUtils.getBeanClassFields(getModelClass()));
        List<String> updateFields = new ArrayList<>();
        List<String> updateColumnFields = new ArrayList<>();
        for (String fieldName: allModelFields.getFields()) {
            Object fieldValue = ClassUtils.getObjFieldValue(sampleObj, fieldName);
            if (fieldValue != null) {
                updateFields.add(fieldName);
                updateColumnFields.add(TextUtils.humpToLine(fieldName));
            }
        }

        Object[][] updateFieldValuesList = new Object[objs.size()][updateFields.size()];
        for (int i = 0; i < objs.size(); i++) {
            Model obj = objs.get(i);
            sampleObj.setUpdateTime(null);
            obj.initDefaultUpdateTime();

            Object[] updateFieldValues = new Object[updateFields.size()];
            for (int j = 0; j < updateFields.size(); j++) {
                Object fieldValue = ClassUtils.getObjFieldValue(obj, updateFields.get(j));
                if (fieldValue == null) {
                    return null;
                }
                updateFieldValues[j] = fieldValue;
            }
            updateFieldValuesList[i] = updateFieldValues;
        }

        return new Object[]{ updateColumnFields.toArray(new String[0]), updateFieldValuesList };
    }

    @Recommendation
    default List<Model> list(HumanCondition humanCondition, String[] selectFields) {
        if (getMappedStatement("list") == null) {
            return commonList(humanCondition.toCondition(), selectFields);
        } else {
            return list(getTableName(), humanCondition.toCondition(), selectFields);
        }
    }

    @Recommendation
    default Integer total(HumanCondition humanCondition) {
        if (getMappedStatement("total") == null) {
            return commonTotal(humanCondition.toCondition());
        } else {
            return total(getTableName(), humanCondition.toCondition());
        }
    }

    @Recommendation
    default Boolean exist(HumanCondition humanCondition) {
        if (getMappedStatement("exist") == null) {
            return commonExist(humanCondition.toCondition());
        } else {
            return exist(getTableName(), humanCondition.toCondition());
        }
    }

    @Recommendation
    default Boolean create(Model obj) {
        Object[] createParams = convertCreateParams(List.of(obj));
        if (createParams == null) {
            log.error("Create {} has empty field", obj);
            return false;
        }

        return create((String[]) createParams[0], ((Object[][]) createParams[1])[0]);
    }

    @Recommendation
    default Boolean update(Model obj) {
        Object[] updateParams = convertUpdateParams(List.of(obj));
        if (updateParams == null) {
            log.error("Update {} has empty field", obj);
            return false;
        }

        return update(obj.getId(), (String[]) updateParams[0], ((Object[][]) updateParams[1])[0]);
    }

    @Recommendation
    default Integer bulkCreate(List<Model> objs) {
        if (objs.size() == 0) {
            return 0;
        }

        Object[] createParams = convertCreateParams(objs);
        if (createParams == null) {
            log.error("Bulk create {} has empty field", objs);
            return 0;
        }

        return bulkCreate((String[]) createParams[0], (Object[][]) createParams[1]);
    }

    @Recommendation
    default Integer bulkUpdate(List<Model> objs) {
        if (objs.size() == 0) {
            return 0;
        }

        Object[] updateParams = convertUpdateParams(objs);
        if (updateParams == null) {
            log.error("Service bulk update {} has empty field", objs);
            return 0;
        }

        return bulkUpdate((String[]) updateParams[0], (Object[][]) updateParams[1]);
    }

    default List<Model> commonList(@Param("condition") Condition condition, String[] selectFields) {
        List<Map<String, Object>> mapList = getCommonMapper().list(getTableName(), condition, selectFields);
        List<Model> objs = new ArrayList<>();
        mapList.forEach(map -> {
            Model obj = mapToObj(map);
            if (obj != null) {
                objs.add(obj);
            }
        });
        return objs;
    }

    default Integer commonTotal(@Param("condition") Condition condition) {
        return getCommonMapper().total(getTableName(), condition);
    }

    default Boolean commonExist(@Param("condition") Condition condition) {
        return getCommonMapper().exist(getTableName(), condition);
    }

    List<Model> list(@Param("condition") Condition condition, String[] selectFields);
    Integer total(@Param("condition") Condition condition);
    Boolean exist(@Param("condition") Condition condition);

    @Recommendation
    default Model get(String id, String[] selectFields) {
        Map<String, Object> map = getCommonMapper().get(getTableName(), id, selectFields);
        return mapToObj(map);
    }

    default Boolean create(String[] createFields, Object[] createFieldValues) {
        return getCommonMapper().create(getTableName(), createFields, createFieldValues);
    }

    default Boolean update(String id, String[] updateFields, Object[] updateFieldValues) {
        return getCommonMapper().update(getTableName(), id, updateFields, updateFieldValues);
    }

    @Recommendation
    default Boolean delete(String id) {
        return getCommonMapper().delete(getTableName(), id);
    }

    default Integer bulkCreate(String[] createFields, Object[][] createFieldValuesList) {
        return getCommonMapper().bulkCreate(getTableName(), createFields, createFieldValuesList);
    }

    default Integer bulkUpdate(String[] updateFields, Object[][] updateFieldValuesList) {
        return getCommonMapper().bulkUpdate(getTableName(), updateFields, updateFieldValuesList);
    }

    @Recommendation
    default Integer bulkDelete(String[] ids) {
        return getCommonMapper().bulkDelete(getTableName(), ids);
    }

    List<Model> list(String table, @Param("condition") Condition condition, String[] selectFields);
    Integer total(String table, @Param("condition") Condition condition);
    Boolean exist(String table, @Param("condition") Condition condition);
    Model get(String table, String id, String[] selectFields);
    Boolean create(String table, String[] createFields, Object[] createFieldValues);
    Boolean update(String table, String id, String[] updateFields, Object[] updateFieldValues);
    Boolean delete(String table, String id);
    Integer bulkCreate(String table, String[] createFields, Object[][] createFieldValuesList);
    Integer bulkUpdate(String table, String[] updateFields, Object[][] updateFieldValuesList);
    Integer bulkDelete(String table, String[] ids);
}
