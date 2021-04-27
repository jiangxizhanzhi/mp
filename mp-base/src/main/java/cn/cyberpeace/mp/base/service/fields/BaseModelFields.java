package cn.cyberpeace.mp.base.service.fields;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.util.ClassUtils;

public abstract class BaseModelFields extends BaseFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(BaseModel.class));

    public static final DynamicFields availableFields = new DynamicFields(new String[]{
            "id", "createTime", "updateTime"
    });

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields(new String[]{
            "id", "createTime", "updateTime"
    });

    public static final DynamicFields searchFields = new DynamicFields();

    public static final DynamicFields orderFields = new DynamicFields(new String[]{
            "id", "createTime", "updateTime"
    });

    public void clearNotWritableFields(BaseModel obj, Boolean keepId) {
        String id = keepId ? obj.getId() : null;

        clearNotWritableFields(obj);

        if (keepId) {
            obj.setId(id);
        }
    }
}
