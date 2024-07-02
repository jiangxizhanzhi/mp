package cn.cyberpeace.mp.frontadmin.service.fields;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.frontadmin.model.SysModelVersionModel;

/**
 * @ClassName SysModelVersionFields
 * @Description
 * @Author guquan
 * @Date 2023/8/23 15:48
 * @Version 1.0.0
 * @see BaseModelFields
 */
public class SysModelVersionFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(SysModelVersionModel.class));

    public static final DynamicFields availableFields = new DynamicFields(SysModelVersionModel::getModelName, SysModelVersionModel::getModelVersion,
            SysModelVersionModel::getVisible).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(SysModelVersionModel::getModelName, SysModelVersionModel::getModelVersion, SysModelVersionModel::getVisible);

    public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}
