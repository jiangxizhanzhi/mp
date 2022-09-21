package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.AuthenticationConfigModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

/**
 * @Description
 * @Author shgang
 * @Date 2022/9/20
 */
public interface AuthenticationConfigService extends BaseModelService<AuthenticationConfigModel, QueryParams> {

    AuthenticationConfigModel selectFirst();
}
