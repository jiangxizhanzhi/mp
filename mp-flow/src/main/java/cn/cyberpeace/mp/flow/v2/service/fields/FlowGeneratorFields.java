package cn.cyberpeace.mp.flow.v2.service.fields;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.flow.v2.model.FlowGeneratorModel;
import org.springframework.stereotype.Component;

@Component
public class FlowGeneratorFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(FlowGeneratorModel.class));

    public static final DynamicFields availableFields = new DynamicFields(
            FlowGeneratorModel::getName, FlowGeneratorModel::getType, FlowGeneratorModel::getIp,
            FlowGeneratorModel::getServer, FlowGeneratorModel::getUsername, FlowGeneratorModel::getPassword,
            FlowGeneratorModel::getToken, FlowGeneratorModel::getResourceId
    ).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(
            FlowGeneratorModel::getName
    );

    public static final DynamicFields orderFields = new DynamicFields(
            FlowGeneratorModel::getName
    ).merge(BaseModelFields.readonlyFields);
}
