package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.FirewallConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.FirewallPageConverterModel;
import cn.cyberpeace.mp.devops.cluster.request.FirewallPageRequest;
import cn.cyberpeace.mp.devops.cluster.response.FirewallPageResponse;
import cn.cyberpeace.mp.devops.cluster.response.base.PageResult;
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
@RequestMapping(ControllerConstant.BASE_API_PATH + "/firewall")
@Api(tags = "网络实例管理-防火墙")
public class FirewallController {

    private FirewallConverter firewallConverter;

    @Autowired
    public FirewallController(FirewallConverter firewallConverter) {
        this.firewallConverter = firewallConverter;
    }

    /**
     * 分页查询
     * @param request
     * @return
     */
    @ApiOperation("分页查询防火墙")
    @GetMapping("/page")
    public PageResult<FirewallPageResponse> page(FirewallPageRequest request){
        FirewallPageConverterModel model = firewallConverter.page(request);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getModelList())){
            return new PageResult<>(Lists.newArrayList(), NumberUtils.INTEGER_ZERO);
        }
        List<FirewallPageResponse> list = model.getModelList().stream().map(firewall ->
            new FirewallPageResponse(firewall.getName(),
                    Objects.nonNull(firewall.getIngressFirewallPolicy()) ? firewall.getIngressFirewallPolicy().getName() : null,
                    Objects.nonNull(firewall.getEgressFirewallPolicy()) ? firewall.getEgressFirewallPolicy().getName() : null,
                    firewall.getDescription(),firewall.getStatus())
        ).collect(Collectors.toList());
        return new PageResult<>(list,model.getTotal());
    }

}
