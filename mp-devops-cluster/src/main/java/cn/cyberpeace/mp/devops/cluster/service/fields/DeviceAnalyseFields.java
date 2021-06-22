package cn.cyberpeace.mp.devops.cluster.service.fields;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.devops.cluster.model.DeviceAnalyseModel;
import org.springframework.stereotype.Component;

/**
 * @author wanggang
 */
@Component
public class DeviceAnalyseFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(DeviceAnalyseModel.class));

    public static final DynamicFields availableFields = new DynamicFields(new String[]{
            "identifier", "name"
    }).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields(new String[]{
    }).merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(new String[]{
            "identifier", "name",
    });

    public static final DynamicFields orderFields = new DynamicFields(new String[]{
    }).merge(BaseModelFields.readonlyFields);
}
