package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.service.CollectionTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/18 16:09
 */

@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/collection/types")
@Api(value = "日志事件类型管理", tags = "日志事件类型管理")
public class CollectionTypeController {

    @Autowired
    private CollectionTypeService collectionTypeService;

    @GetMapping("/list")
    @ApiOperation(value = "查询日志事件类别属性结构")
    public ServiceResult typesTree(BaseSceneElementRequest baseRequest) {

        List list = collectionTypeService.typeTrees(baseRequest);
        return ServiceResult.success(list);
    }


}
