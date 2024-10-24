package cn.cyberpeace.mp.devopscluster.controller;


import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.service.SceneMonitorService;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.request.AssetManagementPageRequest;
import cn.cyberpeace.mp.devopscluster.request.AssetManagementRequest;
import cn.cyberpeace.mp.devopscluster.service.AssetManagementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/asset_management")
@Api(tags = "比赛资产管理")
public class AssetManagementController {


    @Autowired
    private AssetManagementService assetManagementService;

    @Autowired
    private SceneMonitorService sceneMonitorService;


    @GetMapping("/page")
    @ApiOperation(value = "获取资产管理", notes = "")
    public ListObjServiceResult<Object> getPageAssetManagement(AssetManagementPageRequest assetManagementPageRequest){
         return new ListObjServiceResult<>(assetManagementService.getPageAssetManagement(assetManagementPageRequest)).asResult();
    }
    @GetMapping("")
    @ApiOperation(value = "获取单个资产管理", notes = "")
    public ServiceResult<Object> getAssetManagement(AssetManagementRequest assetManagementRequest){
       return assetManagementService.getAssetManagement(assetManagementRequest).asResult();
    }
    @PostMapping("")
    @ApiOperation(value = "批量新增资产管理", notes = "")
    public ServiceResult<Object> insertListAssetManagement(@RequestBody String sceneId){
        List<ElementMonitorBasicInfo> allSceneElements = sceneMonitorService.getAllSceneElements(sceneId, false, null);
        List<AssetManagementRequest> assetManagementRequests = allSceneElements.stream().map(allSceneElement -> {
            AssetManagementRequest assetManagementRequest = new AssetManagementRequest();
            assetManagementRequest.setName(allSceneElement.getName());
            assetManagementRequest.setAddress(allSceneElement.getPorts().get(0).getIp());
            return assetManagementRequest;
        }).collect(Collectors.toList());
        return assetManagementService.insertListAssetManagement(assetManagementRequests).asResult();
    }
    @PatchMapping
    @ApiOperation(value = "修改资产管理", notes = "")
    public ServiceResult<Object> patchPageAssetManagement(@RequestBody AssetManagementRequest assetManagementRequest){
        return assetManagementService.patchPageAssetManagement(assetManagementRequest).asResult();
    }
    @DeleteMapping
    @ApiOperation(value = "删除资产管理", notes = "")
    public ServiceResult<Object> deletePageAssetManagement(@RequestBody String[] ids){
        return assetManagementService.deletePageAssetManagement(ids).asResult();
    }

}
