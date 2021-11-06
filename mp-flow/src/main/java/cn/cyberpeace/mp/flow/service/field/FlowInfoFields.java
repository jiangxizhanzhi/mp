package cn.cyberpeace.mp.flow.service.field;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import org.springframework.stereotype.Component;

@Component
public class FlowInfoFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(FlowInfoModel.class));

    public static final DynamicFields availableFields = new DynamicFields(ClassUtils.getBeanClassFields(FlowInfoModel.class)).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(FlowInfoModel::getTitle, FlowInfoModel::getFlowDesc,
            FlowInfoModel::getInteractiveType, FlowInfoModel::getResourceType, FlowInfoModel::getPcapFile,
            FlowInfoModel::getScriptFile);

    public static final DynamicFields orderFields = new DynamicFields(new String[]{}).merge(BaseModelFields.readonlyFields);
}
