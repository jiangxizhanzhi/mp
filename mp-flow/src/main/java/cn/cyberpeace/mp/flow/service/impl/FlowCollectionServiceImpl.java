package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.enums.InteractiveTypeEnum;
import cn.cyberpeace.mp.flow.model.FlowCollectionModel;
import cn.cyberpeace.mp.flow.model.FlowCollectionRelModel;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.repository.FlowCollectionMapper;
import cn.cyberpeace.mp.flow.repository.FlowInfoMapper;
import cn.cyberpeace.mp.flow.request.AddFlowCollectionRelRequest;
import cn.cyberpeace.mp.flow.request.DelFlowCollectionRelRequest;
import cn.cyberpeace.mp.flow.request.queryparam.FlowCollectionQueryParams;
import cn.cyberpeace.mp.flow.request.queryparam.FlowCollectionRelQueryParams;
import cn.cyberpeace.mp.flow.service.FlowCollectionService;
import cn.cyberpeace.mp.flow.service.field.FlowCollectionFields;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private FlowInfoMapper flowInfoMapper;

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
     * 根据集合id分页查询流量信息
     * @param queryParams
     * @return
     */
    public ListObjServiceResult<FlowInfoModel> getByCollectionId(FlowCollectionRelQueryParams queryParams) {
        List<FlowInfoModel> flowInfoModels = new ArrayList<>();
        ListObjServiceResult<FlowCollectionRelModel> objServiceResult = flowCollectionRelService.list(queryParams);
        if (Objects.nonNull(objServiceResult.getSource())) {
            List<FlowCollectionRelModel> relModels = objServiceResult.getSource().getObjs();
            if (CollectionUtils.isNotEmpty(relModels)) {
                List<String> idList =
                        relModels.stream().map(FlowCollectionRelModel::getFlowId).collect(Collectors.toList());
                flowInfoModels = flowInfoMapper.selectByFlowIds(idList);
            }
        }

        return new ListObjServiceResult<>(new ListObjResult<>(flowInfoModels, objServiceResult.getSource().getTotal())).asResult();
    }

    /**
     * 新增集合关联信息
     * @param addRequest
     * @return
     */
    @Transactional
    public ServiceResult<Integer> addCollectionRel(AddFlowCollectionRelRequest addRequest) {
        ObjResult<FlowCollectionModel> modelResult = get(addRequest.getCollectionId()).getSource();
        if (Objects.isNull(modelResult)) {
            log.error("collection is empty for:{}", addRequest.getCollectionId());
            return ServiceResult.fail("collection is not exist");
        }

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
                // 遍历生成关联对象集合
                FlowCollectionRelModel relModel = new FlowCollectionRelModel();
                relModel.setCollectionId(addRequest.getCollectionId());
                relModel.setFlowId(flowId);
                addRelModels.add(relModel);
            });

            // 更新关联流量数量
            updateRelFlowCount(modelResult.getObj(), addFlowIds, true);

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
    @Transactional
    public ServiceResult<Integer> delCollectionRel(DelFlowCollectionRelRequest delRequest) {
        ObjResult<FlowCollectionModel> modelResult = get(delRequest.getCollectionId()).getSource();
        if (Objects.isNull(modelResult)) {
            log.error("collection is empty for:{}", delRequest.getCollectionId());
            return ServiceResult.fail("collection is not exist");
        }

        if (CollectionUtils.isEmpty(delRequest.getFlowIds())) {
            return ServiceResult.success(0);
        }

        // 更新关联流量数量
        updateRelFlowCount(modelResult.getObj(), delRequest.getFlowIds(), false);
        return flowCollectionRelService.delCollectionRel(delRequest);
    }

    /**
     * 更新关联流量数量
     * @param collectionModel
     * @param flowIds
     * @param isIncrease
     */
    private void updateRelFlowCount(FlowCollectionModel collectionModel, List<String> flowIds, boolean isIncrease) {
        // 更新关联流量数量
        List<FlowInfoModel> flowInfoModels = flowInfoMapper.selectByFlowIds(flowIds);
        if (CollectionUtils.isNotEmpty(flowInfoModels)) {
            // 背景流量数量
            int backgroundCount = (int) flowInfoModels.stream().filter(model -> InteractiveTypeEnum.BACKGROUND.toValue() == model.getInteractiveType()).count();
            // 攻击流量数量
            int attackCount = (int) flowInfoModels.stream().filter(model -> InteractiveTypeEnum.ATTACK.toValue() == model.getInteractiveType()).count();
            if (isIncrease) {
                // 增加
                collectionModel.setBackgroundFlowCount(collectionModel.getBackgroundFlowCount() + backgroundCount);
                collectionModel.setAttackFlowCount(collectionModel.getAttackFlowCount() + attackCount);
            } else {
                // 减少
                collectionModel.setBackgroundFlowCount(collectionModel.getBackgroundFlowCount() - backgroundCount);
                collectionModel.setAttackFlowCount(collectionModel.getAttackFlowCount() - attackCount);
            }

            update(collectionModel);
        } else {
            log.warn("关联流量信息不存在");
        }
    }

}
