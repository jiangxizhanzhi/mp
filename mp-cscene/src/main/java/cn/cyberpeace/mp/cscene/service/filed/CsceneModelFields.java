 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneModelFields.java.
 // File size 1354.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.service.filed;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import org.springframework.stereotype.Component;

/**
 * @ClassName CsceneModelFields
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelFields
 */
@Component
public class CsceneModelFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(CsceneModel.class));

    public static final DynamicFields availableFields = new DynamicFields(ClassUtils.getBeanClassFields(CsceneModel.class));

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields(BaseModel::getId, BaseModel::getCreateTime,
            BaseModel::getUpdateTime);

    public static final DynamicFields searchFields = new DynamicFields(CsceneModel::getName, CsceneModel::getReferenceMetaData);

    public static final DynamicFields orderFields = new DynamicFields(BaseModel::getCreateTime, BaseModel::getUpdateTime);
}
