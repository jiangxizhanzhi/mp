package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.request.queryparam.FlowInfoQueryParams;

import java.util.List;

public interface FlowInfoService extends BaseModelService<FlowInfoModel, FlowInfoQueryParams>  {

    /**
     * 根据id集合查询列表
     * @param idList
     * @return
     */
    List<FlowInfoModel> findByIds(List<String> idList);

}
