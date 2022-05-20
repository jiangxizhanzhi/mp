package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.situationaware.model.GroupModel;
import cn.cyberpeace.mp.situationaware.model.GroupRequest;
import cn.cyberpeace.mp.situationaware.service.NetworkTrafficGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/flow_group")
@Api(value = "态势感知-场景的操作接口", tags = "态势感知-场景的操作接口")
public class NetworkTrafficGroupController {

    @Autowired
    private NetworkTrafficGroupService networkTrafficGroupService;

    @GetMapping("/init-ark-network")
    @ApiOperation(value = "初始化流量网络", notes = "")
    public ServiceResult initArkimeNetwork() {
        networkTrafficGroupService.initArkimeNetwork();
        return ServiceResult.successful();
    }

    @DeleteMapping("/{groupId}")
    @ApiOperation(value = "根据groupId删除分组信息", notes = "")
    public ServiceResult deleteModelById(@PathVariable("groupId") String groupId) {
        networkTrafficGroupService.deleteArkimeInstance(groupId);
        return ServiceResult.successful();
    }


    @PostMapping
    @ApiOperation(value = "新增分组信息", notes = "")
    public ServiceResult addGroup(@RequestBody @Valid GroupRequest groupRequest) {
        var groupModel = new GroupModel();
        BeanUtils.copyProperties(groupRequest, groupModel);
        String res = networkTrafficGroupService.addArkimeImageInstance(groupModel);
        return ServiceResult.builder().detail(res).serviceCode(BaseErrorCode.SUCCESS).build().asResult();
    }
}
