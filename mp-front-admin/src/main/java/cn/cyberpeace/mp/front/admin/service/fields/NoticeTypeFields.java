package cn.cyberpeace.mp.front.admin.service.fields;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.front.admin.model.NoticeTypeModel;
import cn.cyberpeace.mp.front.admin.model.SysNoticeModel;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Component
public class NoticeTypeFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(NoticeTypeModel.class));

    public static final DynamicFields availableFields = new DynamicFields(NoticeTypeModel::getCode,
            NoticeTypeModel::getChName, NoticeTypeModel::getEnName).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(NoticeTypeModel::getCode,
            NoticeTypeModel::getChName, NoticeTypeModel::getEnName);

    public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}
