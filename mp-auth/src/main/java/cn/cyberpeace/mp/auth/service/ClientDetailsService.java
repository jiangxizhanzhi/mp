package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.ClientDetailsModel;
import cn.cyberpeace.mp.auth.model.OrgUserModel;
import cn.cyberpeace.mp.base.service.BaseModelService;

public interface ClientDetailsService extends BaseModelService<ClientDetailsModel> {

    ClientDetailsModel getByClientId(String clientId);

}
