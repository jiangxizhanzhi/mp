package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.model.ContainerCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.ContainerPageRequest;
import cn.cyberpeace.mp.devops.cluster.response.ContainerPageResponse;
import cn.cyberpeace.mp.devops.cluster.service.ContainerService;
import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
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

        List<ContainerPageResponse> list = containers.stream().map(containerModel -> {
            ContainerPageResponse response = ContainerPageResponse.builder().name(containerModel.getName()).image(containerModel.getImage())
                    .node(containerModel.getHost()).status(containerModel.getStatus()).build();
            Map<String,Object> addresses = containerModel.getAddresses();
            if (MapUtils.isNotEmpty(addresses)){
                List<ContainerCloudPlatformModel.ContainerModel.Address> addressList = addresses.values().stream()
                        .map(v -> JSONArray.parseArray(JSONArray.toJSONString(v), ContainerCloudPlatformModel.ContainerModel.Address.class))
                        .flatMap(v -> v.stream()).collect(Collectors.toList());
                response.setAddressList(addressList.stream().map(ContainerCloudPlatformModel.ContainerModel.Address::getAddr).collect(Collectors.toList()));
            }
            return response;
        }).collect(Collectors.toList());

        return new ListObjServiceResult(new ListObjResult(list,model.getTotal()).asResult()).asResult();
    }

}
