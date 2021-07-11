package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
import cn.cyberpeace.mp.flow.enums.InteractiveTypeEnum;
import cn.cyberpeace.mp.flow.model.FlowCollectionModel;
import cn.cyberpeace.mp.flow.model.FlowCollectionRelModel;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.repository.FlowCollectionMapper;
import cn.cyberpeace.mp.flow.repository.FlowInfoMapper;
import cn.cyberpeace.mp.flow.request.AddFlowCollectionRelRequest;
import cn.cyberpeace.mp.flow.request.AddFlowCollectionRequest;
import cn.cyberpeace.mp.flow.request.DelFlowCollectionRelRequest;
import cn.cyberpeace.mp.flow.request.ModifyCollectionRequest;
import cn.cyberpeace.mp.flow.request.queryparam.FlowCollectionQueryParams;
import cn.cyberpeace.mp.flow.request.queryparam.FlowCollectionRelQueryParams;
import cn.cyberpeace.mp.flow.service.FlowCollectionService;
import cn.cyberpeace.mp.flow.service.field.FlowCollectionFields;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


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

    
    @Override
    public void loadListFilterCondition(HumanCondition humanCondition, FlowCollectionQueryParams queryParams) {
        if (Objects.nonNull(queryParams.getCategory())) {
            humanCondition.filterEqual("category", queryParams.getCategory());
        }
    }


    
    public ListObjServiceResult<FlowInfoModel> getByCollectionId(FlowCollectionRelQueryParams queryParams) {
        List<FlowInfoModel> flowInfoModels = new ArrayList<>();
        ListObjServiceResult<FlowCollectionRelModel> objServiceResult = flowCollectionRelService.list(queryParams);
        if (Objects.nonNull(objServiceResult.getSource())) {
            List<FlowCollectionRelModel> relModels = objServiceResult.getSource().getObjs();
            if (CollectionUtils.isNotEmpty(relModels)) {
                List<String> idList =
                        relModels.stream().map(FlowCollectionRelModel::getFlowId).collect(Collectors.toList());
                flowInfoModels = this.selectByFlowIds(idList);
            }
        }

        return new ListObjServiceResult<>(new ListObjResult<>(flowInfoModels, objServiceResult.getSource().getTotal())).asResult();
    }

    
    public List<FlowInfoModel> getFlowsByCollectionId(FlowCollectionRelQueryParams queryParams) {
        List<FlowInfoModel> flowInfoModels = new ArrayList<>();
        ListObjServiceResult<FlowCollectionRelModel> objServiceResult = flowCollectionRelService.list(queryParams);
        if (Objects.nonNull(objServiceResult.getSource())) {
            List<FlowCollectionRelModel> relModels = objServiceResult.getSource().getObjs();
            if (CollectionUtils.isNotEmpty(relModels)) {
                List<String> idList =
                        relModels.stream().map(FlowCollectionRelModel::getFlowId).collect(Collectors.toList());
                flowInfoModels = this.selectByFlowIds(idList);
            }
        }

        return flowInfoModels;
    }

    
    @Transactional
    public ServiceResult<Integer> addCollectionRel(AddFlowCollectionRelRequest addRequest) {
        ObjResult<FlowCollectionModel> modelResult = get(addRequest.getCollectionId()).getSource();
        if (Objects.isNull(modelResult)) {
            log.error("collection is empty for:{}", addRequest.getCollectionId());
            return ResultCommon.fail(FlowErrorCode.COLLECTION_NOT_EXIST);
        }

        List<String> addFlowIds = addRequest.getFlowIds();
        List<FlowInfoModel> existFlowInfos = this.selectByFlowIds(addFlowIds);
        if (CollectionUtils.isNotEmpty(existFlowInfos)) {
            addFlowIds = existFlowInfos.stream().map(FlowInfoModel::getId).collect(Collectors.toList());
        } else {
            log.warn("flows not exist for ids:{}", addFlowIds);
            return ResultCommon.success(0);
        }

        if (CollectionUtils.isNotEmpty(addFlowIds)) {
            List<FlowCollectionRelModel> addRelModels = new ArrayList<>();
            addFlowIds.forEach(flowId -> {
                
                FlowCollectionRelModel relModel = new FlowCollectionRelModel();
                relModel.setCollectionId(addRequest.getCollectionId());
                relModel.setFlowId(flowId);
                addRelModels.add(relModel);
            });

            
            updateRelFlowCount(modelResult.getObj(), addFlowIds, true);

            
            return flowCollectionRelService.bulkCreate(addRelModels);
        }

        return ResultCommon.success(0);
    }

    
    @Transactional
    public ServiceResult<Integer> delCollectionRel(DelFlowCollectionRelRequest delRequest) {
        ObjResult<FlowCollectionModel> modelResult = get(delRequest.getCollectionId()).getSource();
        if (Objects.isNull(modelResult)) {
            log.error("collection is empty for:{}", delRequest.getCollectionId());
            return ResultCommon.fail(FlowErrorCode.COLLECTION_NOT_EXIST);
        }

        if (CollectionUtils.isEmpty(delRequest.getFlowIds())) {
            return ResultCommon.success(0);
        }

        
        updateRelFlowCount(modelResult.getObj(), delRequest.getFlowIds(), false);
        return flowCollectionRelService.delCollectionRel(delRequest);
    }

    
    private void updateRelFlowCount(FlowCollectionModel collectionModel, List<String> flowIds, boolean isIncrease) {
        
        List<FlowInfoModel> flowInfoModels = this.selectByFlowIds(flowIds);
        if (CollectionUtils.isNotEmpty(flowInfoModels)) {
            
            int backgroundCount = (int) flowInfoModels.stream().filter(model -> InteractiveTypeEnum.BACKGROUND.toValue() == model.getInteractiveType()).count();
            
            int attackCount = (int) flowInfoModels.stream().filter(model -> InteractiveTypeEnum.ATTACK.toValue() == model.getInteractiveType()).count();
            if (isIncrease) {
                
                collectionModel.setBackgroundFlowCount(collectionModel.getBackgroundFlowCount() + backgroundCount);
                collectionModel.setAttackFlowCount(collectionModel.getAttackFlowCount() + attackCount);
            } else {
                
                collectionModel.setBackgroundFlowCount(collectionModel.getBackgroundFlowCount() - backgroundCount);
                collectionModel.setAttackFlowCount(collectionModel.getAttackFlowCount() - attackCount);
            }

            update(collectionModel);
        } else {
            log.warn("关联流量信息不存在");
        }
    }

    
    private List<FlowInfoModel> selectByFlowIds(List<String> flowIds) {
        List<FlowInfoModel> flowInfoModels = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(flowIds)) {
            flowInfoModels = flowInfoMapper.selectByFlowIds(flowIds);
        }
        return flowInfoModels;
    }

    
    public ServiceResult<FlowCollectionModel> addCollection(AddFlowCollectionRequest request) {
        if (!checkNameUnique(request.getTitle(), StringUtils.EMPTY)) {
            return ResultCommon.fail(FlowErrorCode.TITLE_UNIQUE);
        }
        FlowCollectionModel flowCollectionModel = new FlowCollectionModel();
        BeanUtils.copyProperties(request, flowCollectionModel);
        this.getModelMapper().create(flowCollectionModel);

        
        AddFlowCollectionRelRequest relRequest = new AddFlowCollectionRelRequest();
        relRequest.setCollectionId(flowCollectionModel.getId());
        if (CollectionUtils.isNotEmpty(request.getFlowInfoList())) {
            List<String> flowIds = request.getFlowInfoList().stream().map(FlowInfoModel::getId).collect(Collectors.toList());
            relRequest.setFlowIds(flowIds);
        }
        this.addCollectionRel(relRequest);
        return ResultCommon.success(flowCollectionModel);
    }

    
    public ServiceResult<FlowCollectionModel> updateCollection(ModifyCollectionRequest request) {
        if (StringUtils.isNotBlank(request.getTitle()) && !checkNameUnique(request.getTitle(), request.getId())) {
            return ResultCommon.fail(FlowErrorCode.TITLE_UNIQUE);
        }

        FlowCollectionModel flowCollectionModel = new FlowCollectionModel();
        BeanUtils.copyProperties(request, flowCollectionModel);
        
        flowCollectionModel.setAttackFlowCount(0);
        flowCollectionModel.setBackgroundFlowCount(0);
        this.getModelMapper().update(flowCollectionModel);
        
        flowCollectionRelService.delByCollectionId(request.getId());

        
        AddFlowCollectionRelRequest relRequest = new AddFlowCollectionRelRequest();
        relRequest.setCollectionId(request.getId());
        if (CollectionUtils.isNotEmpty(request.getFlowInfoList())) {
            List<String> flowIds = request.getFlowInfoList().stream().map(FlowInfoModel::getId).collect(Collectors.toList());
            relRequest.setFlowIds(flowIds);
            this.addCollectionRel(relRequest);
        }
        return ResultCommon.success(flowCollectionModel);
    }

    
    private boolean checkNameUnique(String title, String id) {
        FlowCollectionModel flowCollectionModel = getModelMapper().selectByTitle(title);
        return Objects.isNull(flowCollectionModel) || flowCollectionModel.getId().equals(id);
    }

}
