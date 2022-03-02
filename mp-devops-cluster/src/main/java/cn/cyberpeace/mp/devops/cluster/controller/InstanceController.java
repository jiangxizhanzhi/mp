package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.FlavorConverter;
import cn.cyberpeace.mp.devops.cluster.converter.InstanceConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.FlavorConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.InstanceConvertModel;
import cn.cyberpeace.mp.devops.cluster.request.InstancePageRequest;
import cn.cyberpeace.mp.devops.cluster.response.InstancePageResponse;
import cn.cyberpeace.mp.devops.cluster.response.base.PageResult;
import cn.cyberpeace.mp.devops.cluster.vo.FlavorVO;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
public class InstanceController {

    private InstanceConverter instanceConverter;

    private FlavorConverter flavorConverter;

    @Autowired
    public InstanceController(InstanceConverter instanceConverter,FlavorConverter flavorConverter) {
        this.instanceConverter = instanceConverter;
        this.flavorConverter = flavorConverter;
    }

    /**
     * 分页查询虚拟机信息
     * @param request
     * @return
     */
    @ApiOperation("分页查询虚拟机信息")
    @GetMapping("/page")
    public PageResult<InstancePageResponse> page(InstancePageRequest request){
        InstanceConvertModel model = instanceConverter.page(request);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getModelList())){
            return new PageResult<>(Lists.newArrayList(), NumberUtils.INTEGER_ZERO);
        }
        List<InstanceConvertModel.Model> instances = model.getModelList();

        List<InstancePageResponse> list = instances.stream().map(instanceModel ->
                {
                    InstancePageResponse response = InstancePageResponse.builder().name(instanceModel.getName()).node(instanceModel.getNode())
                            .addressList(instanceModel.getAddressList())
                            .status(instanceModel.getStatus()).createTime(instanceModel.getCreateTime()).build();
                    FlavorConvertModel flavorConvertModel = flavorConverter.getById(instanceModel.getFlavor());
                    response.setFlavor(buildFlavorVO(flavorConvertModel));
                    return response;
                }

        ).collect(Collectors.toList());

        return new PageResult<>(list, model.getTotal());
    }

    /**
     * 构建FlavorVO对象
     * @param flavor
     * @return
     */
    private FlavorVO buildFlavorVO(FlavorConvertModel flavor){
        if (Objects.isNull(flavor)){
            return null;
        }
        FlavorVO vo = new FlavorVO();
        vo.setCpu(flavor.getCpu());
        vo.setDescription(flavor.getDescription());
        vo.setDisk(flavor.getDisk());
        vo.setId(flavor.getId());
        vo.setName(flavor.getName());
        vo.setUuid(flavor.getUuid());
        vo.setRam(flavor.getRam());
        return vo;
    }

}
