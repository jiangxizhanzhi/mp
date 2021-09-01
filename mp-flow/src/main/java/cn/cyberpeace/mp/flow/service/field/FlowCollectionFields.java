package cn.cyberpeace.mp.flow.service.field;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.flow.model.FlowCollectionModel;
import org.springframework.stereotype.Component;

@Component
public class FlowCollectionFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(FlowCollectionModel.class));

    public static final DynamicFields availableFields = new DynamicFields(ClassUtils.getBeanClassFields(FlowCollectionModel.class)).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(FlowCollectionModel::getTitle, FlowCollectionModel::getCollectionDesc,
            FlowCollectionModel::getCategory, FlowCollectionModel::getCreateTime);

    public static final DynamicFields orderFields = new DynamicFields(new String[]{}).merge(BaseModelFields.readonlyFields);
}
