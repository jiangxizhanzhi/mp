package cn.cyberpeace.mp.devops.cluster.service.fields;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.devops.cluster.response.InstancePageResponse;
import org.springframework.stereotype.Component;

/**
 * @author wanggang
 */
@Component
public class InstanceFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(InstancePageResponse.class));
}
