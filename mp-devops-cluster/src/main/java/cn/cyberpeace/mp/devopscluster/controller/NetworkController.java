package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.result.Network;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.NetworkConverter;
import cn.cyberpeace.mp.devopscluster.converter.bo.NetworkBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.NetworkSubnetBO;
import cn.cyberpeace.mp.devopscluster.dto.NetworkPageDTO;
import cn.cyberpeace.mp.devopscluster.request.NetworkPageRequest;
import cn.cyberpeace.mp.devopscluster.service.SceneInfoService;
import cn.cyberpeace.mp.devopscluster.vo.NetworkPageVO;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 网络管理类
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/network")
@Api(tags = "网络实例管理-网络")
@RequiredArgsConstructor
@AuthTarget(TargetEnum.NETWORK_MANAGEMENT)
public class NetworkController {

    private final NetworkConverter networkConverter;

    private final SceneInfoService sceneInfoService;

    /**
     * 分页查询网络信息
     * @param request
     * @return
     */
    @ApiOperation("分页查询网络信息")
    @GetMapping("/page")
    public ListObjServiceResult<Network> page(NetworkPageRequest request) {
        return networkConverter.page(new NetworkPageDTO(request)).asResult();
    }

    private static NetworkPageVO getNetworkPageVO(NetworkBO networkModel) {
        List<NetworkSubnetBO> subnetList = networkModel.getSubnets();
        List<NetworkPageVO.Subnet> subnets = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(subnetList)) {
            List<NetworkPageVO.Subnet> list = new ArrayList<>();
            for (NetworkSubnetBO subnet : subnetList) {
                NetworkPageVO.Subnet subnet1 = new NetworkPageVO.Subnet(subnet.getId(), subnet.getCidr(), subnet.getGatewayIp());
                list.add(subnet1);
            }
            subnets = list;
        }
        return NetworkPageVO.builder().name(networkModel.getName()).networkType(networkModel.getNetworkType())
                .status(networkModel.getStatus()).subnets(subnets).createdAt(networkModel.getCreatedAt())
                .downloadSpeed(networkModel.getDownloadSpeed())
                .uploadSpeed(networkModel.getUploadSpeed())
                //.freeIps(networkModel.getFreeIps())
                //.usedIps(networkModel.getUsedIps())
                .nodeNum(networkModel.getNodeNum())
                .build();
    }



}
