package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.constant.BaseConstants;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.model.FlowCollectionModel;
import cn.cyberpeace.mp.flow.model.FlowCollectionRelModel;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.repository.FlowCollectionMapper;
import cn.cyberpeace.mp.flow.request.AddFlowCollectionRelRequest;
import cn.cyberpeace.mp.flow.request.DelFlowCollectionRelRequest;
import cn.cyberpeace.mp.flow.request.FlowCollectionQueryParams;
import cn.cyberpeace.mp.flow.request.FlowInfoQueryParams;
import cn.cyberpeace.mp.flow.service.FlowCollectionService;
import cn.cyberpeace.mp.flow.service.field.FlowCollectionFields;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 流量集合service
 * @Author qiaokang
 * @Date：2021/8/31 10:31
 */
@Service
@Slf4j
public class FlowCollectionServiceImpl extends BaseModelServiceImpl<FlowCollectionModel, FlowCollectionMapper, FlowCollectionFields, FlowCollectionQueryParams> implements FlowCollectionService {

    public FlowCollectionServiceImpl(FlowCollectionMapper stuMapper, FlowCollectionFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    @Autowired
    private FlowCollectionRelServiceImpl flowCollectionRelService;

    @Autowired
    private FlowInfoServiceImpl flowInfoService;

    /**
     * 重写list查询方法
     * @param humanCondition
     * @param queryParams
     */
    @Override
    public void loadListFilterCondition(HumanCondition humanCondition, FlowCollectionQueryParams queryParams) {
        if (Objects.nonNull(queryParams.getCategory())) {
            humanCondition.filterEqual("category", queryParams.getCategory());
        }
    }


    /**
     * 根据集合id查询流量信息
     * @param collectionId
     * @return
     */
    public ListObjServiceResult<FlowInfoModel> getByCollectionId(String collectionId) {
        List<FlowCollectionRelModel> relModels =
                flowCollectionRelService.selectByCollectionIds(Collections.singletonList(collectionId));
        String[] flowIds = relModels.stream().map(FlowCollectionRelModel::getFlowId).toArray(String[]::new);
        FlowInfoQueryParams flowInfoQueryParams = new FlowInfoQueryParams();
        flowInfoQueryParams.setIds(flowIds);
        handleQueryParams(BaseConstants.STR_LIST, flowInfoQueryParams);

        return flowInfoService.list(flowInfoQueryParams);
    }

    /**
     * 新增集合关联信息
     * @param addRequest
     * @return
     */
    public ServiceResult<Integer> addCollectionRel(AddFlowCollectionRelRequest addRequest) {
        // 查询已有关联信息
        List<FlowCollectionRelModel> relModels =
                flowCollectionRelService.selectByCollectionIds(Collections.singletonList(addRequest.getCollectionId()));
        List<String> oldFlowIds = relModels.stream().map(FlowCollectionRelModel::getFlowId).collect(Collectors.toList());
        List<String> addFlowIds = addRequest.getFlowIds();
        // 过滤已有关联信息
        addFlowIds.removeAll(oldFlowIds);
        if (CollectionUtils.isNotEmpty(addFlowIds)) {
            List<FlowCollectionRelModel> addRelModels = new ArrayList<>();
            addFlowIds.forEach(flowId -> {
                FlowCollectionRelModel relModel = new FlowCollectionRelModel();
                relModel.setCollectionId(addRequest.getCollectionId());
                relModel.setFlowId(flowId);
                addRelModels.add(relModel);
            });

            // 批量新增关联信息
            return flowCollectionRelService.bulkCreate(addRelModels);
        }

        return ServiceResult.success(0);
    }

    /**
     * 删除集合关联信息
     * @param delRequest
     * @return
     */
    public ServiceResult<Integer> delCollectionRel(DelFlowCollectionRelRequest delRequest) {
        if (CollectionUtils.isEmpty(delRequest.getFlowIds())) {
            return ServiceResult.success(0);
        }
        return flowCollectionRelService.delCollectionRel(delRequest);
    }

}
