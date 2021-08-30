package cn.cyberpeace.mp.flow.service.field;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import org.springframework.stereotype.Component;

@Component
public class FlowTaskFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(FlowTaskModel.class));

    public static final DynamicFields availableFields = new DynamicFields(ClassUtils.getBeanClassFields(FlowTaskModel.class)).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(FlowTaskModel::getTitle, FlowTaskModel::getTaskDesc, FlowTaskModel::getTaskStatus,
            FlowTaskModel::getFlowId, FlowTaskModel::getFlowPlatform, FlowTaskModel::getThirdFlowId, FlowTaskModel::getThirdFlowName, FlowTaskModel::getCount,
            FlowTaskModel::getDelay, FlowTaskModel::getIntervalTime, FlowTaskModel::getOriginIps, FlowTaskModel::getTargetIps,
            FlowTaskModel::getActuatorId, FlowTaskModel::getThirdTaskId, FlowTaskModel::getFlowPortId);

    public static final DynamicFields orderFields = new DynamicFields(new String[]{}).merge(BaseModelFields.readonlyFields);
}
