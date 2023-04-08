package cn.cyberpeace.mp.cscene.service.filed;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.cscene.model.ProbeOperateLogModel;
import org.springframework.stereotype.Component;

/**
 * @BelongsPackage: cn.cyberpeace.mp.cscene.service.filed
 * @Author: hhuang
 * @CreateTime: 2023/04/07  16:26
 * @Description:
 * @Version: 1.0
 */
@Component
public class ProbeOperateLogFields extends BaseModelFields{
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(ProbeOperateLogModel.class));

    public static final DynamicFields availableFields = new DynamicFields(ProbeOperateLogModel::getScene, ProbeOperateLogModel::getType,
        ProbeOperateLogModel::getOperate).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(ProbeOperateLogModel::getScene, ProbeOperateLogModel::getType, ProbeOperateLogModel::getOperate);

    public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}
