package cn.cyberpeace.mp.auth.service.fields;

import cn.cyberpeace.mp.auth.model.TenantUserModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.ClassUtils;
import org.springframework.stereotype.Component;

@Component
public class TenantUserFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(TenantUserModel.class));

    public static final DynamicFields availableFields = new DynamicFields(new String[]{
            "tenant", "org", "user"
    }).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields(new String[]{
    }).merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(new String[]{
    });

    public static final DynamicFields orderFields = new DynamicFields(new String[]{
    }).merge(BaseModelFields.readonlyFields);
}
