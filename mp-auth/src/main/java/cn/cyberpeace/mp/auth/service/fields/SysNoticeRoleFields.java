package cn.cyberpeace.mp.auth.service.fields;

import cn.cyberpeace.mp.auth.model.SysNoticeRoleModel;
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
public class SysNoticeRoleFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(SysNoticeRoleModel.class));

    public static final DynamicFields availableFields = new DynamicFields(SysNoticeRoleModel::getTenant, SysNoticeRoleModel::getNoticeId,
            SysNoticeRoleModel::getRole).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(SysNoticeRoleModel::getTenant, SysNoticeRoleModel::getNoticeId, SysNoticeRoleModel::getRole);

    public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}
