package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.TargetDto;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.util.List;

public interface TargetService extends BaseModelService<TargetModel, QueryParams> {

    TargetModel getByIdentifier(String identifier);

    List<TargetModel> listByTenantId(String tenantId);



    boolean exists(String id);

    List<TargetModel> listByParent(String parentId);

    List<TargetModel> listAll(String tenant, Integer type);

    List<TargetDto> getTree(String parentId, Integer type);
}
