package cn.cyberpeace.mp.flow.service.field;

import cn.cyberpeace.mp.flow.model.FlowModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.ClassUtils;
import org.springframework.stereotype.Component;

@Component
public class FlowFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(FlowModel.class));

    public static final DynamicFields availableFields = new DynamicFields(FlowModel::getTitle, FlowModel::getFlowDesc,
            FlowModel::getInteractiveType, FlowModel::getFlowPlatform, FlowModel::getResourceType, FlowModel::getPcapFile,
            FlowModel::getScriptFile).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(FlowModel::getTitle, FlowModel::getFlowDesc,
            FlowModel::getInteractiveType, FlowModel::getFlowPlatform, FlowModel::getResourceType, FlowModel::getPcapFile,
            FlowModel::getScriptFile);

    public static final DynamicFields orderFields = new DynamicFields(new String[]{}).merge(BaseModelFields.readonlyFields);
}
