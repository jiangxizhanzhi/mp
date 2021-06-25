package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.model.NetworkOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.request.NetworkPageRequest;
import cn.cyberpeace.mp.devops.cluster.response.NetworkPageResponse;
import cn.cyberpeace.mp.devops.cluster.service.NetworkService;
import cn.cyberpeace.mp.devops.cluster.service.fields.NetworkFields;
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

    @ApiOperation("分页查询网络信息")
    @PostMapping("/page")
    public ListObjServiceResult page(@RequestBody NetworkPageRequest request){
        NetworkOpenStackModel model = networkService.page(request);
        List<NetworkOpenStackModel.NetworkModel> networks = model.getNetworks();
        if (CollectionUtils.isEmpty(networks)){
            return new ListObjServiceResult();
        }
        List<NetworkPageResponse> list = networks.stream().map(networkModel -> {
            NetworkOpenStackModel.NetworkModel.Subnet subnet = networkModel.getSubnet();
            List<NetworkPageResponse.Subnet> subnets = List.of(new NetworkPageResponse.Subnet(subnet.getId(), subnet.getCidr(), subnet.getGateway_ip()));
            return  NetworkPageResponse.builder().name(networkModel.getName()).networkType(networkModel.getNetwork_type())
                .status(networkModel.getStatus())
                        .subnets(subnets).createdAt(networkModel.getCreated_at()).build();
                }
        ).collect(Collectors.toList());
        return new ListObjServiceResult(new ListObjResult(list,model.getTotal(), NetworkFields.allFields).asResult()).asResult();
    }

}
