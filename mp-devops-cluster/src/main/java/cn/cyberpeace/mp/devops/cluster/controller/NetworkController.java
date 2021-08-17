package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.NetworkConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.NetworkPageConverterModel;
import cn.cyberpeace.mp.devops.cluster.request.NetworkPageRequest;
import cn.cyberpeace.mp.devops.cluster.response.NetworkPageResponse;
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
 * 网络管理类
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/network")
@Api(tags = "网络实例管理-网络")
public class NetworkController {

    private NetworkConverter networkConverter;

    @Autowired
    public NetworkController(NetworkConverter networkConverter) {
        this.networkConverter = networkConverter;
    }

    /**
     * 分页查询网络信息
     * @param request
     * @return
     */
    @ApiOperation("分页查询网络信息")
    @GetMapping("/page")
    public ListObjServiceResult<NetworkPageResponse> page(NetworkPageRequest request){
        NetworkPageConverterModel model = networkConverter.page(request);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getModelList())){
            return new ListObjServiceResult<>(Lists.newArrayList(), NumberUtils.INTEGER_ZERO);
        }
        List<NetworkPageConverterModel.Model> networks = model.getModelList();
        List<NetworkPageResponse> list = networks.stream().map(networkModel -> {
            List<NetworkPageConverterModel.Model.Subnet> subnetList = networkModel.getSubnets();
            List<NetworkPageResponse.Subnet> subnets = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(subnetList)) {
                subnets = subnetList.stream().map(subnet -> new NetworkPageResponse.Subnet(subnet.getId(), subnet.getCidr(), subnet.getGatewayIp())).collect(Collectors.toList());
            }
            return  NetworkPageResponse.builder().name(networkModel.getName()).networkType(networkModel.getNetworkType())
                    .status(networkModel.getStatus()).subnets(subnets).createdAt(networkModel.getCreatedAt()).build();
                }
        ).collect(Collectors.toList());
        return new ListObjServiceResult<>(list,model.getTotal());
    }

}
