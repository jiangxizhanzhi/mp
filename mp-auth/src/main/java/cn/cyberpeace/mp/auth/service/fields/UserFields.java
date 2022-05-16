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
            UserModel::getExpiredTime, UserModel::getLastLogin, UserModel::getRes
    ).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(UserModel::getUsername, UserModel::getName,
            UserModel::getNickname, UserModel::getEmail);

    public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}
