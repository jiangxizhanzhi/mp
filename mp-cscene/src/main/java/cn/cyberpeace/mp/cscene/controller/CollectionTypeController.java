package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.common.CollectionApp;
import cn.cyberpeace.mp.cscene.dto.common.CollectionType;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.request.common.CollectionAppRequest;
import cn.cyberpeace.mp.cscene.service.CollectionTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;



@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/collection/type")
@Api(value = "采集类型管理", tags = "采集类型管理")
public class CollectionTypeController {

    @Autowired
    private CollectionTypeService collectionTypeService;

    @GetMapping("/list")
    @ApiOperation(value = "查询采集类型")
    public ServiceResult<List<CollectionType>> typesTree(BaseSceneElementRequest baseRequest) {

        List<CollectionType> list = collectionTypeService.typeTrees(baseRequest);
        return ResultCommon.success(list);
    }



    @GetMapping("/apps")
    @ApiOperation(value = "查询节点应用列表")
    public ServiceResult<List<CollectionApp>> getApps(CollectionAppRequest appRequest) {
        List<CollectionApp> appNames = collectionTypeService.getAppNames(appRequest);
        return ResultCommon.success(appNames);
    }


}
