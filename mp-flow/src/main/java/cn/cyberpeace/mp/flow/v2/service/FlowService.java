package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.flow.v2.model.FlowModel;
import cn.cyberpeace.mp.flow.v2.repository.FlowMapper;
import cn.cyberpeace.mp.flow.v2.service.fields.FlowFields;
import cn.cyberpeace.mp.flow.v2.service.param.FlowQueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FlowService extends BaseModelServiceImpl<FlowModel, FlowMapper, FlowFields, FlowQueryParams> {
    @Autowired
    public FlowService(FlowMapper modelMapper, FlowFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public void loadListFilterCondition(HumanCondition humanCondition, FlowQueryParams queryParams) {
        var categories = queryParams.getCategories();
        if (categories != null && !categories.isEmpty()) {
            humanCondition.filterIn(FlowModel::getCategory, categories.toArray());
        }

        var resourceTypes = queryParams.getResourceTypes();
        if (resourceTypes != null && !resourceTypes.isEmpty()) {
            humanCondition.filterIn(FlowModel::getResourceType, resourceTypes.toArray());
        }

        Integer interactiveType = queryParams.getInteractiveType();
        if (Objects.nonNull(interactiveType)) {
            humanCondition.filterEqual(FlowModel::getInteractiveType, interactiveType);
        }
    }

}
