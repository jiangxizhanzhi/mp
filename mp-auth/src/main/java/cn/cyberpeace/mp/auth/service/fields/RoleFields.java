 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RoleFields.java.
 // File size 1258.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service.fields;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Component
public class RoleFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(RoleModel.class));

    public static final DynamicFields availableFields = new DynamicFields(RoleModel::getTenant, RoleModel::getOrg,
           RoleModel::getIdentifier, RoleModel::getName
    ).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(RoleModel::getIdentifier, RoleModel::getName);

    public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}
