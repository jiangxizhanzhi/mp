package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.situationaware.model.GroupModel;
import cn.cyberpeace.mp.situationaware.model.GroupRequest;
import cn.cyberpeace.mp.situationaware.service.NetworkTrafficGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/flow_group")
@Api(value = "态势感知-场景的操作接口", tags = "态势感知-场景的操作接口")
public class NetworkTrafficGroupController {

    @Autowired
    private NetworkTrafficGroupService networkTrafficGroupService;

    @GetMapping("/groups")
    @ApiOperation(value = "查询所有分组信息", notes = "")
    public ServiceResult<List<GroupModel>> queryAllGroups() {
        List<GroupModel> groupModels = networkTrafficGroupService.queryAllGroups();
        return ServiceResult.<List<GroupModel>>builder().data(groupModels).build();
    }

    @GetMapping("/{groupId}")
    @ApiOperation(value = "根据groupId查询分组信息", notes = "")
    public ServiceResult<GroupModel> queryGroupModelById(@PathVariable("groupId") String groupId){
        var groupModel = networkTrafficGroupService.queryGroupById(groupId);
        return ServiceResult.<GroupModel>builder().data(groupModel).build();
    }

    @DeleteMapping("/{groupId}")
    @ApiOperation(value = "根据groupId删除分组信息", notes = "")
    public ServiceResult deleteModelById(@PathVariable("groupId") String groupId) {
        networkTrafficGroupService.deleteByGroupId(groupId);
        return ServiceResult.successful();
    }


    @PostMapping
    @ApiOperation(value = "新增分组信息", notes = "")
    public ServiceResult addGroup(@RequestBody @Valid GroupRequest groupRequest) {
        var groupModel = new GroupModel();
        BeanUtils.copyProperties(groupRequest, groupModel);
        String res = networkTrafficGroupService.addGroup(groupModel);
        return ServiceResult.builder().detail(res).serviceCode(BaseErrorCode.SUCCESS).build().asResult();
    }
}
