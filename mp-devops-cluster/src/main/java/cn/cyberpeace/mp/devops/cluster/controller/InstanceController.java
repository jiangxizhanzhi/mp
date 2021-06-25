package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.model.InstanceOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.request.InstancePageRequest;
import cn.cyberpeace.mp.devops.cluster.response.InstancePageResponse;
import cn.cyberpeace.mp.devops.cluster.service.InstanceService;
import cn.cyberpeace.mp.devops.cluster.service.fields.InstanceFields;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 实例管理类
 */
@Validated
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/instance")
public class InstanceController {

    private InstanceService instanceService;

    @Autowired
    public InstanceController(InstanceService instanceService) {
        this.instanceService = instanceService;
    }

    @ApiOperation("分页查询实例信息")
    @PostMapping("/page")
    public ListObjServiceResult page(@RequestBody @Valid InstancePageRequest request){

        InstanceOpenStackModel model = instanceService.page(request);
        List<InstanceOpenStackModel.InstanceModel> instances = model.getInstances();

        if (CollectionUtils.isEmpty(instances)){
            return new ListObjServiceResult();
        }

        List<InstancePageResponse> list = instances.stream().map(instanceModel ->
            InstancePageResponse.builder().name(instanceModel.getInstance_name()).node(instanceModel.getHostname())
                    .addressList(instanceModel.getAddresses().stream().map(InstanceOpenStackModel.InstanceModel.Address::getIp).collect(Collectors.toList()))
                    .status(instanceModel.getStatus()).createTime(instanceModel.getCreated_at()).build()
        ).collect(Collectors.toList());

        return new ListObjServiceResult(new ListObjResult(list, model.getTotal(), InstanceFields.allFields).asResult()).asResult();
    }

}
