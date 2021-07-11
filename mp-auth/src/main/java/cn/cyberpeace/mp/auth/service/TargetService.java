package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.target.TargetBo;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.util.List;


public interface TargetService extends BaseModelService<TargetModel, QueryParams> {

    <T extends TargetModel> T getByIdentifier(String identifier);



    boolean exists(String id);

    <T extends TargetModel> List<T> listByParent(String parentId);

    
    <T extends TargetModel> List<T> listAll(String tenant, Integer type);

    
    <T extends TargetModel> List<T> listAllIgnoreVisible(String tenant, Integer type);

    <T extends TargetModel> List<T> listByMultiTypes(String tenant, Integer[] types);

    List<TargetBo> getTree(String parentId, Integer type);

    boolean deleteAll();
}
