package cn.cyberpeace.mp.auth.service.fields;

import cn.cyberpeace.mp.auth.model.UserPermissionModel;
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
public class UserPermissionFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(UserPermissionModel.class));

    public static final DynamicFields availableFields = new DynamicFields(UserPermissionModel::getTenant,
            UserPermissionModel::getOwner, UserPermissionModel::getTarget, UserPermissionModel::getCanRead,
            UserPermissionModel::getCanCreate, UserPermissionModel::getCanUpdate, UserPermissionModel::getCanDelete
    ).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields();

    public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}
