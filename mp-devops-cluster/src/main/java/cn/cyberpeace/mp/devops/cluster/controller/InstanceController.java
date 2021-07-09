package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.enums.CloudPlatformEnum;
import cn.cyberpeace.mp.devops.cluster.model.InstanceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.InstancePageRequest;
import cn.cyberpeace.mp.devops.cluster.response.InstancePageResponse;
import cn.cyberpeace.mp.devops.cluster.service.InstanceService;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
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

    private InstanceService instanceService;

    @Autowired
    public InstanceController(InstanceService instanceService) {
        this.instanceService = instanceService;
    }

    /**
     * 分页查询虚拟机信息
     * @param request
     * @return
     */
    @ApiOperation("分页查询虚拟机信息")
    @GetMapping("/page")
    public ListObjServiceResult<InstancePageResponse> page(@Valid InstancePageRequest request){
        InstanceCloudPlatformModel model = instanceService.page(request);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getInstances())){
            return new ListObjServiceResult(Lists.newArrayList(),0);
        }
        List<InstanceCloudPlatformModel.InstanceModel> instances = model.getInstances();
        List<InstancePageResponse> list = instances.stream().map(instanceModel ->
            InstancePageResponse.builder().name(instanceModel.getName()).node(instanceModel.getHostname())
                    .addressList(instanceModel.getAddresses().stream().map(InstanceCloudPlatformModel.InstanceModel.Address::getIp).collect(Collectors.toList()))
                    .status(CloudPlatformEnum.fromValue(instanceModel.getStatus())).createTime(instanceModel.getCreated_at()).build()
        ).collect(Collectors.toList());

        return new ListObjServiceResult(list, model.getTotal());
    }

}
