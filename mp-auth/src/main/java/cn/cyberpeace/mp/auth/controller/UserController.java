package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.dto.user.UserRequest;
import cn.cyberpeace.mp.auth.dto.user.UserSimpleDto;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserHeartbeatService;
import cn.cyberpeace.mp.auth.service.UserOnlineRecordService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.param.UserQueryParams;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.log.annotation.OperateLog;
import cn.cyberpeace.mp.devops.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTypeEnums;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Description: 用户管理接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "用户管理接口", tags = "AUTH-用户管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/users")
public class UserController {

    @Autowired
    private UserService userService;

    /*@Override
    @ApiIgnore
    @ApiOperation(value = "(弃用，建议用/api/mp/auth/org_users/page/byorg) 分页查询组织下的用户", notes = "")
    @GetMapping
    public ListObjServiceResult<UserModel> list(UserQueryParams queryParams) {
        return super.list(queryParams);
    }

    @Override
    @ApiIgnore
    @ApiOperation(value = "(弃用，建议用/add) 创建数据", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @PostMapping
    public ObjServiceResult<UserModel> create(@RequestBody UserModel obj, @RequestParam(required = false) String[] fields) {
        return super.create(obj, fields);
    }

    @Override
    @ApiOperation(value = "(弃用，建议用/modify)更新指定id数据", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @PatchMapping("/{id}")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "修改用户", target = OpTargetEnums.USER)
    public ObjServiceResult<UserModel> update(@PathVariable String id, @RequestBody UserModel obj,
                                              @RequestParam(required = false) String[] fields) {
        return super.update(id, obj, fields);
    }*/

    @ApiOperation("删除指定id用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
    })
    @DeleteMapping("/{id}")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.DELETE, title = "删除用户", target = OpTargetEnums.USER)
    public ServiceResult<Boolean> delete(@PathVariable String id) {
        return userService.delete(id);
    }


    @ApiOperation(value = "查询指定id数据 附带组织，角色信息", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @GetMapping("/{id}")
    public ObjServiceResult get(@PathVariable String id, @RequestParam(required = false) String[] fields) {
        UserDto userDto = userService.getUserDto(id);
        return ResultUtils.obj2ObjServiceResult(userDto);
    }


    @ApiOperation(value = "查询指定id数据 返回有限数据，无登录用户权限拦截", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class)
    })
    @GetMapping("/no_restrict/{id}")
    public ObjServiceResult<UserSimpleDto> get(@PathVariable String id) {
        UserDto userDto = userService.getUserDtoNoRestrict(id);
        UserSimpleDto userSimpleDto = new UserSimpleDto();
        BeanUtils.copyProperties(userDto, userSimpleDto);

        return ResultUtils.obj2ObjServiceResult(userSimpleDto);
    }


    @ApiOperation(value = "查询当前登录用户信息 附带组织，角色信息", notes = "")
    @GetMapping("/current")
    public ServiceResult<UserDto> getCurrentUser() {
        UserDto userDto = userService.getCurrentUserDto();
        return ServiceResult.success(userDto);
    }


    @ApiOperation(value = "创建用户，同时完成组织，角色绑定关系", notes = "")
    @PostMapping("/add")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.CREATE, title = "新增用户", target = OpTargetEnums.USER)
    public ServiceResult<UserModel> add(@RequestBody UserRequest createRequest) {
        UserModel userModel = userService.add(createRequest);
        return ServiceResult.success(userModel);
    }

    @ApiOperation(value = "修改用户，同时完成组织，角色绑定关系", notes = "")
    @PostMapping("/modify")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "更新用户", target = OpTargetEnums.USER)
    public ServiceResult<UserModel> modify(@RequestBody UserRequest modifyRequest) {
        UserModel userModel = userService.modify(modifyRequest);
        return ServiceResult.success(userModel);
    }

}
