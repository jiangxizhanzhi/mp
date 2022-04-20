package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.ContainerConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.ContainerConvertModel;
import cn.cyberpeace.mp.devops.cluster.request.ContainerPageRequest;
import cn.cyberpeace.mp.devops.cluster.response.ContainerPageResponse;
import cn.cyberpeace.mp.devops.cluster.response.base.PageResult;
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
 * 容器管理类
 * @author wanggang
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/container")
@Api(tags = "计算实例管理-容器")
public class ContainerController {

    private ContainerConverter containerConverter;

    @Autowired
    public ContainerController(ContainerConverter containerConverter) {
        this.containerConverter = containerConverter;
    }

    /**
     * 分页查询容器列表
     * @param request
     * @return
     */
    @ApiOperation("分页查询容器列表")
    @GetMapping("/page")
    public PageResult<ContainerPageResponse> page(ContainerPageRequest request){
        ContainerConvertModel model = containerConverter.page(request);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getModelList())){
            return new PageResult<>(Lists.newArrayList(), NumberUtils.INTEGER_ZERO);
        }
        List<ContainerPageResponse> list = model.getModelList().stream().map(containerModel ->
                ContainerPageResponse.builder().name(containerModel.getName())
                .image(containerModel.getImage()).node(containerModel.getNode())
                .status(containerModel.getStatus()).addressList(containerModel.getAddressList())
                        .vlanIdList(containerModel.getVlanIdList()).build())
                .collect(Collectors.toList());

        return new PageResult<>(list, model.getTotal());
    }

}
