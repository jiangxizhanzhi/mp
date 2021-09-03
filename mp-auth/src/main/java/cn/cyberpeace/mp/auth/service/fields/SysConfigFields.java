package cn.cyberpeace.mp.auth.service.fields;

import cn.cyberpeace.mp.auth.model.SysConfigModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.ClassUtils;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Component
public class SysConfigFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(SysConfigModel.class));

    public static final DynamicFields availableFields = new DynamicFields(SysConfigModel::getTenant, SysConfigModel::getKeyName,
            SysConfigModel::getValue).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(SysConfigModel::getTenant, SysConfigModel::getKeyName, SysConfigModel::getValue);

    public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}
