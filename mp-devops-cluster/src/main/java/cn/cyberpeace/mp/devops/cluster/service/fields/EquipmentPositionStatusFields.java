package cn.cyberpeace.mp.devops.cluster.service.fields;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.devops.cluster.model.EquipmentPositionStatusModel;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/27
 */
@Component
public class EquipmentPositionStatusFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(EquipmentPositionStatusModel.class));

    public static final DynamicFields availableFields = new DynamicFields(EquipmentPositionStatusModel::getCode,
            EquipmentPositionStatusModel::getChName, EquipmentPositionStatusModel::getEnName).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(EquipmentPositionStatusModel::getCode,
            EquipmentPositionStatusModel::getChName, EquipmentPositionStatusModel::getEnName);

    public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}
