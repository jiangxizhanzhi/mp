package cn.cyberpeace.mp.auth.service.fields;

import cn.cyberpeace.mp.auth.model.OrgPermissionModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.ClassUtils;
import org.springframework.stereotype.Component;

@Component
public class OrgPermissionFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(
            OrgPermissionModel.class));

    public static final DynamicFields availableFields = new DynamicFields(new String[]{
            "tenant", "owner", "target", "canRead", "canCreate", "canUpdate", "canDelete"
    }).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields(new String[]{
    }).merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(new String[]{
    });

    public static final DynamicFields orderFields = new DynamicFields(new String[]{
    }).merge(BaseModelFields.readonlyFields);
}
