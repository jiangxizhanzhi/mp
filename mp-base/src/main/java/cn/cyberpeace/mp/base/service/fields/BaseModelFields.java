 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseModelFields.java.
 // File size 1496.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.service.fields;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;

/**
 * @ClassName BaseModelFields
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseFields
 */
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

    /**
     * clearNotWritableFields
     *
     * @param obj
     * @param keepId
     */
    public void clearNotWritableFields(BaseModel obj, boolean keepId) {
        String id = keepId ? obj.getId() : null;

        clearNotWritableFields(obj);

        if (keepId) {
            obj.setId(id);
        }
    }
}
