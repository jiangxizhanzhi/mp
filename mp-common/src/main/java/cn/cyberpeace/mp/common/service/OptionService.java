package cn.cyberpeace.mp.common.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.bo.OptionBO;
import cn.cyberpeace.mp.common.bo.OptionModelBO;
import cn.cyberpeace.mp.common.model.OptionModel;
import cn.cyberpeace.mp.common.request.queryparam.OptionQueryParams;

import java.util.List;


public interface OptionService extends BaseModelService<OptionModel, OptionQueryParams> {

    
    List<OptionModel> findByType(String type);

    
    ServiceResult<List<OptionBO>> getStrOptions(String type);


    
    ServiceResult<List<OptionBO>> getIntOptions(String type);

    
    ListObjServiceResult<OptionModelBO> page(OptionQueryParams queryParams);

    
    void addOption(OptionModelBO optionModelBO);

    
    void editOption(OptionModelBO optionModelBO);

    
    void deleteOptions(List<String> ids);

    
    OptionModelBO findById(String id);

    
    List<OptionModel> findByIds(List<String> ids);

    
    Integer getMaxCode(String type);

    
    Integer getMaxSort(String type);

}
