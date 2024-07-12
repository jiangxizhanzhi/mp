 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysNoticeUserFields.java.
 // File size 1421.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service.fields;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeUserModel;
import org.springframework.stereotype.Component;

/**
 * @ClassName SysNoticeUserFields
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/9/20 11:13
 * @Version 1.0.0
 * @see BaseModelFields
 */
@Component
public class SysNoticeUserFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(SysNoticeUserModel.class));

    public static final DynamicFields availableFields = new DynamicFields(SysNoticeUserModel::getTenant, SysNoticeUserModel::getNoticeId,
            SysNoticeUserModel::getUser).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(SysNoticeUserModel::getTenant, SysNoticeUserModel::getNoticeId, SysNoticeUserModel::getUser);

    public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}
