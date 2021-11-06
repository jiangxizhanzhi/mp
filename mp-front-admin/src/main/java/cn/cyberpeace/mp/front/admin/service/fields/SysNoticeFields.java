package cn.cyberpeace.mp.front.admin.service.fields;

import cn.cyberpeace.mp.front.admin.model.SysNoticeModel;
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
public class SysNoticeFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(SysNoticeModel.class));

    public static final DynamicFields availableFields = new DynamicFields(SysNoticeModel::getTenant, SysNoticeModel::getTitle,
            SysNoticeModel::getPublisher).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(SysNoticeModel::getTenant, SysNoticeModel::getTitle, SysNoticeModel::getPublisher, SysNoticeModel::getType);

    public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}
