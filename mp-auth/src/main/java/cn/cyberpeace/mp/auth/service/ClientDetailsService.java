package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.ClientDetailsModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public interface ClientDetailsService extends BaseModelService<ClientDetailsModel, QueryParams> {

    ClientDetailsModel getByClientId(String clientId);

}
