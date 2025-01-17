 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseModelFields.java.
 // File size 1261.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:11 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.service.fields;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;

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
