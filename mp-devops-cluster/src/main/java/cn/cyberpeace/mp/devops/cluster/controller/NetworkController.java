package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.NetworkConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.NetworkPageConverterModel;
import cn.cyberpeace.mp.devops.cluster.request.NetworkPageRequest;
import cn.cyberpeace.mp.devops.cluster.vo.NetworkPageVO;
import cn.cyberpeace.mp.devops.cluster.vo.common.PageResult;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
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
@Api(tags = "网络实例管理-网络")
@RequiredArgsConstructor
public class NetworkController {

    private final NetworkConverter networkConverter;

    /**
     * 分页查询网络信息
     * @param request
     * @return
     */
    @ApiOperation("分页查询网络信息")
    @GetMapping("/page")
    public PageResult<NetworkPageVO> page(NetworkPageRequest request){
        NetworkPageConverterModel model = networkConverter.page(request);
        List<NetworkPageConverterModel.Model> networks = model.getModelList();
        List<NetworkPageVO> list = networks.stream().map(networkModel -> {
            List<NetworkPageConverterModel.Model.Subnet> subnetList = networkModel.getSubnets();
            List<NetworkPageVO.Subnet> subnets = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(subnetList)) {
                subnets = subnetList.stream().map(subnet -> new NetworkPageVO.Subnet(subnet.getId(), subnet.getCidr(), subnet.getGatewayIp())).collect(Collectors.toList());
            }
            return  NetworkPageVO.builder().name(networkModel.getName()).networkType(networkModel.getNetworkType())
                    .status(networkModel.getStatus()).subnets(subnets).createdAt(networkModel.getCreatedAt()).build();
                }
        ).collect(Collectors.toList());
        return new PageResult<>(list,model.getTotal());
    }

}
