package cn.cyberpeace.mp.devopscluster.service.impl;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.model.AssetManagementModel;
import cn.cyberpeace.mp.devopscluster.repository.AssetManagementMapper;
import cn.cyberpeace.mp.devopscluster.request.AssetManagementPageRequest;
import cn.cyberpeace.mp.devopscluster.request.AssetManagementRequest;
import cn.cyberpeace.mp.devopscluster.service.AssetManagementService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AssetManagementServiceImpl implements AssetManagementService {

    @Autowired
    private AssetManagementMapper assetManagementMapper;

    @Override
    public ListObjResult getPageAssetManagement(AssetManagementPageRequest assetManagementPageRequest) {
        if (StringUtils.isEmpty(assetManagementPageRequest.getSceneId())) {
            throw new MpRuntimeException("获取资产管理场景id不能为空");
        }
            HumanCondition humanCondition = new HumanCondition().orderBy(AssetManagementModel::getCreateTime)
                    .pagination(assetManagementPageRequest.getPage(), assetManagementPageRequest.getPageSize())
                    .filterContains(AssetManagementModel::getName,assetManagementPageRequest.getName())
                    .filterContains(AssetManagementModel::getSceneId,assetManagementPageRequest.getSceneId());
            int total = assetManagementMapper.total(humanCondition);
            List<AssetManagementModel> models = assetManagementMapper.list(humanCondition);
            return new ListObjResult(models, total).asResult();
    }

    @Override
    public ServiceResult<Object> getAssetManagement(AssetManagementRequest assetManagementRequest) {
        if (StringUtils.isEmpty(assetManagementRequest.getId())) {
            throw new MpRuntimeException("获取资产管理资产id不能为空");
        }
        AssetManagementModel assetManagementModel = assetManagementMapper.get(assetManagementRequest.getId());
        return new ServiceResult<>(assetManagementModel);
    }

    @Override
    public ServiceResult<Object> insertPageAssetManagement(AssetManagementRequest assetManagementRequest) {
        AssetManagementModel assetManagementModel = new AssetManagementModel();
        BeanUtils.copyProperties(assetManagementRequest,assetManagementModel);

        return new ServiceResult<>(assetManagementMapper.create(assetManagementModel));
    }

    @Override
    public ServiceResult<Object> patchPageAssetManagement(AssetManagementRequest assetManagementRequest) {
        if (StringUtils.isEmpty(assetManagementRequest.getId())) {
            throw new MpRuntimeException("修改资产管理资产id不能为空");
        }
        AssetManagementModel assetManagementModel = new AssetManagementModel();
        BeanUtils.copyProperties(assetManagementRequest,assetManagementModel);

        return new ServiceResult<>(assetManagementMapper.update(assetManagementModel));
    }

    @Override
    public ServiceResult<Object> deletePageAssetManagement(String[] ids) {

        return new ServiceResult<>(assetManagementMapper.bulkDelete(ids));
    }

    @Override
    public ServiceResult<Object> insertListAssetManagement(List<AssetManagementRequest> assetManagementRequest) {
        List<AssetManagementModel> list = assetManagementRequest.stream().map(s -> {
            AssetManagementModel assetManagementModel = new AssetManagementModel();
            BeanUtils.copyProperties(assetManagementRequest, assetManagementModel);
            return assetManagementModel;
        }).collect(Collectors.toList());
        return new ServiceResult<>(assetManagementMapper.bulkCreate(list));
    }
}
