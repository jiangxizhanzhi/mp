package cn.cyberpeace.mp.auth.service.fields;

import cn.cyberpeace.mp.auth.model.TenantModel;
import cn.cyberpeace.mp.base.model.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.ClassUtils;
import org.springframework.stereotype.Component;

@Component
public class TenantFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(TenantModel.class));

    public static final DynamicFields fields = new DynamicFields(new String[]{
            "identifier", "name"
    }).merge(BaseModelFields.fields);

    public static final DynamicFields hiddenFields = allFields.subtract(fields);

    public static final DynamicFields readonlyFields = new DynamicFields(new String[]{

    }).merge(BaseModelFields.readonlyFields);
}
