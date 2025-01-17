 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OptionFields.java.
 // File size 1304.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.common.service.fields;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.common.model.OptionModel;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/28
 */
@Component
public class OptionFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(OptionModel.class));

    public static final DynamicFields availableFields = new DynamicFields(ClassUtils.getBeanClassFields(OptionModel.class)).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(new DynamicFields(BaseModel::getCreateTime, BaseModel::getUpdateTime));

    public static final DynamicFields searchFields = new DynamicFields(ClassUtils.getBeanClassFields(OptionModel.class));

    public static final DynamicFields orderFields = new DynamicFields(new String[]{}).merge(BaseModelFields.readonlyFields);
}
