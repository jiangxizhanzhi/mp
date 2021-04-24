package cn.cyberpeace.mp.base.service;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.condition.*;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.service.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.base.util.TextUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
public abstract class BaseModelServiceImpl<Model extends BaseModel,
        ModelMapper extends BaseModelMapper<Model>> implements BaseModelService<Model> {

    public static final Class<? extends BaseModelFields> modelFieldsClass = null;

    public static final Class<? extends CustomCondition> customConditionClass = null;

    private ModelMapper modelMapper;

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
        Object[] createParams = convertCreateParams(new ArrayList<>(){{ add(obj); }});
        if (createParams == null) {
            return new ServiceResult(false);
        }

        Boolean result = modelMapper.create((String[]) createParams[0], ((Object[][]) createParams[1])[0]);

        if (result) {
            DynamicFields validModelFields = getValidModelFields(fields);
            return new ServiceResult(new ObjResult<>(obj, validModelFields));
        } else {
            return new ServiceResult(false);
        }
    }

    @Override
    public ServiceResult update(Model obj, String[] fields) {
        Object[] updateParams = convertUpdateParams(new ArrayList<>(){{ add(obj); }});
        if (updateParams == null) {
            return new ServiceResult(false);
        }

        Boolean result = modelMapper.update(obj.getId(), (String[]) updateParams[0], ((Object[][]) updateParams[1])[0]);

        DynamicFields validModelFields = getValidModelFields(fields);
        if (validModelFields == null) {
            validModelFields = new DynamicFields((String[]) updateParams[2]);
        } else {
            validModelFields = validModelFields.intersect((String[]) updateParams[2]);
        }

        if (result) {
            return new ServiceResult(new ObjResult<>(obj, validModelFields));
        } else {
            return new ServiceResult(false);
        }
    }

    @Override
    public ServiceResult delete(String id) {
        return new ServiceResult(modelMapper.delete(id));
    }

    @Override
    public ServiceResult bulkCreate(List<Model> objs) {
        if (objs.size() == 0) {
            return new ServiceResult(0);
        }

        Object[] createParams = convertCreateParams(objs);
        if (createParams == null) {
            return new ServiceResult(0);
        }
        Integer result = modelMapper.bulkCreate((String[]) createParams[0], (Object[][]) createParams[1]);

        return new ServiceResult(result);
    }

    @Override
    public ServiceResult bulkUpdate(List<Model> objs) {
        if (objs.size() == 0) {
            return new ServiceResult(0);
        }

        Object[] updateParams = convertUpdateParams(objs);
        if (updateParams == null) {
            return new ServiceResult(0);
        }
        Integer result = modelMapper.bulkUpdate((String[]) updateParams[0], (Object[][]) updateParams[1]);

        return new ServiceResult(result);
    }

    @Override
    public ServiceResult bulkDelete(String[] ids) {
        return new ServiceResult(modelMapper.bulkDelete(ids));
    }
}
