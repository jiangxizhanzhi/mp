package cn.cyberpeace.mp.auth.service.fields;

import cn.cyberpeace.mp.auth.model.TenantUserModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.ClassUtils;
import org.springframework.stereotype.Component;

@Component
public class TenantUserFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(TenantUserModel.class));

    public static final DynamicFields availableFields = new DynamicFields(TenantUserModel::getTenant,
            TenantUserModel::getOrg, TenantUserModel::getUser
    ).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields();

    public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}
