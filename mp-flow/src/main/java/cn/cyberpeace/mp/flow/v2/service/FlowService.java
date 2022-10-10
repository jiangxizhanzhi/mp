package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
import cn.cyberpeace.mp.flow.v2.model.FlowModel;
import cn.cyberpeace.mp.flow.v2.repository.FlowMapper;
import cn.cyberpeace.mp.flow.v2.service.fields.FlowFields;
import cn.cyberpeace.mp.flow.v2.service.param.FlowQueryParams;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    @Override
    public ObjServiceResult<FlowModel> create(FlowModel obj, String[] fields) {
        if (!checkNameUnique(obj.getName(), null)) {
            throw new MpRuntimeException(FlowErrorCode.TITLE_UNIQUE);
        }
        return super.create(obj, fields);
    }

    @Override
    public ObjServiceResult<FlowModel> update(FlowModel obj, String[] fields) {
        if (!checkNameUnique(obj.getName(), obj.getId())) {
            throw new MpRuntimeException(FlowErrorCode.TITLE_UNIQUE);
        }
        return super.update(obj, fields);
    }

    /**
     * 校验名称是否唯一
     * @param name
     * @param id
     * @return
     */
    private boolean checkNameUnique(String name, String id) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(FlowModel::getName, name);

        List<FlowModel> flowModels = getModelMapper().list(condition);
        return CollectionUtils.isEmpty(flowModels) || flowModels.get(0).getId().equals(id);
    }

}
