package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.target.TargetDto;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public interface TargetService extends BaseModelService<TargetModel, QueryParams> {

    TargetModel getByIdentifier(String identifier);

//    List<TargetModel> listByTenantId(String tenantId);

    boolean exists(String id);

    List<TargetModel> listByParent(String parentId);

    List<TargetModel> listAll(String tenant, Integer type);

    List<TargetModel> listByMultiTypes(String tenant, Integer[] types);

    List<TargetDto> getTree(String parentId, Integer type);
}
