package cn.cyberpeace.mp.frontadmin.service.fields;

import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.frontadmin.model.SysActivCtlModel;
import org.springframework.stereotype.Component;


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
