package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.TenantUserModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

public interface TenantUserService extends BaseModelService<TenantUserModel, QueryParams> {

    boolean deleteByUserId(String userId);

    boolean deleteByTenantId(String tenantId);
}
