package cn.cyberpeace.mp.auth.service.fields;

import cn.cyberpeace.mp.auth.model.ClientDetailsModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.ClassUtils;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
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
