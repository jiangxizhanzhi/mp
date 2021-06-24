package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.service.BaseModelService;

import java.util.List;

public interface TargetService extends BaseModelService<TargetModel> {

    TargetModel getByIdentifier(String identifier);

    List<TargetModel> getAllByTenant(String tenant);

}
