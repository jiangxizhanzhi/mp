package cn.cyberpeace.mp.base.service;

import cn.cyberpeace.mp.base.model.BaseObjMappingModel;
import cn.cyberpeace.mp.base.service.result.ServiceResult;

import java.util.List;


/**
 * @ClassName BaseObjMappingService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface BaseObjMappingService<M extends BaseObjMappingModel> {

    /**
     * getMapping
     *
     * @param group
     * @param name
     * @return
     */
    ServiceResult<M> getMapping(String group, String name);

    /**
     * getMappings
     *
     * @param group
     * @return
     */
    ServiceResult<List<M>> getMappings(String group);

    /**
     * syncMappings
     *
     * @param group
     * @param mappings
     * @return
     */
    ServiceResult<List<M>> syncMappings(String group, List<M> mappings);

    /**
     * deleteMappings
     *
     * @param group
     * @return
     */
    ServiceResult<Integer> deleteMappings(String group);

}
