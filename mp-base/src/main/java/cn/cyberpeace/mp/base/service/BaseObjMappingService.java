package cn.cyberpeace.mp.base.service;

import cn.cyberpeace.mp.base.model.BaseObjMappingModel;
import cn.cyberpeace.mp.base.service.result.ServiceResult;

import java.util.List;


public interface BaseObjMappingService<M extends BaseObjMappingModel> {

    ServiceResult<M> getMapping(String group, String name);

    ServiceResult<List<M>> getMappings(String group);

    ServiceResult<List<M>> syncMappings(String group, List<M> mappings);

    ServiceResult<Integer> deleteMappings(String group);

}
