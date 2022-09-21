package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.AuthenticationConfigModel;
import cn.cyberpeace.mp.auth.repository.AuthenticationConfigMapper;
import cn.cyberpeace.mp.auth.service.AuthenticationConfigService;
import cn.cyberpeace.mp.auth.service.fields.AuthenticationConfigFields;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @Description
 * @Author shgang
 * @Date 2022/9/20
 */
@Repository
public class AuthenticationConfigServiceImpl extends BaseModelServiceImpl<AuthenticationConfigModel, AuthenticationConfigMapper, AuthenticationConfigFields, QueryParams>
    implements AuthenticationConfigService {

    @Autowired
    public AuthenticationConfigServiceImpl(AuthenticationConfigMapper authenticationConfigMapper) {
        setModelMapper(authenticationConfigMapper);
    }

    @Override
    public AuthenticationConfigModel selectFirst() {
        return getModelMapper().first(new HumanCondition());
    }
}
