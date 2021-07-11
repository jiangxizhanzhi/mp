package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.bo.user.UserSimpleBo;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.request.user.UserRequest;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Api(value = "用户管理接口", tags = "AUTH-用户管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/users")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("删除指定id用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.DELETE, title = "删除用户", target = OpTargetEnums.USER)
    public ServiceResult<Boolean> delete(@PathVariable String id) {
        return userService.delete(id);
    }

    
    @ApiOperation("批量删除用户")
    @ApiImplicitParam(name="ids", value="用户id列表",dataTypeClass = String[].class)
    @DeleteMapping("/bulk_delete")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.DELETE, title = "批量删除用户", target = OpTargetEnums.USER)
    public ServiceResult<Integer> bulkDelete(@RequestBody String[] ids) {
        return userService.bulkDelete(ids).asResult();
    }


    @ApiOperation(value = "查询指定id数据 附带组织，角色信息", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @GetMapping("/{id}")
    public ObjServiceResult<UserBo> get(@PathVariable String id, @RequestParam(required = false) String[] fields) {
        UserBo userBo = userService.getUserDto(id);
        return ResultUtils.obj2ObjServiceResult(userBo);
    }


    @ApiOperation(value = "查询指定id数据 返回有限数据，无登录用户权限拦截", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class)
    })
    @GetMapping("/no_restrict/{id}")
    public ObjServiceResult<UserSimpleBo> get(@PathVariable String id) {
        UserBo userBo = userService.getUserDtoNoRestrict(id);
        if (Objects.nonNull(userBo)) {
            UserSimpleBo userSimpleBo = new UserSimpleBo();
            BeanUtils.copyProperties(userBo, userSimpleBo);
            return ResultUtils.obj2ObjServiceResult(userSimpleBo);
        }

        return new ObjServiceResult(null).asResult();
    }

    @ApiOperation(value = "查询指定ids列表 返回有限数据，无登录用户权限拦截", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name="ids", paramType="body", value="数据ids",dataTypeClass = List.class)
    })
    @PostMapping("/no_restrict/by_ids")
    public ServiceResult<List<UserSimpleBo>> get(@RequestBody List<String> ids) {
        var userModels = userService.getUserModelNoRestrictByUserIds(ids);

        List<UserSimpleBo> userSimpleBos = new ArrayList<>();
        userModels.forEach(userModel -> {
            UserSimpleBo userSimpleBo = new UserSimpleBo();
            BeanUtils.copyProperties(userModel, userSimpleBo);
            userSimpleBos.add(userSimpleBo);
        });

        return ResultCommon.success(userSimpleBos);
    }


    @ApiOperation(value = "查询当前登录用户信息 附带组织，角色信息", notes = "")
    @GetMapping("/current")
    public ServiceResult<UserBo> getCurrentUser() {
        UserBo userBo = userService.getCurrentUserDto();
        return ResultCommon.success(userBo);
    }


    @ApiOperation(value = "创建用户，同时完成组织，角色绑定关系", notes = "")
    @ApiImplicitParam(name = "createRequest", value = "请求", dataTypeClass = UserRequest.class)
    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.CREATE, title = "新增用户", target = OpTargetEnums.USER)
    public ServiceResult<UserModel> add(@RequestBody UserRequest createRequest) {
        createRequest.setAvatar(FsCommon.getFileKey(createRequest.getAvatar()));
        UserModel userModel = userService.add(createRequest);
        return ResultCommon.success(userModel);
    }

    @ApiOperation(value = "修改用户，同时完成组织，角色绑定关系", notes = "")
    @ApiImplicitParam(name = "modifyRequest", value = "修改请求", dataTypeClass = UserRequest.class)
    @PostMapping("/modify")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "更新用户", target = OpTargetEnums.USER)
    public ServiceResult<UserModel> modify(@RequestBody UserRequest modifyRequest) {

        UserModel userModel = userService.modify(modifyRequest);
        return ResultCommon.success(userModel);
    }
}
