package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.model.FlowCollectionRelModel;
import cn.cyberpeace.mp.flow.repository.FlowCollectionRelMapper;
import cn.cyberpeace.mp.flow.request.DelFlowCollectionRelRequest;
import cn.cyberpeace.mp.flow.request.queryparam.FlowCollectionRelQueryParams;
import cn.cyberpeace.mp.flow.service.FlowCollectionRelService;
import cn.cyberpeace.mp.flow.service.field.FlowCollectionRelFields;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class FlowCollectionRelServiceImpl extends BaseModelServiceImpl<FlowCollectionRelModel, FlowCollectionRelMapper, FlowCollectionRelFields, FlowCollectionRelQueryParams> implements FlowCollectionRelService {

    public FlowCollectionRelServiceImpl(FlowCollectionRelMapper stuMapper, FlowCollectionRelFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    
    @Override
    public void loadListFilterCondition(HumanCondition humanCondition, FlowCollectionRelQueryParams queryParams) {
        if (StringUtils.isNotBlank(queryParams.getCollectionId())) {
            humanCondition.filterEqual("collectionId", queryParams.getCollectionId());
        }
    }

    
    public List<FlowCollectionRelModel> selectByCollectionIds(List<String> collectionIds) {
        return getModelMapper().selectByCollectionIds(collectionIds);
    }

    
    public ServiceResult<Integer> delCollectionRel(DelFlowCollectionRelRequest delRequest) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual("collectionId", delRequest.getCollectionId());
        condition.filterIn("flowId", delRequest.getFlowIds().toArray(new String[0]));
        int count = getModelMapper().filterDelete(condition);
        return ResultCommon.success(count);
    }

    
    public Integer delByCollectionId(String collectionId) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(FlowCollectionRelModel::getCollectionId, collectionId);
        return getModelMapper().filterDelete(condition);
    }


}
