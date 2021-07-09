package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.enums.CloudPlatformEnum;
import cn.cyberpeace.mp.devops.cluster.model.NetworkCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.NetworkPageRequest;
import cn.cyberpeace.mp.devops.cluster.response.NetworkPageResponse;
import cn.cyberpeace.mp.devops.cluster.service.NetworkService;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 网络管理类
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/network")
@Api(tags = "网络实例管理-网络")
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
            return new ListObjServiceResult(Lists.newArrayList(),0);
        }
        List<NetworkCloudPlatformModel.NetworkModel> networks = model.getNetworks();
        List<NetworkPageResponse> list = networks.stream().map(networkModel -> {
            NetworkCloudPlatformModel.NetworkModel.Subnet subnet = networkModel.getSubnet();
            List<NetworkPageResponse.Subnet> subnets = Lists.newArrayList();
            if (Objects.nonNull(subnet)) {
                subnets = List.of(new NetworkPageResponse.Subnet(subnet.getId(), subnet.getCidr(), subnet.getGateway_ip()));
            }
            return  NetworkPageResponse.builder().name(networkModel.getName()).networkType(networkModel.getNetwork_type())
                .status(CloudPlatformEnum.fromValue(networkModel.getStatus()))
                        .subnets(subnets).createdAt(networkModel.getCreated_at()).build();
                }
        ).collect(Collectors.toList());
        return new ListObjServiceResult(list,model.getTotal());
    }

}
