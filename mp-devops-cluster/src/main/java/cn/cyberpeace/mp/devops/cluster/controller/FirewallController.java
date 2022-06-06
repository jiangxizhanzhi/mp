package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Firewall;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.FirewallConverter;
import cn.cyberpeace.mp.devops.cluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devops.cluster.request.FirewallPageRequest;
import cn.cyberpeace.mp.devops.cluster.vo.FirewallPageVO;
import cn.cyberpeace.mp.devops.cluster.vo.common.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping(ControllerConstant.BASE_API_PATH + "/firewall")
@Api(tags = "网络实例管理-防火墙")
@RequiredArgsConstructor
public class FirewallController {

    private final FirewallConverter firewallConverter;

    /**
     * 分页查询
     * @param request
     * @return
     */
    @ApiOperation("分页查询防火墙")
    @GetMapping("/page")
    public PageResult<FirewallPageVO> page(FirewallPageRequest request){
        BasePageBO<Firewall> model = firewallConverter.page(request);
        List<FirewallPageVO> list = model.getList().stream().map(firewall ->
            new FirewallPageVO(firewall.getName(),
                    Objects.nonNull(firewall.getIngressFirewallPolicy()) ? firewall.getIngressFirewallPolicy().getName() : null,
                    Objects.nonNull(firewall.getEgressFirewallPolicy()) ? firewall.getEgressFirewallPolicy().getName() : null,
                    firewall.getDescription(),firewall.getStatus())
        ).collect(Collectors.toList());
        return new PageResult<>(list,model.getTotal());
    }

}
