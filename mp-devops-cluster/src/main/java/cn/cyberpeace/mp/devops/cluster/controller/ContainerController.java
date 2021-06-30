package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.model.ContainerCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.ContainerPageRequest;
import cn.cyberpeace.mp.devops.cluster.response.ContainerPageResponse;
import cn.cyberpeace.mp.devops.cluster.service.ContainerService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 容器管理类
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/container")
public class ContainerController {

    private ContainerService containerService;

    @Autowired
    public ContainerController(ContainerService containerService) {
        this.containerService = containerService;
    }

    /**
     * 分页查询容器列表
     * @param request
     * @return
     */
    @ApiOperation("分页查询容器列表")
    @GetMapping("/page")
    public ListObjServiceResult<ContainerPageResponse> page(ContainerPageRequest request){
        ContainerCloudPlatformModel model = containerService.page(request);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getContainers())){
            return new ListObjServiceResult();
        }
        List<ContainerCloudPlatformModel.ContainerModel> containers = model.getContainers();
        List<ContainerPageResponse> list = containers.stream().map(containerModel ->
                ContainerPageResponse.builder().name(containerModel.getName()).image(containerModel.getImage())
                .node(containerModel.getHost()).addressList(List.of(containerModel.getAddresses()))
                .status(containerModel.getStatus()).build()
        ).collect(Collectors.toList());

        return new ListObjServiceResult(new ListObjResult(list,model.getTotal()).asResult()).asResult();
    }

}
