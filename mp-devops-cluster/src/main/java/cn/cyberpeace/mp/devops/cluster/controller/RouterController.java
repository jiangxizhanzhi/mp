package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.RouterConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.RouterPageConverterModel;
import cn.cyberpeace.mp.devops.cluster.request.RouterPageRequest;
import cn.cyberpeace.mp.devops.cluster.response.RouterPageResponse;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private RouterConverter routerConverter;

    @Autowired
    public RouterController(RouterConverter routerConverter) {
        this.routerConverter = routerConverter;
    }

    /**
     * 分页查询路由信息
     * @param request
     * @return
     */
    @ApiOperation("分页查询路由信息")
    @GetMapping("/page")
    public ListObjServiceResult<RouterPageResponse> page(RouterPageRequest request){
        RouterPageConverterModel model = routerConverter.page(request);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getModelList())){
            return new ListObjServiceResult<>(Lists.newArrayList(), NumberUtils.INTEGER_ZERO);
        }
        List<RouterPageResponse> list =  model.getModelList().stream().map(routerModel ->
                RouterPageResponse.builder().name(routerModel.getName())
                        .status(routerModel.getStatus()).createdAt(routerModel.getCreatedAt()).build()
        ).collect(Collectors.toList());
        return new ListObjServiceResult<>(list,model.getTotal());
    }

}
