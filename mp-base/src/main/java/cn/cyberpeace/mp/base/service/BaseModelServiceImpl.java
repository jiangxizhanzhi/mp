package cn.cyberpeace.mp.base.service;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.DynamicFields;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.util.ClassUtils;
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
            allModelFields = ClassUtils.getStaticFieldValue(modelFieldsClass, "fields",
                    DynamicFields.class);
        }
        return allModelFields;
    }

    private DynamicFields getReadonlyModelFields() {
        DynamicFields readonlyModelFields = null;
        if (modelFieldsClass != null) {
            readonlyModelFields = ClassUtils.getStaticFieldValue(modelFieldsClass, "readonlyFields",
                    DynamicFields.class);
        }
        return readonlyModelFields;
    }

    private DynamicFields getValidModelFields(List<String> fields) {
        DynamicFields validModelFields = null;
        if (modelFieldsClass != null && fields != null) {
            DynamicFields allModelFields = getAllModelFields();
            if (allModelFields != null) {
                validModelFields = allModelFields.intersect(fields);
                if (!validModelFields.hasValidFields()) {
                    validModelFields = null;
                }
            }
        }

        return validModelFields;
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
    public ListObjResult<Model> list(Integer page, Integer pageSize, List<String> fields) {
        Integer[] limitOffset = getLimitOffset(page, pageSize);
        if (limitOffset == null) {
            return new ListObjResult<>();
        }

        DynamicFields validModelFields = getValidModelFields(fields);
        List<String> selectFields = validModelFields == null ? null : validModelFields.getFields();
        List<Model> data = new ArrayList<>(modelMapper.list(limitOffset[0], limitOffset[1], selectFields));
        Integer total = modelMapper.total();

        return new ListObjResult<>(data, total, validModelFields);
    }

    @Override
    public ObjResult<Model> get(String id, List<String> fields) {
        DynamicFields validModelFields = getValidModelFields(fields);
        List<String> selectFields = validModelFields == null ? null : validModelFields.getFields();
        Model obj = modelMapper.get(id, selectFields);
        return new ObjResult<>(obj, validModelFields);
    }

    @Override
    public ObjResult<Model> create(Model obj, List<String> fields) {
        DynamicFields readonlyModelFields = getReadonlyModelFields();
        readonlyModelFields.getFields().forEach(fieldName -> {
            ClassUtils.setBeanObjFieldValue(obj, fieldName, null);
        });
        obj.initDefault();

        DynamicFields allModelFields = getAllModelFields();
        List<String> createFields = allModelFields == null ? null : allModelFields.getFields();

        modelMapper.create(obj, createFields);

        DynamicFields validModelFields = getValidModelFields(fields);
        return new ObjResult<>(obj, validModelFields);
    }

    @Override
    public ObjResult<Model> update(String id, Model obj, List<String> fields) {
        DynamicFields readonlyModelFields = getReadonlyModelFields();
        readonlyModelFields.getFields().forEach(fieldName -> {
            ClassUtils.setBeanObjFieldValue(obj, fieldName, null);
        });
        obj.initDefaultUpdateTime();

        DynamicFields validModelFields = getValidModelFields(fields);
        List<String> updateFields = validModelFields == null ? null : validModelFields.getFields();
        modelMapper.update(id, obj, updateFields);

        return new ObjResult<>(obj, validModelFields);
    }

    @Override
    public Boolean delete(String id) {
        return modelMapper.delete(id);
    }
}
