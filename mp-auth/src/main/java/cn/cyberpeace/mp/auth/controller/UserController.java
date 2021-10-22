package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.AuthLogConstant;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.ResetPwdRequest;
import cn.cyberpeace.mp.auth.dto.ResetSelfPwdRequest;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.dto.user.UserLogPageRequest;
import cn.cyberpeace.mp.auth.dto.user.UserOnlineCntDto;
import cn.cyberpeace.mp.auth.dto.user.UserOnlineReportPageRequest;
import cn.cyberpeace.mp.auth.dto.user.UserRequest;
import cn.cyberpeace.mp.auth.dto.validate.AddGroup;
import cn.cyberpeace.mp.auth.dto.validate.UpdateGroup;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.UserHeartbeatService;
import cn.cyberpeace.mp.auth.service.UserLogService;
import cn.cyberpeace.mp.auth.service.UserOnlineReportService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.param.UserQueryParams;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.log.annotation.OperateLog;
import cn.cyberpeace.mp.devops.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTypeEnums;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @Description: 用户管理接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "用户管理接口", tags = "AUTH-用户管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/users")
public class UserController extends BaseModelController<UserModel, UserQueryParams, UserService> {

    @Autowired
    private UserLogService userLogService;

    @Autowired
    private OrgUserService orgUserService;

    @Autowired
    private UserOnlineReportService userOnlineReportService;

    @Autowired
    private UserHeartbeatService heartbeatService;

    @Autowired
    public UserController(UserService modelService) {
        setModelService(modelService);
    }


    @Override
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
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "修改用户", target = AuthLogConstant.TARGET_USER)
    public ObjServiceResult<UserModel> update(@PathVariable String id, @RequestBody UserModel obj,
                                              @RequestParam(required = false) String[] fields) {
        return super.update(id, obj, fields);
    }

    @Override
    @ApiOperation("删除指定id用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
    })
    @DeleteMapping("/{id}")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.DELETE, title = "删除用户", target = AuthLogConstant.TARGET_USER)
    public ServiceResult<Boolean> delete(@PathVariable String id) {
        return super.delete(id);
    }

    @Override
    @ApiOperation(value = "查询指定id数据 附带组织，角色信息", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @GetMapping("/{id}")
    public ObjServiceResult get(@PathVariable String id, @RequestParam(required = false) String[] fields) {
        UserDto userDto = getModelService().getUserDto(id);
        return ResultUtils.obj2ObjServiceResult(userDto);
    }

    @ApiOperation(value = "查询当前登录用户信息 附带组织，角色信息", notes = "")
    @GetMapping("/current")
    public ServiceResult<UserDto> getCurrentUser() {
        UserDto userDto = getModelService().getCurrentUserDto();
        return ServiceResult.success(userDto);
    }


    @ApiOperation(value = "创建用户，同时完成组织，角色绑定关系", notes = "")
    @PostMapping("/add")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.CREATE, title = "新增用户", target = AuthLogConstant.TARGET_USER)
    public ServiceResult<UserModel> add(@RequestBody UserRequest createRequest) {
        UserModel userModel = getModelService().add(createRequest);
        return ServiceResult.success(userModel);
    }

    @ApiOperation(value = "修改用户，同时完成组织，角色绑定关系", notes = "")
    @PostMapping("/modify")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "更新用户", target = AuthLogConstant.TARGET_USER)
    public ServiceResult<UserModel> modify(@RequestBody UserRequest modifyRequest) {
        UserModel userModel = getModelService().modify(modifyRequest);
        return ServiceResult.success(userModel);
    }


    @ApiOperation(value = "修改密码", notes = "")
    @PostMapping("/resetpwd")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "重置密码", target = AuthLogConstant.TARGET_USER)
    public ServiceResult resetPwd(@Validated(AddGroup.class) @RequestBody ResetPwdRequest resetPwdRequest) {
        String decryptedPwd = PasswordUtils.desDecrypt(resetPwdRequest.getNewPwd());
        boolean success = getModelService().resetPwd(resetPwdRequest.getUserId(), decryptedPwd);
        return ServiceResult.successful();
    }

    @ApiOperation(value = "修改自己的密码", notes = "")
    @PostMapping("/resetpwd/self")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "重置本人密码", target = AuthLogConstant.TARGET_USER)
    public ServiceResult resetSelfPwd(@Validated(UpdateGroup.class) @RequestBody ResetSelfPwdRequest req) {
        String decryptedOldPwd = PasswordUtils.desDecrypt(req.getOldPwd());
        String decryptedNewPwd = PasswordUtils.desDecrypt(req.getNewPwd());
        boolean success = getModelService().resetSelfPwd(decryptedOldPwd, decryptedNewPwd);
        return ServiceResult.successful();
    }


    @ApiOperation(value = "禁用用户", notes = "")
    @PostMapping("/disable")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "禁用用户", target = AuthLogConstant.TARGET_USER)
    public ServiceResult<String> disable(@RequestBody List<String> userIds) {
        String errMsg = getModelService().batchDisable(userIds);
        if (StringUtils.isBlank(errMsg)) {
            return ServiceResult.successful();
        }
        return ServiceResult.success(errMsg);
    }


    @ApiOperation(value = "启用用户", notes = "")
    @PostMapping("/enable")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "启用用户", target = AuthLogConstant.TARGET_USER)
    public ServiceResult<String> enable(@RequestBody List<String> userIds) {
        String errMsg = getModelService().batchEnable(userIds);
        if (StringUtils.isBlank(errMsg)) {
            return ServiceResult.successful();
        }
        return ServiceResult.success(errMsg);
    }


    @ApiOperation(value = "审核用户", notes = "")
    @PostMapping("/approve")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "审核用户", target = AuthLogConstant.TARGET_USER)
    public ServiceResult<String> approve(@RequestBody List<String> userIds) {
        String errMsg = getModelService().batchApprove(userIds);
        if (StringUtils.isBlank(errMsg)) {
            return ServiceResult.successful();
        }
        return ServiceResult.success(errMsg);
    }


    @ApiOperation(value = "更改用户头像", notes = "")
    @PostMapping("/update_avatar")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "更改头像", target = AuthLogConstant.TARGET_USER)
    public ServiceResult updateAvatar(@RequestParam String userId, MultipartFile file) throws MpRuntimeException {
        boolean success = getModelService().updateAvatar(userId, file);
        return ServiceResult.successful();
    }


    @ApiOperation(value = "用户心跳-统计在线用户信息", notes = "")
    @PostMapping("/heartbeat")
    public ServiceResult heartbeat() throws MpRuntimeException {
        heartbeatService.heartbeat();
        return ServiceResult.successful();
    }


    @ApiOperation(value = "用户在线报告", notes = "")
    @GetMapping("/olreport")
    public ServiceResult onlineReport(UserOnlineReportPageRequest pageRequest) {
        pageRequest.ensure();
        PageInfo pageInfo = userOnlineReportService.page(pageRequest);
        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }

    @ApiOperation(value = "在线用户数量", notes = "")
    @GetMapping("/olUserCnt")
    public ServiceResult<UserOnlineCntDto> onlineUserCnt() {
        UserOnlineCntDto userOnlineCntDto = userOnlineReportService.onlineUserCnt();
        return ServiceResult.success(userOnlineCntDto);
    }



    @ApiOperation(value = "查询用户操作日志", notes = "")
    @GetMapping("/log")
    public ListObjServiceResult userlog(UserLogPageRequest logPageRequest) {
//        PageInfo pageInfo = userLogService.getUserLog(logPageRequest);
//        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
        throw new MpRuntimeException("Not Support Method!");
    }


}
