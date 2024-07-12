 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ClientDetailsFields.java.
 // File size 1786.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.service.fields;

import cn.cyberpeace.mp.auth.model.ClientDetailsModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import org.springframework.stereotype.Component;

/**
 * @ClassName ClientDetailsFields
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see BaseModelFields
 */
@Component
public class ClientDetailsFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(ClientDetailsModel.class));

    public static final DynamicFields availableFields = new DynamicFields(ClientDetailsModel::getClientId,
            ClientDetailsModel::getResourceIds, ClientDetailsModel::getClientSecret, ClientDetailsModel::getType, ClientDetailsModel::getScope,
            ClientDetailsModel::getAuthorizedGrantTypes, ClientDetailsModel::getWebServerRedirectUri, ClientDetailsModel::getAuthorities,
            ClientDetailsModel::getAccessTokenValidity, ClientDetailsModel::getRefreshTokenValidity, ClientDetailsModel::getAdditionalInformation,
            ClientDetailsModel::getAutoapprove).merge(BaseModelFields.availableFields);

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

    public static final DynamicFields searchFields = new DynamicFields(ClientDetailsModel::getClientId, ClientDetailsModel::getType);

    public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}
