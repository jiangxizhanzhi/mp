package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.TenantUserModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public interface TenantUserService extends BaseModelService<TenantUserModel, QueryParams> {

    boolean deleteByUserId(String userId);

    boolean deleteByTenantId(String tenantId);
}
