package cn.cyberpeace.mp.frontadmin.service.fields;

import cn.cyberpeace.mp.frontadmin.model.SysNoticeReadModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import org.springframework.stereotype.Component;


@Component
public class SysNoticeReadFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(SysNoticeReadModel.class));

    public static final DynamicFields availableFields = new DynamicFields(SysNoticeReadModel::getTenant, SysNoticeReadModel::getNoticeId,
            SysNoticeReadModel::getUser, SysNoticeReadModel::getIsReaded, SysNoticeReadModel::getIsNeedPush, SysNoticeReadModel::getReadCount).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(SysNoticeReadModel::getTenant, SysNoticeReadModel::getNoticeId, SysNoticeReadModel::getUser);

    public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}
