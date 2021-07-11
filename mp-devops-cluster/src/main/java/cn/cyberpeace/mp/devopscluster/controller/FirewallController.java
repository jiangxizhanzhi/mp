package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.cscene.bo.CsceneNodeInfoBO;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Firewall;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.FirewallConverter;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.dto.BasePageDTO;
import cn.cyberpeace.mp.devopscluster.request.BasePageRequest;
import cn.cyberpeace.mp.devopscluster.service.SceneInfoService;
import cn.cyberpeace.mp.devopscluster.vo.FirewallPageVO;
import cn.cyberpeace.mp.devopscluster.vo.common.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping(ControllerConstant.BASE_API_PATH + "/firewall")
@Api(tags = "网络实例管理-防火墙")
@RequiredArgsConstructor
public class FirewallController {

    private final FirewallConverter firewallConverter;

    private final SceneInfoService sceneInfoService;

    
    @ApiOperation("分页查询防火墙")
    @GetMapping("/page")
    public PageResult<FirewallPageVO> page(BasePageRequest request){
        request.ensure();
        BasePageBO<Firewall> model = firewallConverter.page(new BasePageDTO());
        List<FirewallPageVO> list = convertBOToPageVO(model);

        int total = list.size();
        int skip = (request.getPage() - 1) * request.getPerPage();
        List<FirewallPageVO> firewallList = list.stream().skip(skip).limit(request.getPerPage()).collect(Collectors.toList());

        
        List<String> resourceNames = firewallList.stream().map(FirewallPageVO::getName).collect(Collectors.toList());
        Consumer<Map<String, CsceneNodeInfoBO>> consumer = (map) -> {
            firewallList.forEach(firewall -> {
                var csceneNodeInfoBO = map.get(firewall.getName());
                if (Objects.nonNull(csceneNodeInfoBO)) {
                    firewall.setSceneName(StringUtils.isBlank(csceneNodeInfoBO.getCsceneName()) ? "" : csceneNodeInfoBO.getCsceneName());
                    firewall.setReferenceType(csceneNodeInfoBO.getReferenceType());
                }
            });
        };

        sceneInfoService.processSceneInfo(resourceNames, consumer);

        return new PageResult<>(firewallList, total);
    }

    
    private List<FirewallPageVO> convertBOToPageVO(BasePageBO<Firewall> model){
        return model.getList().stream().map(firewall ->
        {
            if (StringUtils.isNotBlank(firewall.getName()) && firewall.getName().startsWith("MP_SCENE_")) {
                return new FirewallPageVO(firewall.getName(),
                        Objects.nonNull(firewall.getIngressFirewallPolicy()) ? firewall.getIngressFirewallPolicy().getName() : null,
                        Objects.nonNull(firewall.getEgressFirewallPolicy()) ? firewall.getEgressFirewallPolicy().getName() : null,
                        firewall.getDescription(),firewall.getStatus());
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
