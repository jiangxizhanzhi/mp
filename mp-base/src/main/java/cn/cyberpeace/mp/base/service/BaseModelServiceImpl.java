package cn.cyberpeace.mp.base.service;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.DynamicFields;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
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

    private void clearNotWritableFields(BaseModel obj) {
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

    private Object[] convertCreateParams(BaseModel[] objs) {
        DynamicFields allModelFields = getAllModelFields();
        if (allModelFields == null) {
            allModelFields = new DynamicFields(ClassUtils.getBeanClassFields(objs[0].getClass()));
        }
        String[] createFields = allModelFields.getFields();
        String[] createColumnFields = new String[createFields.length];
        for (int i = 0; i < createFields.length; i++) {
            createColumnFields[i] = TextUtils.humpToLine(createFields[i]);
        }

        Object[][] createFieldValuesList = new Object[createFields.length][objs.length];
        for (int i = 0; i < objs.length; i++) {
            BaseModel obj = objs[i];
            clearNotWritableFields(obj);
            obj.initDefault();

            Object[] createFieldValues = new Object[createFields.length];
            for (int j = 0; j < createFields.length; j++) {
                createFieldValues[j] = ClassUtils.getObjFieldValue(obj, createFields[j]);
            }
            createFieldValuesList[i] = createFieldValues;
        }

        return new Object[]{ createColumnFields, createFieldValuesList };
    }

    private Object[] convertUpdateParams(BaseModel[] objs) {
        String[] ids = new String[objs.length];
        for (int i = 0; i < objs.length; i++) {
            ids[i] = objs[i].getId();
        }

        BaseModel sampleObj = objs[0];
        clearNotWritableFields(sampleObj);
        sampleObj.initDefaultUpdateTime();
        sampleObj.setId(ids[0]);

        DynamicFields availableModelFields = getAvailableModelFields();
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

        Object[][] updateFieldValuesList = new Object[updateFields.size()][objs.length];
        for (int i = 0; i < objs.length; i++) {
            BaseModel obj = objs[i];
            clearNotWritableFields(obj);
            obj.initDefaultUpdateTime();
            obj.setId(ids[i]);

            Object[] updateFieldValues = new Object[updateFields.size()];
            for (int j = 0; j < updateFields.size(); j++) {
                updateFieldValues[j] = ClassUtils.getObjFieldValue(obj, updateFields.get(j));
            }
            updateFieldValuesList[i] = updateFieldValues;
        }

        return new Object[]{ updateColumnFields.toArray(new String[0]), updateFieldValuesList,
                updateFields.toArray(new String[0]) };
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
    public ServiceResult list(Integer page, Integer pageSize, String[] fields) {
        Integer[] limitOffset = getLimitOffset(page, pageSize);
        if (limitOffset == null) {
            return new ServiceResult(new ListObjResult<>());
        }

        DynamicFields validModelFields = getValidModelFields(fields);
        String[] selectFields = validModelFields == null ? null : validModelFields.getFields();
        List<Model> data = new ArrayList<>(modelMapper.list(limitOffset[0], limitOffset[1], selectFields));
        Integer total = modelMapper.total();

        return new ServiceResult(new ListObjResult<>(data, total, validModelFields));
    }

    @Override
    public ServiceResult get(String id, String[] fields) {
        DynamicFields validModelFields = getValidModelFields(fields);
        String[] selectFields = validModelFields == null ? null : validModelFields.getFields();
        Model obj = modelMapper.get(id, selectFields);
        return new ServiceResult(new ObjResult<>(obj, validModelFields));
    }

    @Override
    public ServiceResult create(Model obj, String[] fields) {
        Object[] createParams = convertCreateParams(new BaseModel[]{obj});
        Boolean result = modelMapper.create((String[]) createParams[0], ((Object[][]) createParams[1])[0]);

        if (result) {
            DynamicFields validModelFields = getValidModelFields(fields);
            return new ServiceResult(new ObjResult<>(obj, validModelFields));
        } else {
            return new ServiceResult(false);
        }
    }

    @Override
    public ServiceResult update(String id, Model obj, String[] fields) {
        Object[] updateParams = convertUpdateParams(new BaseModel[]{obj});
        Boolean result = modelMapper.update(id, (String[]) updateParams[0], ((Object[][]) updateParams[1])[0]);

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
    public ServiceResult bulkCreate(Model[] objs) {
        if (objs.length == 0) {
            return new ServiceResult(0);
        }

        Object[] createParams = convertCreateParams(objs);
        Integer result = modelMapper.bulkCreate((String[]) createParams[0], (Object[][]) createParams[1]);

        return new ServiceResult(result);
    }

    @Override
    public ServiceResult bulkUpdate(Model[] objs) {
        if (objs.length == 0) {
            return new ServiceResult(0);
        }

        Object[] updateParams = convertUpdateParams(objs);
        Integer result = modelMapper.bulkUpdate((String[]) updateParams[0], (Object[][]) updateParams[1]);

        return new ServiceResult(result);
    }

    @Override
    public ServiceResult bulkDelete(String[] ids) {
        return new ServiceResult(modelMapper.bulkDelete(ids));
    }
}
