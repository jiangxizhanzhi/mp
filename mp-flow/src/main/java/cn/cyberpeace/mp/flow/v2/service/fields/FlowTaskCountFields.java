package cn.cyberpeace.mp.flow.v2.service.fields;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskCountModel;
import org.springframework.stereotype.Component;

@Component
public class FlowTaskCountFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(FlowTaskCountModel.class));

    public static final DynamicFields availableFields = allFields;

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = allFields;

    public static final DynamicFields orderFields = allFields;
}
