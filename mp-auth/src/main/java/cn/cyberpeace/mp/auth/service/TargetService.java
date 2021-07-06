package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.util.List;

public interface TargetService extends BaseModelService<TargetModel, QueryParams> {

    TargetModel getByIdentifier(String identifier);

    List<TargetModel> listByTenantId(String tenantId);

}
