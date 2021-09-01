package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.model.FlowCollectionRelModel;
import cn.cyberpeace.mp.flow.repository.FlowCollectionRelMapper;
import cn.cyberpeace.mp.flow.request.DelFlowCollectionRelRequest;
import cn.cyberpeace.mp.flow.service.FlowCollectionRelService;
import cn.cyberpeace.mp.flow.service.field.FlowCollectionRelFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 流量集合关联service
 * @Author qiaokang
 * @Date：2021/8/31 10:31
 */
@Service
@Slf4j
public class FlowCollectionRelServiceImpl extends BaseModelServiceImpl<FlowCollectionRelModel, FlowCollectionRelMapper, FlowCollectionRelFields, QueryParams> implements FlowCollectionRelService {

    public FlowCollectionRelServiceImpl(FlowCollectionRelMapper stuMapper, FlowCollectionRelFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    /**
     * 根据集合id查询关联信息
     * @param collectionIds
     * @return
     */
    public List<FlowCollectionRelModel> selectByCollectionIds(List<String> collectionIds) {
        return getModelMapper().selectByCollectionIds(collectionIds);
    }

    /**
     * 删除关联关系
     * @param delRequest
     * @return
     */
    public ServiceResult<Integer> delCollectionRel(DelFlowCollectionRelRequest delRequest) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual("collectionId", delRequest.getCollectionId());
        condition.filterIn("flowId", delRequest.getFlowIds().toArray(new String[0]));
        int count = getModelMapper().filterDelete(condition);
        return ServiceResult.success(count);
    }


}
