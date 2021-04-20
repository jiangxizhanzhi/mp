package cn.cyberpeace.mp.base.service;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.DynamicFields;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.base.util.TextUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class BaseModelServiceImpl<Model extends BaseModel,
        ModelMapper extends BaseModelMapper<Model>> implements BaseModelService<Model> {

    private Class<? extends BaseModelFields> modelFieldsClass;

    private ModelMapper modelMapper;

    private DynamicFields getAllModelFields() {
        DynamicFields allModelFields = null;
        if (modelFieldsClass != null) {
            allModelFields = ClassUtils.getStaticFieldValue(modelFieldsClass, "allFields",
                    DynamicFields.class);
        }
        return allModelFields;
    }

    private DynamicFields getAvailableModelFields() {
        DynamicFields availableFields = null;
        if (modelFieldsClass != null) {
            availableFields = ClassUtils.getStaticFieldValue(modelFieldsClass, "fields",
                    DynamicFields.class);
        }
        return availableFields;
    }

    private DynamicFields getHiddenModelFields() {
        DynamicFields hiddenFields = null;
        if (modelFieldsClass != null) {
            hiddenFields = ClassUtils.getStaticFieldValue(modelFieldsClass, "hiddenFields",
                    DynamicFields.class);
        }
        return hiddenFields;
    }

    private DynamicFields getReadonlyModelFields() {
        DynamicFields readonlyModelFields = null;
        if (modelFieldsClass != null) {
            readonlyModelFields = ClassUtils.getStaticFieldValue(modelFieldsClass, "readonlyFields",
                    DynamicFields.class);
        }
        return readonlyModelFields;
    }

    private DynamicFields getValidModelFields(String[] fields) {
        DynamicFields validModelFields = null;
        if (modelFieldsClass != null && fields != null) {
            DynamicFields availableFields = getAvailableModelFields();
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
        DynamicFields hiddenModelFields = getHiddenModelFields();
        if (hiddenModelFields != null) {
            for (String fieldName: hiddenModelFields.getFields()) {
                ClassUtils.setBeanObjFieldValue(obj, fieldName, null);
            }
        }

        DynamicFields readonlyModelFields = getReadonlyModelFields();
        if (readonlyModelFields != null) {
            for (String fieldName: readonlyModelFields.getFields()) {
                ClassUtils.setBeanObjFieldValue(obj, fieldName, null);
            }
        }
    }

    private Integer[] getLimitOffset(Integer page, Integer pageSize) {
        Integer limit = null;
        if (pageSize != null && pageSize.compareTo(0) >= 0) {
            limit = pageSize;
        }
        if (limit != null && limit.compareTo(0) == 0) {
            return null;
        }

        Integer offset = null;
        if (page != null && page.compareTo(0) > 0) {
            offset = page - 1;
        }
        if (limit == null && page != null) {
            return null;
        }

        return new Integer[]{ limit, offset };
    }

    @Override
    public ListObjResult<Model> list(Integer page, Integer pageSize, String[] fields) {
        Integer[] limitOffset = getLimitOffset(page, pageSize);
        if (limitOffset == null) {
            return new ListObjResult<>();
        }

        DynamicFields validModelFields = getValidModelFields(fields);
        String[] selectFields = validModelFields == null ? null : validModelFields.getFields();
        List<Model> data = new ArrayList<>(modelMapper.list(limitOffset[0], limitOffset[1], selectFields));
        Integer total = modelMapper.total();

        return new ListObjResult<>(data, total, validModelFields);
    }

    @Override
    public ObjResult<Model> get(String id, String[] fields) {
        DynamicFields validModelFields = getValidModelFields(fields);
        String[] selectFields = validModelFields == null ? null : validModelFields.getFields();
        Model obj = modelMapper.get(id, selectFields);
        return new ObjResult<>(obj, validModelFields);
    }

    @Override
    public ObjResult<Model> create(Model obj, String[] fields) {
        clearNotWritableFields(obj);
        obj.initDefault();

        DynamicFields allModelFields = getAllModelFields();
        if (allModelFields == null) {
            allModelFields = new DynamicFields(ClassUtils.getBeanClassFields(obj.getClass()));
        }
        String[] createFields = allModelFields.getFields();
        String[] createColumnFields = new String[createFields.length];
        Object[] createFieldValues = new Object[createFields.length];
        for (int i = 0; i < createFields.length; i++) {
            createColumnFields[i] = TextUtils.humpToLine(createFields[i]);
            createFieldValues[i] = ClassUtils.getObjFieldValue(obj, createFields[i]);
        }

        modelMapper.create(obj, createColumnFields, createFieldValues);

        DynamicFields validModelFields = getValidModelFields(fields);
        return new ObjResult<>(obj, validModelFields);
    }

    @Override
    public ObjResult<Model> update(String id, Model obj, String[] fields) {
        clearNotWritableFields(obj);
        obj.initDefaultUpdateTime();

        DynamicFields availableModelFields = getAvailableModelFields();
        if (availableModelFields == null) {
            availableModelFields = new DynamicFields(ClassUtils.getBeanClassFields(obj.getClass()));
        }
        List<String> updateFields = new ArrayList<>();
        List<String> updateColumnFields = new ArrayList<>();
        List<Object> updateFieldValues = new ArrayList<>();
        for (String fieldName: availableModelFields.getFields()) {
            Object fieldValue = ClassUtils.getObjFieldValue(obj, fieldName);
            if (fieldValue != null) {
                updateFields.add(fieldName);
                updateColumnFields.add(TextUtils.humpToLine(fieldName));
                updateFieldValues.add(fieldValue);
            }
        }

        modelMapper.update(id, obj, updateColumnFields.toArray(new String[0]), updateFieldValues.toArray());

        DynamicFields validModelFields = getValidModelFields(fields);
        if (validModelFields == null) {
            validModelFields = new DynamicFields(updateFields);
        } else {
            validModelFields = validModelFields.intersect(updateFields);
        }

        return new ObjResult<>(obj, validModelFields);
    }

    @Override
    public Boolean delete(String id) {
        return modelMapper.delete(id);
    }
}
