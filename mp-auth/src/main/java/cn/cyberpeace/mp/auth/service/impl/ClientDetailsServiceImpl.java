package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.ClientDetailsModel;
import cn.cyberpeace.mp.auth.repository.ClientDetailsMapper;
import cn.cyberpeace.mp.auth.service.ClientDetailsService;
import cn.cyberpeace.mp.auth.service.fields.ClientDetailsFields;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@Slf4j
public class ClientDetailsServiceImpl extends BaseModelServiceImpl<ClientDetailsModel, ClientDetailsMapper, ClientDetailsFields, QueryParams>
        implements ClientDetailsService {


    @Autowired
    public ClientDetailsServiceImpl(ClientDetailsMapper modelMapper, ClientDetailsFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public ClientDetailsModel getByClientId(String clientId) {
        log.info("clientId:{}", clientId);
        return getModelMapper().first(new HumanCondition().filterEqual("client_id", clientId));
    }
}
