package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.validation.stereotype.Length;
import cn.cyberpeace.mp.flow.model.FlowCategoryModel;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
public interface FlowCategoryService extends BaseModelService<FlowCategoryModel, QueryParams> {
    /**
     * 查询所有流量分类
     * @return
     */
    List<FlowCategoryModel> findAll();
}
