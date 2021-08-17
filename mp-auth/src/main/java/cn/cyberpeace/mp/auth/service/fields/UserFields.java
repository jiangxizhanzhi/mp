package cn.cyberpeace.mp.auth.service.fields;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.ClassUtils;
import org.springframework.stereotype.Component;

@Component
public class UserFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(UserModel.class));

    public static final DynamicFields availableFields = new DynamicFields(UserModel::getTenant,
            UserModel::getIdentifier, UserModel::getUsername, UserModel::getName, UserModel::getNickname,
            UserModel::getAvatar, UserModel::getBirthday, UserModel::getEmail, UserModel::getMobile,
            UserModel::getIdCard, UserModel::getAddress, UserModel::getIsEnabled, UserModel::getIsAudited, UserModel::getInitCheck, UserModel::getDescription, UserModel::getDateJoined,
            UserModel::getExpiredTime, UserModel::getLastLogin
    ).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(UserModel::getUsername, UserModel::getName,
            UserModel::getNickname, UserModel::getEmail,UserModel::getMobile
    );

    public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}
