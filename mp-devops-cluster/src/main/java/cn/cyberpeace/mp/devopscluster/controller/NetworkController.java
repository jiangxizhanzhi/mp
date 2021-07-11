package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.cscene.bo.CsceneNodeInfoBO;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.NetworkConverter;
import cn.cyberpeace.mp.devopscluster.converter.bo.NetworkBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.NetworkSubnetBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.VlanIpStockBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.dto.NetworkPageDTO;
import cn.cyberpeace.mp.devopscluster.request.NetworkPageRequest;
import cn.cyberpeace.mp.devopscluster.service.SceneInfoService;
import cn.cyberpeace.mp.devopscluster.vo.NetworkPageVO;
import cn.cyberpeace.mp.devopscluster.vo.VlanIpStockVO;
import cn.cyberpeace.mp.devopscluster.vo.common.PageResult;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/network")
@Api(tags = "网络实例管理-网络")
@RequiredArgsConstructor
public class NetworkController {

    private final NetworkConverter networkConverter;

    private final SceneInfoService sceneInfoService;

    
    @ApiOperation("分页查询网络信息")
    @GetMapping("/page")
    public PageResult<NetworkPageVO> page(NetworkPageRequest request){
        BasePageBO<NetworkBO> model = networkConverter.page(new NetworkPageDTO(request));
        List<NetworkBO> networks = model.getList();
        List<NetworkPageVO> networkList = networks.stream().map(networkModel -> {
            List<NetworkSubnetBO> subnetList = networkModel.getSubnets();
            List<NetworkPageVO.Subnet> subnets = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(subnetList)) {
                subnets = subnetList.stream().map(subnet -> new NetworkPageVO.Subnet(subnet.getId(), subnet.getCidr(), subnet.getGatewayIp(), subnet.getFreeIps(), subnet.getUsedIps())).collect(Collectors.toList());
            }
            return NetworkPageVO.builder().name(networkModel.getName()).networkType(networkModel.getNetworkType())
                    .status(networkModel.getStatus()).subnets(subnets).createdAt(networkModel.getCreatedAt())
                    .downloadSpeed(networkModel.getDownloadSpeed())
                    .uploadSpeed(networkModel.getUploadSpeed())
                    
                    
                    .nodeNum(networkModel.getNodeNum())
                    .build();
                }
        ).collect(Collectors.toList());

        
        List<String> networkNames = networkList.stream().map(NetworkPageVO::getName).collect(Collectors.toList());
        Consumer<Map<String, CsceneNodeInfoBO>> consumer = (map) -> {
            networkList.forEach(net -> {
                var csceneNodeInfoBO = map.get(net.getName());
                if (Objects.nonNull(csceneNodeInfoBO)) {
                    net.setSceneName(StringUtils.isBlank(csceneNodeInfoBO.getCsceneName()) ? "" : csceneNodeInfoBO.getCsceneName());
                    net.setReferenceType(csceneNodeInfoBO.getReferenceType());
                }
            });
        };

        sceneInfoService.processSceneInfo(networkNames, consumer);

        return new PageResult<>(networkList,model.getTotal());
    }




    
    @ApiOperation("查询vlan ip占用信息")
    @GetMapping("/vlan_ip")
    public ServiceResult<VlanIpStockVO> vlanIpStock(){
        VlanIpStockBO bo = networkConverter.vlanIpStock();
        VlanIpStockVO vo = new VlanIpStockVO();
        BeanUtils.copyProperties(bo, vo);
        return ResultCommon.success(vo);
    }

}
