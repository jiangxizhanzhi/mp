package cn.cyberpeace.mp.base.repository;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.Condition;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.base.util.ModelUtils;
import cn.cyberpeace.mp.base.util.TextUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public interface BaseModelMapper<Model extends BaseModel> {

    Logger log = LoggerFactory.getLogger(BaseModelMapper.class);

    Class<?> modelClass = null;

    private Class<?> getModelClass() {
        Class<?> mapperInterface = ClassUtils.getObjFieldValue(Proxy.getInvocationHandler(this),
                "mapperInterface", Class.class);
        Class<?> modelClass = ClassUtils.getStaticFieldValue(mapperInterface, "modelClass",
                Class.class);
        return modelClass;
    }

    private String getTableName() {
        return ModelUtils.getTableName(getModelClass());
    }

    private CommonMapper getCommonMapper() {
        return BeanUtils.getBean(CommonMapper.class);
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

    default List<Model> list(HumanCondition humanCondition, String[] selectFields) {
        return list(humanCondition.toCondition(), selectFields);
    }

    default Integer total(HumanCondition humanCondition) {
        return total(humanCondition.toCondition());
    }

    default Boolean create(Model obj) {
        Object[] createParams = convertCreateParams(new ArrayList<>(){{ add(obj); }});
        if (createParams == null) {
            log.error("Create {} has empty field", obj);
            return false;
        }

        return create((String[]) createParams[0], ((Object[][]) createParams[1])[0]);
    }

    default Boolean update(Model obj) {
        Object[] updateParams = convertUpdateParams(new ArrayList<>(){{ add(obj); }});
        if (updateParams == null) {
            log.error("Update {} has empty field", obj);
            return false;
        }

        return update(obj.getId(), (String[]) updateParams[0], ((Object[][]) updateParams[1])[0]);
    }

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

    List<Model> list(@Param("condition") Condition condition, String[] selectFields);
    Integer total(@Param("condition") Condition condition);
    Model get(String id, String[] selectFields);

    default Boolean create(String[] createFields, Object[] createFieldValues) {
        return getCommonMapper().create(getTableName(), createFields, createFieldValues);
    }

    default Boolean update(String id, String[] updateFields, Object[] updateFieldValues) {
        return getCommonMapper().update(getTableName(), id, updateFields, updateFieldValues);
    }

    default Boolean delete(String id) {
        return getCommonMapper().delete(getTableName(), id);
    }

    default Integer bulkCreate(String[] createFields, Object[][] createFieldValuesList) {
        return getCommonMapper().bulkCreate(getTableName(), createFields, createFieldValuesList);
    }

    default Integer bulkUpdate(String[] updateFields, Object[][] updateFieldValuesList) {
        return getCommonMapper().bulkUpdate(getTableName(), updateFields, updateFieldValuesList);
    }

    default Integer bulkDelete(String[] ids) {
        return getCommonMapper().bulkDelete(getTableName(), ids);
    }

}
