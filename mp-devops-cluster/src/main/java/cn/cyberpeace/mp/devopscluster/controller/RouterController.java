package cn.cyberpeace.mp.devopscluster.controller;

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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/router")
@Api(tags = "网络实例管理-路由")
@RequiredArgsConstructor
public class RouterController {

    private final RouterConverter routerConverter;

    private final SceneInfoService sceneInfoService;

    
    @ApiOperation("分页查询路由信息")
    @GetMapping("/page")
    public PageResult<RouterPageVO> page(RouterPageRequest request){
        BasePageBO<RouterBO> model = routerConverter.page(new RouterPageDTO(request));
        List<RouterPageVO> routerList =  model.getList().stream().map(routerModel ->
                RouterPageVO.builder().name(routerModel.getName())
                        .status(routerModel.getStatus()).createdAt(routerModel.getCreatedAt()).build()
        ).collect(Collectors.toList());

        
        List<String> routerNames = routerList.stream().map(RouterPageVO::getName).collect(Collectors.toList());
        Consumer<Map<String, CsceneNodeInfoBO>> consumer = (map) -> {
            routerList.forEach(rout -> {
                var csceneNodeInfoBO = map.get(rout.getName());
                if (Objects.nonNull(csceneNodeInfoBO)) {
                    rout.setSceneName(StringUtils.isBlank(csceneNodeInfoBO.getCsceneName()) ? "" : csceneNodeInfoBO.getCsceneName());
                    rout.setReferenceType(csceneNodeInfoBO.getReferenceType());
                }
            });
        };

        sceneInfoService.processSceneInfo(routerNames, consumer);

        return new PageResult<>(routerList,model.getTotal());
    }

}
