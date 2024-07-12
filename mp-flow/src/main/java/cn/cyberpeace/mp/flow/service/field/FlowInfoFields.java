 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowInfoFields.java.
 // File size 1472.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.service.field;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import org.springframework.stereotype.Component;

/**
 * @ClassName FlowInfoFields
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelFields
 */
@Component
public class FlowInfoFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(FlowInfoModel.class));

    public static final DynamicFields availableFields = new DynamicFields(ClassUtils.getBeanClassFields(FlowInfoModel.class)).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(new DynamicFields(BaseModel::getCreateTime, BaseModel::getUpdateTime));

    public static final DynamicFields searchFields = new DynamicFields(FlowInfoModel::getTitle, FlowInfoModel::getFlowDesc,
            FlowInfoModel::getInteractiveType, FlowInfoModel::getResourceType, FlowInfoModel::getPcapFile,
            FlowInfoModel::getScriptFile);

    public static final DynamicFields orderFields = allFields;
}
