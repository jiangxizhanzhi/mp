package cn.cyberpeace.mp.datasandtable.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.datasandtable.model.GroupModel;
import cn.cyberpeace.mp.datasandtable.model.GroupRequest;
import cn.cyberpeace.mp.datasandtable.service.GroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/flow_group")
@Api(value = "数据沙盘-场景的操作接口", tags = "数据沙盘-场景的操作接口")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @GetMapping("/groups")
    @ApiOperation(value = "查询所有分组信息", notes = "")
    public ServiceResult<List<GroupModel>> queryAllGroups() {
        List<GroupModel> groupModels = groupService.queryAllGroups();
        return ServiceResult.<List<GroupModel>>builder().data(groupModels).build();
    }

    @GetMapping("/{groupId}")
    @ApiOperation(value = "根据groupId查询分组信息", notes = "")
    public ServiceResult<GroupModel> queryGroupModelById(@PathVariable("groupId") String groupId){
        var groupModel = groupService.queryGroupById(groupId);
        return ServiceResult.<GroupModel>builder().data(groupModel).build();
    }

    @DeleteMapping("/{groupId}")
    @ApiOperation(value = "根据groupId删除分组信息", notes = "")
    public ServiceResult deleteModelById(@PathVariable("groupId") String groupId) {
        groupService.deleteByGroupId(groupId);
        return ServiceResult.successful();
    }


    @PostMapping
    @ApiOperation(value = "新增分组信息", notes = "")
    public ServiceResult addGroup(@RequestBody @Valid GroupRequest groupRequest) {
        var groupModel = new GroupModel();
        BeanUtils.copyProperties(groupRequest, groupModel);
        groupService.addGroup(groupModel);
        return ServiceResult.successful();
    }
}
