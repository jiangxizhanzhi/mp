package cn.cyberpeace.mp.frontadmin.service.fields;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.frontadmin.model.SysDeployLogModel;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2023/05/31 16:13
 */
@Component
public class SysDeployLogFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(SysDeployLogModel.class));

    public static final DynamicFields availableFields = new DynamicFields(SysDeployLogModel::getSystem, SysDeployLogModel::getVersion,
            SysDeployLogModel::getDescription, SysDeployLogModel::getStatus).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields();

    public static final DynamicFields searchFields = new DynamicFields(SysDeployLogModel::getSystem, SysDeployLogModel::getVersion);

    public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}
