package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.model.RouterOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.request.RouterPageRequest;
import cn.cyberpeace.mp.devops.cluster.response.RouterPageResponse;
import cn.cyberpeace.mp.devops.cluster.service.RouterService;
import cn.cyberpeace.mp.devops.cluster.service.fields.RouterFields;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 路由管理类
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/router")
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
    @PostMapping("/page")
    public ListObjServiceResult<RouterPageResponse> page(@RequestBody RouterPageRequest request){
        RouterOpenStackModel model = routerService.page(request);
        List<RouterOpenStackModel.RouterModel> routers = model.getRouters();
        if (CollectionUtils.isEmpty(routers)){
            return new ListObjServiceResult<>();
        }
        List<RouterPageResponse> list = routers.stream().map(routerModel ->
                RouterPageResponse.builder().name(routerModel.getName()).status(routerModel.getStatus()).createdAt(routerModel.getCreated_at()).build()
        ).collect(Collectors.toList());
        return new ListObjServiceResult(new ListObjResult(list,model.getTotal(), RouterFields.allFields).asResult()).asResult();
    }

}
