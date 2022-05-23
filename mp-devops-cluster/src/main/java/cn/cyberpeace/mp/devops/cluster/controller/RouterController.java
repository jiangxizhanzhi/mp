package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.RouterConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.RouterPageConverterModel;
import cn.cyberpeace.mp.devops.cluster.request.RouterPageRequest;
import cn.cyberpeace.mp.devops.cluster.response.RouterPageResponse;
import cn.cyberpeace.mp.devops.cluster.response.base.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
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
@Api(tags = "网络实例管理-路由")
@RequiredArgsConstructor
public class RouterController {

    private final RouterConverter routerConverter;

    /**
     * 分页查询路由信息
     * @param request
     * @return
     */
    @ApiOperation("分页查询路由信息")
    @GetMapping("/page")
    public PageResult<RouterPageResponse> page(RouterPageRequest request){
        RouterPageConverterModel model = routerConverter.page(request);
        List<RouterPageResponse> list =  model.getModelList().stream().map(routerModel ->
                RouterPageResponse.builder().name(routerModel.getName())
                        .status(routerModel.getStatus()).createdAt(routerModel.getCreatedAt()).build()
        ).collect(Collectors.toList());
        return new PageResult<>(list,model.getTotal());
    }

}
