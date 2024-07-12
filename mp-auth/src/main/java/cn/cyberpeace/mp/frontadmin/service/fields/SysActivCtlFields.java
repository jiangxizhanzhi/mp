 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysActivCtlFields.java.
 // File size 1427.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service.fields;

import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.frontadmin.model.SysActivCtlModel;
import org.springframework.stereotype.Component;

/**
 * @ClassName SysActivCtlFields
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/2/15 11:13
 * @Version 1.0.0
 * @see BaseModelFields
 */
@Component
public class SysActivCtlFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(SysActivCtlModel.class));

    public static final DynamicFields availableFields = new DynamicFields(SysActivCtlModel::getTenant, SysActivCtlModel::getSuffix,
            SysActivCtlModel::getSysTimestamp).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields(TargetModel::getCreateTime,
            TargetModel::getUpdateTime);

    public static final DynamicFields searchFields = new DynamicFields(SysActivCtlModel::getTenant);

    public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}
