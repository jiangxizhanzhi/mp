package cn.cyberpeace.mp.cscene.flow.field;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskRecordModel;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/25
 */
@Component
public class FlowTaskRecordFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(
            FlowTaskRecordModel.class));

    public static final DynamicFields availableFields = allFields;

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields();

    public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}
