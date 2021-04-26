package cn.cyberpeace.mp.base.service;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.CommonModelMapper;
import cn.cyberpeace.mp.base.repository.condition.*;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.service.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.base.util.ModelUtils;
import cn.cyberpeace.mp.base.util.TextUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
public abstract class BaseModelServiceImpl<Model extends BaseModel,
        ModelMapper extends BaseModelMapper<Model>> implements BaseModelService<Model> {

    public static final Class<? extends BaseModel> modeClass = null;

    public static final Class<? extends BaseModelFields> modelFieldsClass = null;

    public static final Class<? extends CustomCondition> customConditionClass = null;

    private ModelMapper modelMapper;

    @Autowired
    private CommonModelMapper commonModelMapper;

    private String getTableName() {
        Class<?> modeClass = ClassUtils.getStaticFieldValue(getClass(), "modelClass",
                Class.class);
        if (modeClass == null) {
            return null;
        }
        return ModelUtils.getTableName(modeClass);
    }

    private String getTableName(Model obj) {
        String tableName = ModelUtils.getTableName(obj.getClass());
        if (StringUtils.isEmpty(tableName)) {
            tableName = getTableName();
        }
        return tableName;
    }

    private DynamicFields getSpecModelFields(String fieldName) {
        DynamicFields modelFields = null;
        Class<?> modelFieldsClass = ClassUtils.getStaticFieldValue(getClass(), "modelFieldsClass",
                Class.class);
        if (modelFieldsClass != null) {
            modelFields = ClassUtils.getStaticFieldValue(modelFieldsClass, fieldName, DynamicFields.class);
        }
        return modelFields;
    }

    private DynamicFields getValidModelFields(String[] fields) {
        DynamicFields validModelFields = null;
        Class<?> modelFieldsClass = ClassUtils.getStaticFieldValue(getClass(), "modelFieldsClass",
                Class.class);
        if (modelFieldsClass != null && fields != null) {
            DynamicFields availableFields = getSpecModelFields("fields");
            if (availableFields != null) {
                validModelFields = availableFields.intersect(fields);
                if (!validModelFields.hasValidFields()) {
                    validModelFields = null;
                }
            }
        }

        return validModelFields;
    }

    private void clearNotWritableFields(Model obj) {
        DynamicFields hiddenModelFields = getSpecModelFields("hiddenFields");
        if (hiddenModelFields != null) {
            for (String fieldName: hiddenModelFields.getFields()) {
                ClassUtils.setBeanObjFieldValue(obj, fieldName, null);
            }
        }

        DynamicFields readonlyModelFields = getSpecModelFields("readonlyFields");
        if (readonlyModelFields != null) {
            for (String fieldName: readonlyModelFields.getFields()) {
                ClassUtils.setBeanObjFieldValue(obj, fieldName, null);
            }
        }
    }

    private Object[] convertCreateParams(List<Model> objs) {
        DynamicFields allModelFields = getSpecModelFields("allFields");
        if (allModelFields == null) {
            allModelFields = new DynamicFields(ClassUtils.getBeanClassFields(objs.get(0).getClass()));
        }
        String[] createFields = allModelFields.getFields();
        String[] createColumnFields = new String[createFields.length];
        for (int i = 0; i < createFields.length; i++) {
            createColumnFields[i] = TextUtils.humpToLine(createFields[i]);
        }

        Object[][] createFieldValuesList = new Object[objs.size()][createFields.length];
        for (int i = 0; i < objs.size(); i++) {
            Model obj = objs.get(i);
            clearNotWritableFields(obj);
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
        clearNotWritableFields(sampleObj);
        sampleObj.initDefaultUpdateTime();
        sampleObj.setId(ids[0]);

        DynamicFields availableModelFields = getSpecModelFields("fields");
        if (availableModelFields == null) {
            availableModelFields = new DynamicFields(ClassUtils.getBeanClassFields(sampleObj.getClass()));
        }

        List<String> updateFields = new ArrayList<>();
        List<String> updateColumnFields = new ArrayList<>();
        for (String fieldName: availableModelFields.getFields()) {
            Object fieldValue = ClassUtils.getObjFieldValue(sampleObj, fieldName);
            if (fieldValue != null) {
                updateFields.add(fieldName);
                updateColumnFields.add(TextUtils.humpToLine(fieldName));
            }
        }

        Object[][] updateFieldValuesList = new Object[objs.size()][updateFields.size()];
        for (int i = 0; i < objs.size(); i++) {
            Model obj = objs.get(i);
            clearNotWritableFields(obj);
            obj.initDefaultUpdateTime();
            obj.setId(ids[i]);

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

        return new Object[]{ updateColumnFields.toArray(new String[0]), updateFieldValuesList,
                updateFields.toArray(new String[0]) };
    }

    private Condition getCondition(Map<String, Object> params, String search, String[] searchFields, String[] orders,
                                   Integer page, Integer pageSize) {
        Condition condition = new Condition();
        condition.setPagination(new PaginationCondition().loadFromPageParams(page, pageSize));

        DynamicFields availableSearchFields = getSpecModelFields("searchFields");
        String[] lastSearchFields = null;
        if (availableSearchFields != null) {
            if (searchFields != null && searchFields.length > 0) {
                DynamicFields validSearchFields = availableSearchFields.intersect(searchFields);
                if (validSearchFields.hasValidFields()) {
                    lastSearchFields = validSearchFields.getFields();
                } else {
                    lastSearchFields = availableSearchFields.getFields();
                }
            } else {
                lastSearchFields = availableSearchFields.getFields();
            }
            lastSearchFields = TextUtils.humpToLine(lastSearchFields);
        }
        condition.setSearch(new SearchCondition(search, lastSearchFields));

        DynamicFields availableOrderFields = getSpecModelFields("orderFields");
        String[] lastOrders = null;
        if (availableOrderFields != null && orders != null && orders.length > 0) {
            Map<String, String> fieldNameOrderMap = new LinkedHashMap<>();
            for (String order: orders) {
                if (order.startsWith("-")) {
                    fieldNameOrderMap.put(order.substring(1), order);
                } else {
                    fieldNameOrderMap.put(order, order);
                }
            }
            DynamicFields validOrderFields = availableOrderFields.intersect(
                    fieldNameOrderMap.keySet().toArray(new String[0]));
            List<String> lastOrderList = new ArrayList<>();
            fieldNameOrderMap.forEach((fieldName, order) -> {
                if (validOrderFields.hasField(fieldName)) {
                    lastOrderList.add(order);
                }
            });
            lastOrders = TextUtils.humpToLine(lastOrderList.toArray(new String[0]));
        }
        condition.setOrder(new OrderCondition(lastOrders));

        condition.setCustom(getCustomCondition(params));
        return condition;
    }

    public CustomCondition getCustomCondition(Map<String, Object> params) {
        Class<?> customConditionClass = ClassUtils.getStaticFieldValue(getClass(),
                "customConditionClass", Class.class);
        if (customConditionClass == null) {
            customConditionClass = CustomCondition.class;
        }

        CustomCondition customCondition;
        try {
            customCondition = (CustomCondition) customConditionClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("Create custom condition by {} failed: {}", customConditionClass, e);
            customCondition = new CustomCondition();
        }

        return customCondition.loadFromParams(params);
    }

    @Override
    public ServiceResult list(Map<String, Object> params, String search, String[] searchFields, String[] orders,
                              Integer page, Integer pageSize, String[] fields) {
        Condition condition = getCondition(params, search, searchFields, orders, page, pageSize);
        DynamicFields validModelFields = getValidModelFields(fields);
        String[] selectFields = validModelFields == null ? null : TextUtils.humpToLine(validModelFields.getFields());
        List<Model> data = new ArrayList<>(modelMapper.list(condition, selectFields));
        Integer total = modelMapper.total(condition);

        return new ServiceResult(new ListObjResult<>(data, total, validModelFields));
    }

    @Override
    public ServiceResult get(String id, String[] fields) {
        DynamicFields validModelFields = getValidModelFields(fields);
        String[] selectFields = validModelFields == null ? null : TextUtils.humpToLine(validModelFields.getFields());
        Model obj = modelMapper.get(id, selectFields);
        return new ServiceResult(new ObjResult<>(obj, validModelFields));
    }

    @Override
    public ServiceResult create(Model obj, String[] fields) {
        String tableName = getTableName(obj);
        if (StringUtils.isEmpty(tableName)) {
            log.error("Service create {} with no table name found", obj);
            return new ServiceResult(false);
        }

        Object[] createParams = convertCreateParams(new ArrayList<>(){{ add(obj); }});
        if (createParams == null) {
            log.error("Service create {} has empty field", obj);
            return new ServiceResult(false);
        }

        Boolean result = commonModelMapper.create(tableName, (String[]) createParams[0],
                ((Object[][]) createParams[1])[0]);

        if (result) {
            DynamicFields validModelFields = getValidModelFields(fields);
            return new ServiceResult(new ObjResult<>(obj, validModelFields));
        } else {
            log.error("Service create {} failed", obj);
            return new ServiceResult(false);
        }
    }

    @Override
    public ServiceResult update(Model obj, String[] fields) {
        String tableName = getTableName(obj);
        if (StringUtils.isEmpty(tableName)) {
            log.error("Service update {} with no table name found", obj);
            return new ServiceResult(false);
        }

        Object[] updateParams = convertUpdateParams(new ArrayList<>(){{ add(obj); }});
        if (updateParams == null) {
            log.error("Service update {} has empty field", obj);
            return new ServiceResult(false);
        }

        Boolean result = commonModelMapper.update(tableName, obj.getId(), (String[]) updateParams[0],
                ((Object[][]) updateParams[1])[0]);

        if (result) {
            DynamicFields validModelFields = getValidModelFields(fields);
            if (validModelFields == null) {
                validModelFields = new DynamicFields((String[]) updateParams[2]);
            } else {
                validModelFields = validModelFields.intersect((String[]) updateParams[2]);
            }
            return new ServiceResult(new ObjResult<>(obj, validModelFields));
        } else {
            log.error("Service update {} failed", obj);
            return new ServiceResult(false);
        }
    }

    @Override
    public ServiceResult delete(String id) {
        String tableName = getTableName();
        if (StringUtils.isEmpty(tableName)) {
            log.error("Service delete {} with no table name found", id);
            return new ServiceResult(false);
        }

        Boolean result = commonModelMapper.delete(tableName, id);

        if (!result) {
            log.error("Service delete {} failed", id);
        }

        return new ServiceResult(result);
    }

    @Override
    public ServiceResult bulkCreate(List<Model> objs) {
        if (objs.size() == 0) {
            return new ServiceResult(0);
        }

        String tableName = getTableName(objs.get(0));
        if (StringUtils.isEmpty(tableName)) {
            log.error("Service bulk create {} with no table name found", objs);
            return new ServiceResult(0);
        }

        Object[] createParams = convertCreateParams(objs);
        if (createParams == null) {
            log.error("Service bulk create {} has empty field", objs);
            return new ServiceResult(0);
        }

        Integer result = commonModelMapper.bulkCreate(tableName, (String[]) createParams[0],
                (Object[][]) createParams[1]);

        if (result < objs.size()) {
            log.error("Service bulk create {} with some failed", objs);
        }

        return new ServiceResult(result);
    }

    @Override
    public ServiceResult bulkUpdate(List<Model> objs) {
        if (objs.size() == 0) {
            return new ServiceResult(0);
        }

        String tableName = getTableName(objs.get(0));
        if (StringUtils.isEmpty(tableName)) {
            log.error("Service bulk update {} with no table name found", objs);
            return new ServiceResult(0);
        }

        Object[] updateParams = convertUpdateParams(objs);
        if (updateParams == null) {
            log.error("Service bulk update {} has empty field", objs);
            return new ServiceResult(0);
        }

        Integer result = commonModelMapper.bulkUpdate(tableName, (String[]) updateParams[0],
                (Object[][]) updateParams[1]);

        if (result < objs.size()) {
            log.error("Service bulk update {} with some failed", objs);
        }

        return new ServiceResult(result);
    }

    @Override
    public ServiceResult bulkDelete(String[] ids) {
        String tableName = getTableName();
        if (StringUtils.isEmpty(tableName)) {
            log.error("Service bulk delete {} with no table name found", ids);
            return new ServiceResult(0);
        }

        Integer result = commonModelMapper.bulkDelete(tableName, ids);

        if (result < ids.length) {
            log.error("Service bulk delete {} with some failed", ids);
        }

        return new ServiceResult(result);
    }
}
