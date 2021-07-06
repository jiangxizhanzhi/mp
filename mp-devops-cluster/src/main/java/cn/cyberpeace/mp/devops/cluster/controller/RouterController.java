package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.enums.CloudPlatformEnum;
import cn.cyberpeace.mp.devops.cluster.model.RouterCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.RouterPageRequest;
import cn.cyberpeace.mp.devops.cluster.response.RouterPageResponse;
import cn.cyberpeace.mp.devops.cluster.service.RouterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 路由管理类
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/router")
@Api(tags = "网络实例管理-路由")
public class RouterController {

    private RouterService routerService;

    @Autowired
    public RouterController(RouterService routerService) {
        this.routerService = routerService;
    }

    /**
     * 分页查询路由信息
     * @param request
     * @return
     */
    @ApiOperation("分页查询路由信息")
    @GetMapping("/page")
    public ListObjServiceResult<RouterPageResponse> page(RouterPageRequest request){
        RouterCloudPlatformModel model = routerService.page(request);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getRouters())){
            return new ListObjServiceResult<>();
        }
        List<RouterCloudPlatformModel.RouterModel> routers = model.getRouters();
        List<RouterPageResponse> list = routers.stream().map(routerModel ->
                RouterPageResponse.builder().name(routerModel.getName())
                        .status(CloudPlatformEnum.fromValue(routerModel.getStatus())).createdAt(routerModel.getCreated_at()).build()
        ).collect(Collectors.toList());
        return new ListObjServiceResult(new ListObjResult(list,model.getTotal()).asResult()).asResult();
    }

}
