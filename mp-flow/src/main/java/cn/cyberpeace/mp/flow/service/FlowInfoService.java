package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.request.queryparam.FlowInfoQueryParams;

import java.util.List;

/**
 * @ClassName FlowInfoService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface FlowInfoService extends BaseModelService<FlowInfoModel, FlowInfoQueryParams>  {

    /**
     * 根据id集合查询列表
     *
     * @param idList
     * @return
     */
    List<FlowInfoModel> findByIds(List<String> idList);

}
