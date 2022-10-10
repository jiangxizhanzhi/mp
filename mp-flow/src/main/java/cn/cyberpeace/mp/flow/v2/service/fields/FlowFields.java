package cn.cyberpeace.mp.flow.v2.service.fields;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.flow.v2.model.FlowModel;
import org.springframework.stereotype.Component;

@Component
public class FlowFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(FlowModel.class));

    public static final DynamicFields availableFields = new DynamicFields(
            FlowModel::getName, FlowModel::getDescription, FlowModel::getCategory, FlowModel::getInteractiveType,
            FlowModel::getResourceType, FlowModel::getProtocols, FlowModel::getPcapFile, FlowModel::getProtocolConfigs,
            FlowModel::getProtocolFlowUnits, FlowModel::getScriptType, FlowModel::getScriptFile,
            FlowModel::getScriptParams
    ).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(
            FlowModel::getName, FlowModel::getDescription, FlowModel::getInteractiveType
    );

    public static final DynamicFields orderFields = new DynamicFields(
            FlowModel::getName
    ).merge(BaseModelFields.readonlyFields);
}
