package cn.cyberpeace.mp.flow.v2.service.fields;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskModel;
import org.springframework.stereotype.Component;

@Component
public class FlowTaskFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(FlowTaskModel.class));

    public static final DynamicFields availableFields = new DynamicFields(
            FlowTaskModel::getName, FlowTaskModel::getDescription, FlowTaskModel::getCategory,
            FlowTaskModel::getBandwidth, FlowTaskModel::getLoadMode, FlowTaskModel::getFilterProtocols,
            FlowTaskModel::getFlows
            ).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(
            FlowTaskModel::getName, FlowTaskModel::getDescription
    );

    public static final DynamicFields orderFields = new DynamicFields(
            FlowTaskModel::getName
    ).merge(BaseModelFields.readonlyFields);
}
