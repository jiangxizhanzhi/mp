package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.model.NetworkCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.NetworkPageRequest;
import cn.cyberpeace.mp.devops.cluster.response.NetworkPageResponse;
import cn.cyberpeace.mp.devops.cluster.service.NetworkService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 网络管理类
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/network")
public class NetworkController {

    private NetworkService networkService;

    @Autowired
    public NetworkController(NetworkService networkService) {
        this.networkService = networkService;
    }

    /**
     * 分页查询网络信息
     * @param request
     * @return
     */
    @ApiOperation("分页查询网络信息")
    @GetMapping("/page")
    public ListObjServiceResult<NetworkPageResponse> page(NetworkPageRequest request){
        NetworkCloudPlatformModel model = networkService.page(request);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getNetworks())){
            return new ListObjServiceResult();
        }
        List<NetworkCloudPlatformModel.NetworkModel> networks = model.getNetworks();
        List<NetworkPageResponse> list = networks.stream().map(networkModel -> {
            NetworkCloudPlatformModel.NetworkModel.Subnet subnet = networkModel.getSubnet();
            List<NetworkPageResponse.Subnet> subnets = List.of(new NetworkPageResponse.Subnet(subnet.getId(), subnet.getCidr(), subnet.getGateway_ip()));
            return  NetworkPageResponse.builder().name(networkModel.getName()).networkType(networkModel.getNetwork_type())
                .status(networkModel.getStatus())
                        .subnets(subnets).createdAt(networkModel.getCreated_at()).build();
                }
        ).collect(Collectors.toList());
        return new ListObjServiceResult(new ListObjResult(list,model.getTotal()).asResult()).asResult();
    }

}
