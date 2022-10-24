package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.flow.v2.model.FlowModel;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskModel;
import cn.cyberpeace.mp.flow.v2.repository.FlowTaskMapper;
import cn.cyberpeace.mp.flow.v2.service.fields.FlowTaskFields;
import cn.cyberpeace.mp.flow.v2.service.param.FlowTaskQueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("flowTaskServiceV2")
public class FlowTaskService extends BaseModelServiceImpl<FlowTaskModel, FlowTaskMapper, FlowTaskFields, FlowTaskQueryParams> {
    @Autowired
    public FlowTaskService(@Qualifier("v2FlowTaskMapper") FlowTaskMapper modelMapper, @Qualifier("v2FlowTaskFields") FlowTaskFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public void loadListFilterCondition(HumanCondition humanCondition, FlowTaskQueryParams queryParams) {
        var categories = queryParams.getCategories();
        if (categories != null && !categories.isEmpty()) {
            humanCondition.filterIn(FlowModel::getCategory, categories.toArray());
        }
    }

}
