package cn.cyberpeace.mp.frontbusiness.controller;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.bo.user.UserOnlineCntBo;
import cn.cyberpeace.mp.auth.bo.user.UserOnlineReportBo;
import cn.cyberpeace.mp.auth.request.user.ResetPwdRequest;
import cn.cyberpeace.mp.auth.request.user.ResetSelfPwdRequest;
import cn.cyberpeace.mp.auth.request.user.UserOnlineRecordSummaryPageRequest;
import cn.cyberpeace.mp.auth.request.validate.AddGroup;
import cn.cyberpeace.mp.auth.request.validate.UpdateGroup;
import cn.cyberpeace.mp.auth.service.UserHeartbeatService;
import cn.cyberpeace.mp.auth.service.UserOnlineRecordService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(value = "用户管理接口(业务)", tags = "AUTH-用户管理接口(业务)")
@Slf4j
@RestController("userFrontController")
@RequestMapping(ControllerConstant.BASE_API_PATH + "/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserOnlineRecordService userOnlineRecordService;

    @Autowired
    private UserHeartbeatService heartbeatService;

    @ApiOperation(value = "修改密码", notes = "")
    @ApiImplicitParam(name="resetPwdRequest", value="修改请求", dataTypeClass = ResetPwdRequest.class)
    @PostMapping("/resetpwd")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "重置密码", target = OpTargetEnums.USER)
    public ServiceResult resetPwd(@Validated(AddGroup.class) @RequestBody ResetPwdRequest resetPwdRequest) {
        String decryptedPwd = PasswordUtils.desDecrypt(resetPwdRequest.getNewPwd());
        boolean success = userService.resetPwd(resetPwdRequest.getUserId(), decryptedPwd);
        return ResultCommon.success();
    }

    @ApiOperation(value = "修改自己的密码", notes = "")
    @ApiImplicitParam(name="req", value="修改请求", dataTypeClass = ResetSelfPwdRequest.class)
    @PostMapping("/resetpwd/self")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "重置本人密码", target = OpTargetEnums.USER)
    public ServiceResult resetSelfPwd(@Validated(UpdateGroup.class) @RequestBody ResetSelfPwdRequest req) {
        String decryptedOldPwd = PasswordUtils.desDecrypt(req.getOldPwd());
        String decryptedNewPwd = PasswordUtils.desDecrypt(req.getNewPwd());
        boolean success = userService.resetSelfPwd(decryptedOldPwd, decryptedNewPwd);
        return ResultCommon.success();
    }


    @ApiOperation(value = "禁用用户", notes = "")
    @ApiImplicitParam(name = "userIds", value = "id列表", dataTypeClass = List.class)
    @PostMapping("/disable")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "禁用用户", target = OpTargetEnums.USER)
    public ServiceResult<String> disable(@RequestBody List<String> userIds) {
        String errMsg = userService.batchDisable(userIds);
        if (StringUtils.isBlank(errMsg)) {
            return ResultCommon.success();
        }
        return ResultCommon.success(errMsg);
    }


    @ApiOperation(value = "启用用户", notes = "")
    @ApiImplicitParam(name = "userIds", value = "id列表", dataTypeClass = List.class, paramType = "body", required = true)
    @PostMapping("/enable")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "启用用户", target = OpTargetEnums.USER)
    public ServiceResult<String> enable(@RequestBody List<String> userIds) {
        String errMsg = userService.batchEnable(userIds);
        if (StringUtils.isBlank(errMsg)) {
            return ResultCommon.success();
        }
        return ResultCommon.success(errMsg);
    }

    @ApiOperation(value = "审核用户", notes = "")
    @ApiImplicitParam(name = "userIds", value = "id列表", dataTypeClass = List.class, paramType = "query")
    @PostMapping("/approve")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "审核用户", target = OpTargetEnums.USER)
    public ServiceResult<String> approve(@RequestBody List<String> userIds) {
        String errMsg = userService.batchApprove(userIds);
        if (StringUtils.isBlank(errMsg)) {
            return ResultCommon.success();
        }
        return ResultCommon.success(errMsg);
    }


    @ApiOperation(value = "用户心跳-统计在线用户信息", notes = "")
    @PostMapping("/heartbeat")
    public ServiceResult heartbeat() throws MpRuntimeException {
        heartbeatService.heartbeat();
        return ResultCommon.success();
    }

    @ApiOperation(value = "用户在线报告", notes = "")
    @GetMapping("/olreport")
    public ServiceResult onlineReport(UserOnlineRecordSummaryPageRequest pageRequest) {
        pageRequest.ensure();
        PageInfo<UserOnlineReportBo> pageInfo = userOnlineRecordService.recordSummaryPage(pageRequest);
        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }

    @ApiOperation(value = "在线用户数量", notes = "")
    @GetMapping("/olUserCnt")
    public ServiceResult<UserOnlineCntBo> onlineUserCnt() {
        UserOnlineCntBo userOnlineCntBo = userOnlineRecordService.onlineUserCnt();
        return ResultCommon.success(userOnlineCntBo);
    }

}
