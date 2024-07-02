package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @ClassName FirewallController
 * @Description 网络管理类
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/firewall")
@Api(tags = "网络实例管理-防火墙")
@RequiredArgsConstructor
@AuthTarget(TargetEnum.NETWORK_MANAGEMENT)
public class FirewallController {

    private final FirewallConverter firewallConverter;

    private final SceneInfoService sceneInfoService;

    /**
     * 分页查询
     *
     * @param request
     * @return
     */
    @ApiOperation("分页查询防火墙")
    @GetMapping("/page")
    public PageResult<FirewallPageVO> page(BasePageRequest request){
        request.ensure();
        BasePageBO<Firewall> model = firewallConverter.page(new BasePageDTO());
        List<FirewallPageVO> list = convertBOToPageVO(model);

        int total = list.size();
        int skip = (request.getPage() - 1) * request.getPerPage();
        List<FirewallPageVO> firewallList = new ArrayList<>();
        long limit = request.getPerPage();
        long toSkip = skip;
        for (FirewallPageVO pageVO : list) {
            if (toSkip > 0) {
                toSkip--;
                continue;
            }
            if (limit-- == 0) break;
            firewallList.add(pageVO);
        }

        // 处理场景信息
        List<String> resourceNames = new ArrayList<>();
        for (FirewallPageVO firewallPageVO : firewallList) {
            String name = firewallPageVO.getName();
            resourceNames.add(name);
        }
        Consumer<Map<String, CsceneNodeInfoBO>> consumer = (map) -> {
            for (FirewallPageVO firewall : firewallList) {
                CsceneNodeInfoBO csceneNodeInfoBO = map.get(firewall.getName());
                if (Objects.nonNull(csceneNodeInfoBO)) {
                    firewall.setSceneName(StringUtils.isBlank(csceneNodeInfoBO.getCsceneName()) ? "" : csceneNodeInfoBO.getCsceneName());
                    firewall.setReferenceType(csceneNodeInfoBO.getReferenceType());
                }
            }
        };

        sceneInfoService.processSceneInfo(resourceNames, consumer);

        return new PageResult<>(firewallList, total);
    }

    /**
     * 转换BO to VO
     *
     * @param model
     * @return
     */
    private List<FirewallPageVO> convertBOToPageVO(BasePageBO<Firewall> model){
        List<FirewallPageVO> list = new ArrayList<>();
        for (Firewall firewall : model.getList()) {
            FirewallPageVO firewallPageVO = getFirewallPageVO(firewall);
            if (firewallPageVO != null) {
                list.add(firewallPageVO);
            }
        }
        return list;
    }

    /**
     * @param firewall
     * @return 网络分页返回信息
     */
    private static FirewallPageVO getFirewallPageVO(Firewall firewall) {
        if (StringUtils.isNotBlank(firewall.getName()) && firewall.getName().startsWith("MP_SCENE_")) {
            return new FirewallPageVO(firewall.getName(),
                    Objects.nonNull(firewall.getIngressFirewallPolicy()) ? firewall.getIngressFirewallPolicy().getName() : null,
                    Objects.nonNull(firewall.getEgressFirewallPolicy()) ? firewall.getEgressFirewallPolicy().getName() : null,
                    firewall.getDescription(), firewall.getStatus());
        }
        return null;
    }

}
