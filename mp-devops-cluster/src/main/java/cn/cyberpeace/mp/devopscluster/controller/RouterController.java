 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RouterController.java.
 // File size 3421.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.cscene.bo.CsceneNodeInfoBO;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.RouterConverter;
import cn.cyberpeace.mp.devopscluster.converter.bo.RouterBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.dto.RouterPageDTO;
import cn.cyberpeace.mp.devopscluster.request.RouterPageRequest;
import cn.cyberpeace.mp.devopscluster.service.SceneInfoService;
import cn.cyberpeace.mp.devopscluster.vo.RouterPageVO;
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
 * @ClassName RouterController
 * @Description 路由管理类
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/router")
@Api(tags = "网络实例管理-路由")
@RequiredArgsConstructor
@AuthTarget(TargetEnum.NETWORK_MANAGEMENT)
public class RouterController {

    private final RouterConverter routerConverter;

    private final SceneInfoService sceneInfoService;

    /**
     * 分页查询路由信息
     *
     * @param request
     * @return
     */
    @ApiOperation("分页查询路由信息")
    @GetMapping("/page")
    public PageResult<RouterPageVO> page(RouterPageRequest request){
        BasePageBO<RouterBO> model = routerConverter.page(new RouterPageDTO(request));
        List<RouterPageVO> routerList = new ArrayList<>();
        for (RouterBO routerModel : model.getList()) {
            RouterPageVO build = RouterPageVO.builder().name(routerModel.getName())
                    .status(routerModel.getStatus()).createdAt(routerModel.getCreatedAt()).build();
            routerList.add(build);
        }

        // 处理场景信息
        List<String> routerNames = new ArrayList<>();
        for (RouterPageVO routerPageVO : routerList) {
            String name = routerPageVO.getName();
            routerNames.add(name);
        }
        Consumer<Map<String, CsceneNodeInfoBO>> consumer = (map) -> {
            for (RouterPageVO rout : routerList) {
                CsceneNodeInfoBO csceneNodeInfoBO = map.get(rout.getName());
                if (Objects.nonNull(csceneNodeInfoBO)) {
                    rout.setSceneName(StringUtils.isBlank(csceneNodeInfoBO.getCsceneName()) ? "" : csceneNodeInfoBO.getCsceneName());
                    rout.setReferenceType(csceneNodeInfoBO.getReferenceType());
                }
            }
        };

        sceneInfoService.processSceneInfo(routerNames, consumer);

        return new PageResult<>(routerList,model.getTotal());
    }

}
