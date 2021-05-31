package cn.cyberpeace.mp.auth.service.fields;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.ClassUtils;
import org.springframework.stereotype.Component;

@Component
public class RoleFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(RoleModel.class));

    public static final DynamicFields availableFields = new DynamicFields(RoleModel::getTenant, RoleModel::getOrg,
            RoleModel::getParent, RoleModel::getIdentifier, RoleModel::getName
    ).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(RoleModel::getIdentifier, RoleModel::getName);

    public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}
