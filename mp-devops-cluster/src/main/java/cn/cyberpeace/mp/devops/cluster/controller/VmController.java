package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneNodeInfoBO;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.VmConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.InstanceConvertModel;
import cn.cyberpeace.mp.devops.cluster.request.InstancePageRequest;
import cn.cyberpeace.mp.devops.cluster.vo.InstancePageVO;
import cn.cyberpeace.mp.devops.cluster.vo.common.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 虚拟机管理类
 */
@Validated
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/instance")
@Api(tags = "计算实例管理-虚拟机")
@RequiredArgsConstructor
public class VmController {

    private final VmConverter vmConverter;

    private final CsceneSpecialService csceneSpecialService;

    /**
     * 分页查询虚拟机信息
     * @param request
     * @return
     */
    @ApiOperation("分页查询虚拟机信息")
    @GetMapping("/page")
    public PageResult<InstancePageVO> page(InstancePageRequest request){
        InstanceConvertModel model = vmConverter.page(request);
        List<InstanceConvertModel.Model> instances = model.getModelList();
        List<String> instanceNames = instances.stream().map(InstanceConvertModel.Model::getName).collect(Collectors.toList());
        ServiceResult<Map<String, CsceneNodeInfoBO>> serviceResult = csceneSpecialService.getSceneInfoByNodeResourceName(instanceNames);
        Map<String, CsceneNodeInfoBO> source = serviceResult.getSource();
        List<InstancePageVO> list = instances.stream().map(instanceModel ->
            InstancePageVO.builder().name(instanceModel.getName())
                    .sceneName(Objects.isNull(source.get(instanceModel.getName())) ? "" : source.get(instanceModel.getName()).getCsceneName())
                    .node(instanceModel.getNode())
                    .addressList(instanceModel.getAddressList())
                    .status(instanceModel.getStatus()).createTime(instanceModel.getCreateTime()).build()
        ).collect(Collectors.toList());

        return new PageResult<>(list, model.getTotal());
    }

}
