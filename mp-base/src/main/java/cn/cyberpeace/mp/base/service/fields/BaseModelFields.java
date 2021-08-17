package cn.cyberpeace.mp.base.service.fields;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.util.ClassUtils;

public abstract class BaseModelFields extends BaseFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(BaseModel.class));

    public static final DynamicFields availableFields = new DynamicFields(BaseModel::getId, BaseModel::getCreateTime,
            BaseModel::getUpdateTime);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields(BaseModel::getId, BaseModel::getCreateTime,
            BaseModel::getUpdateTime);

    public static final DynamicFields searchFields = new DynamicFields();

    public static final DynamicFields orderFields = new DynamicFields(BaseModel::getCreateTime,
            BaseModel::getUpdateTime);

    public void clearNotWritableFields(BaseModel obj, boolean keepId) {
        String id = keepId ? obj.getId() : null;

        clearNotWritableFields(obj);

        if (keepId) {
            obj.setId(id);
        }
    }
}
