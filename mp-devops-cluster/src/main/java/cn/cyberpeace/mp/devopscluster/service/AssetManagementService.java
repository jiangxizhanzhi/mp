package cn.cyberpeace.mp.devopscluster.service;

import cn.cyberpeace.mp.base.service.result.BaseObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.request.AssetManagementPageRequest;
import cn.cyberpeace.mp.devopscluster.request.AssetManagementRequest;

import java.util.List;

public interface AssetManagementService {
    ListObjResult getPageAssetManagement(AssetManagementPageRequest assetManagementPageRequest);

    ServiceResult<Object> getAssetManagement(AssetManagementRequest assetManagementRequest);

    ServiceResult<Object> insertPageAssetManagement(AssetManagementRequest assetManagementRequest);

    ServiceResult<Object> patchPageAssetManagement(AssetManagementRequest assetManagementRequest);

    ServiceResult<Object> deletePageAssetManagement(String[] ids);

    ServiceResult<Object> insertListAssetManagement(List<AssetManagementRequest> assetManagementRequest);
}
