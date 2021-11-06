package cn.cyberpeace.mp.flow.service.field;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.flow.model.FlowScriptDemoModel;
import org.springframework.stereotype.Component;

@Component
public class FlowScriptDemoFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(FlowScriptDemoModel.class));

    public static final DynamicFields availableFields = new DynamicFields(ClassUtils.getBeanClassFields(FlowScriptDemoModel.class)).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(FlowScriptDemoModel::getScriptName, FlowScriptDemoModel::getUrl);

    public static final DynamicFields orderFields = new DynamicFields(new String[]{}).merge(BaseModelFields.readonlyFields);
}
