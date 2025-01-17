 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserFields.java.
 // File size 1731.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service.fields;

import cn.cyberpeace.mp.auth.model.UserModel;
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
public class UserFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(UserModel.class));

    public static final DynamicFields availableFields = new DynamicFields(UserModel::getTenant,
            UserModel::getUsername, UserModel::getName, UserModel::getNickname,
            UserModel::getAvatar, UserModel::getBirthday, UserModel::getEmail,
            UserModel::getIsEnabled, UserModel::getIsAudited, UserModel::getInitCheck, UserModel::getDescription, UserModel::getDateJoined,
            UserModel::getExpiredTime, UserModel::getLastLogin, UserModel::getIdentifier, UserModel::getIdCard, UserModel::getMobile, UserModel::getAddress
    ).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields(UserModel::getCreateTime, UserModel::getUpdateTime);

    public static final DynamicFields searchFields = new DynamicFields(UserModel::getUsername, UserModel::getName,
            UserModel::getNickname, UserModel::getEmail, UserModel::getMobile);

    public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}
