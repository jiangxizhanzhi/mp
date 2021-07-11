package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.auth.model.ClientDetailsModel;
import cn.cyberpeace.mp.auth.repository.ClientDetailsMapper;
import cn.cyberpeace.mp.auth.service.ClientDetailsService;
import cn.cyberpeace.mp.auth.service.fields.ClientDetailsFields;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
@Slf4j
public class ClientDetailsServiceImpl extends BaseModelServiceImpl<ClientDetailsModel, ClientDetailsMapper, ClientDetailsFields, QueryParams>
        implements ClientDetailsService {

    public static final String CACHE_VAL_CLIENTS= "CLIENTS_CACHE";

    @Autowired
    public ClientDetailsServiceImpl(ClientDetailsMapper modelMapper, ClientDetailsFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    @Cacheable(cacheManager = AuthCacheConfig.CACHE_MANAGER_LONG, value = CACHE_VAL_CLIENTS)
    public ClientDetailsModel getByClientId(String clientId) {
        ClientDetailsModel model = getModelMapper().first(new HumanCondition().filterEqual(ClientDetailsModel::getClientId, clientId));
        if (Objects.nonNull(model)) {
            String secret = model.getClientSecret();
            String decryptedSecret = PasswordUtils.desDecrypt(secret);
            model.setClientSecret(decryptedSecret);
        }
        return model;
    }

}
