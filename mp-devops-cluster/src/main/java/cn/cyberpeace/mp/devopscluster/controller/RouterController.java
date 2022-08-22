package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.RouterConverter;
import cn.cyberpeace.mp.devopscluster.converter.bo.RouterBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.dto.RouterPageDTO;
import cn.cyberpeace.mp.devopscluster.request.RouterPageRequest;
import cn.cyberpeace.mp.devopscluster.vo.RouterPageVO;
import cn.cyberpeace.mp.devopscluster.vo.common.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 路由管理类
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/router")
@Api(tags = "网络实例管理-路由")
@RequiredArgsConstructor
public class RouterController {

    private final RouterConverter routerConverter;

    /**
     * 分页查询路由信息
     * @param request
     * @return
     */
    @ApiOperation("分页查询路由信息")
    @GetMapping("/page")
    public PageResult<RouterPageVO> page(RouterPageRequest request){
        BasePageBO<RouterBO> model = routerConverter.page(new RouterPageDTO(request));
        List<RouterPageVO> list =  model.getList().stream().map(routerModel ->
                RouterPageVO.builder().name(routerModel.getName())
                        .status(routerModel.getStatus()).createdAt(routerModel.getCreatedAt()).build()
        ).collect(Collectors.toList());
        return new PageResult<>(list,model.getTotal());
    }

}
