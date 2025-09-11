package cn.cyberpeace.mp.devopscluster.service.fields;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.devopscluster.model.PlatformModel;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author guquan
 * @Date：2025/9/9 11:13
 */
@Component
public class PlatformFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(PlatformModel.class));

    public static final DynamicFields availableFields = new DynamicFields(ClassUtils.getBeanClassFields(PlatformModel.class)).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(ClassUtils.getBeanClassFields(PlatformModel.class));

    public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}
