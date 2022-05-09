package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.VmConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.InstanceConvertModel;
import cn.cyberpeace.mp.devops.cluster.request.InstancePageRequest;
import cn.cyberpeace.mp.devops.cluster.response.InstancePageResponse;
import cn.cyberpeace.mp.devops.cluster.response.base.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 虚拟机管理类
 */
@Validated
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/instance")
@Api(tags = "计算实例管理-虚拟机")
public class VmController {

    private VmConverter vmConverter;

    @Autowired
    public VmController(VmConverter vmConverter) {
        this.vmConverter = vmConverter;
    }

    /**
     * 分页查询虚拟机信息
     * @param request
     * @return
     */
    @ApiOperation("分页查询虚拟机信息")
    @GetMapping("/page")
    public PageResult<InstancePageResponse> page(InstancePageRequest request){
        InstanceConvertModel model = vmConverter.page(request);
        List<InstanceConvertModel.Model> instances = model.getModelList();
        List<InstancePageResponse> list = instances.stream().map(instanceModel ->
            InstancePageResponse.builder().name(instanceModel.getName()).node(instanceModel.getNode())
                    .addressList(instanceModel.getAddressList())
                    .status(instanceModel.getStatus()).createTime(instanceModel.getCreateTime()).build()
        ).collect(Collectors.toList());

        return new PageResult<>(list, model.getTotal());
    }

}
