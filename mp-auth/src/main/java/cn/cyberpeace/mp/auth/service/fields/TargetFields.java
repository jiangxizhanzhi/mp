package cn.cyberpeace.mp.auth.service.fields;

import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Component
public class TargetFields extends BaseModelFields {
    //public class TargetFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(TargetModel.class));

    public static final DynamicFields availableFields = new DynamicFields(TargetModel::getParent, TargetModel::getIdentifier, TargetModel::getTenant,
            TargetModel::getName, TargetModel::getPath, TargetModel::getType
    );

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields(TargetModel::getCreateTime,
            TargetModel::getUpdateTime);

    public static final DynamicFields searchFields = new DynamicFields(TargetModel::getParent, TargetModel::getIdentifier, TargetModel::getTenant,
            TargetModel::getName, TargetModel::getPath);

    public static final DynamicFields orderFields = new DynamicFields(TargetModel::getId, TargetModel::getCreateTime,
            TargetModel::getUpdateTime);
}
