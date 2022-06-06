package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneNodeInfoBO;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.ContainerConverter;
import cn.cyberpeace.mp.devops.cluster.converter.bo.ContainerBO;
import cn.cyberpeace.mp.devops.cluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devops.cluster.request.ContainerPageRequest;
import cn.cyberpeace.mp.devops.cluster.vo.ContainerPageVO;
import cn.cyberpeace.mp.devops.cluster.vo.common.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 容器管理类
 * @author wanggang
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/container")
@Api(tags = "计算实例管理-容器")
@RequiredArgsConstructor
public class ContainerController {

    private final ContainerConverter containerConverter;

    private final CsceneSpecialService csceneSpecialService;

    /**
     * 分页查询容器列表
     * @param request
     * @return
     */
    @ApiOperation("分页查询容器列表")
    @GetMapping("/page")
    public PageResult<ContainerPageVO> page(ContainerPageRequest request){
        BasePageBO<ContainerBO> model = containerConverter.page(request);
        List<String> nameList = model.getList().stream().map(ContainerBO::getName).collect(Collectors.toList());
        ServiceResult<Map<String, CsceneNodeInfoBO>> serviceResult = csceneSpecialService.getSceneInfoByNodeResourceName(nameList);
        Map<String, CsceneNodeInfoBO> source = serviceResult.getSource();
        List<ContainerPageVO> list = model.getList().stream().map(containerModel ->
                ContainerPageVO.builder().name(containerModel.getName())
                        .sceneName(Objects.isNull(source.get(containerModel.getName())) ? "" : source.get(containerModel.getName()).getCsceneName())
                .image(containerModel.getImage()).node(containerModel.getNode())
                .status(containerModel.getStatus()).addressList(containerModel.getAddressList())
                        .vlanIdList(containerModel.getVlanIdList()).build())
                .collect(Collectors.toList());
        return new PageResult<>(list, model.getTotal());
    }

}
