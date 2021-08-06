package cn.cyberpeace.mp.auth.service.fields;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.ClassUtils;
import org.springframework.stereotype.Component;

@Component
public class OrgFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(OrgModel.class));

    public static final DynamicFields availableFields = new DynamicFields(OrgModel::getTenant, OrgModel::getParent,
            OrgModel::getIdentifier, OrgModel::getName, OrgModel::getPath
    ).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(OrgModel::getIdentifier, OrgModel::getName);

    public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}
