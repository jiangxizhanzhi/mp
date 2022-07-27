package cn.cyberpeace.mp.situationaware.service.fields;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.situationaware.model.ProbeTypeModel;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/27
 */
@Component
public class ProbeTypeFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(ProbeTypeModel.class));

    public static final DynamicFields availableFields = new DynamicFields(ProbeTypeModel::getCode,
            ProbeTypeModel::getChName, ProbeTypeModel::getEnName).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(ProbeTypeModel::getCode,
            ProbeTypeModel::getChName, ProbeTypeModel::getEnName);

    public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}

