package cn.cyberpeace.mp.base.service.fields;

import cn.cyberpeace.mp.base.model.DynamicFields;

public abstract class BaseModelFields extends BaseFields {
    public static final DynamicFields fields = new DynamicFields(new String[]{
            "id", "createTime", "updateTime"
    });
    public static final DynamicFields readonlyFields = new DynamicFields(new String[]{
            "id", "createTime", "updateTime"
    });
}
