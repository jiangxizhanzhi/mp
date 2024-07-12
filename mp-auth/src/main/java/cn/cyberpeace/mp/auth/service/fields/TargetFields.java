 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TargetFields.java.
 // File size 1550.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.service.fields;

import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import org.springframework.stereotype.Component;

/**
 * @ClassName TargetFields
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see BaseModelFields
 */
@Component
public class TargetFields extends BaseModelFields {
    //public class TargetFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(TargetModel.class));

    public static final DynamicFields availableFields = new DynamicFields(TargetModel::getParent, TargetModel::getIdentifier, TargetModel::getTenant,
            TargetModel::getName, TargetModel::getPath, TargetModel::getType
    );

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields(TargetModel::getCreateTime,
            TargetModel::getUpdateTime);

    public static final DynamicFields searchFields = new DynamicFields(TargetModel::getParent, TargetModel::getIdentifier, TargetModel::getTenant,
            TargetModel::getName, TargetModel::getPath);

    public static final DynamicFields orderFields = new DynamicFields(TargetModel::getId, TargetModel::getCreateTime,
            TargetModel::getUpdateTime);
}
