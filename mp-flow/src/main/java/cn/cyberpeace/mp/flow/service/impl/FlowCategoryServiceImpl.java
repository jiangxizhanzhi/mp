package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowCategoryModel;
import cn.cyberpeace.mp.flow.repository.FlowCategoryMapper;
import cn.cyberpeace.mp.flow.service.FlowCategoryService;
import cn.cyberpeace.mp.flow.service.field.FlowCategoryFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Service
public class FlowCategoryServiceImpl extends BaseModelServiceImpl<FlowCategoryModel, FlowCategoryMapper,
        FlowCategoryFields, QueryParams>
        implements FlowCategoryService {

    public FlowCategoryServiceImpl(FlowCategoryMapper flowCategoryMapper, FlowCategoryFields flowCategoryFields) {
        setModelMapper(flowCategoryMapper);
        setModelFields(flowCategoryFields);
    }

    @Override
    public List<FlowCategoryModel> findAll() {
        return getModelMapper().list(new HumanCondition());
    }
}
