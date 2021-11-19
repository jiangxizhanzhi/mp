package cn.cyberpeace.mp.front.business.controller;

import cn.cyberpeace.mp.auth.constant.AuthLogConstant;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.ResetPwdRequest;
import cn.cyberpeace.mp.auth.dto.ResetSelfPwdRequest;
import cn.cyberpeace.mp.auth.dto.user.UserOnlineCntDto;
import cn.cyberpeace.mp.auth.dto.user.UserOnlineRecordSummaryPageRequest;
import cn.cyberpeace.mp.auth.dto.validate.AddGroup;
import cn.cyberpeace.mp.auth.dto.validate.UpdateGroup;
import cn.cyberpeace.mp.auth.service.UserHeartbeatService;
import cn.cyberpeace.mp.auth.service.UserOnlineRecordService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.log.annotation.OperateLog;
import cn.cyberpeace.mp.devops.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTypeEnums;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Description: 用户管理接口(业务)
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
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
    @PostMapping("/resetpwd")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "重置密码", target = AuthLogConstant.TARGET_USER)
    public ServiceResult resetPwd(@Validated(AddGroup.class) @RequestBody ResetPwdRequest resetPwdRequest) {
        String decryptedPwd = PasswordUtils.desDecrypt(resetPwdRequest.getNewPwd());
        boolean success = userService.resetPwd(resetPwdRequest.getUserId(), decryptedPwd);
        return ServiceResult.successful();
    }

    @ApiOperation(value = "修改自己的密码", notes = "")
    @PostMapping("/resetpwd/self")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "重置本人密码", target = AuthLogConstant.TARGET_USER)
    public ServiceResult resetSelfPwd(@Validated(UpdateGroup.class) @RequestBody ResetSelfPwdRequest req) {
        String decryptedOldPwd = PasswordUtils.desDecrypt(req.getOldPwd());
        String decryptedNewPwd = PasswordUtils.desDecrypt(req.getNewPwd());
        boolean success = userService.resetSelfPwd(decryptedOldPwd, decryptedNewPwd);
        return ServiceResult.successful();
    }


    @ApiOperation(value = "禁用用户", notes = "")
    @PostMapping("/disable")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "禁用用户", target = AuthLogConstant.TARGET_USER)
    public ServiceResult<String> disable(@RequestBody List<String> userIds) {
        String errMsg = userService.batchDisable(userIds);
        if (StringUtils.isBlank(errMsg)) {
            return ServiceResult.successful();
        }
        return ServiceResult.success(errMsg);
    }


    @ApiOperation(value = "启用用户", notes = "")
    @PostMapping("/enable")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "启用用户", target = AuthLogConstant.TARGET_USER)
    public ServiceResult<String> enable(@RequestBody List<String> userIds) {
        String errMsg = userService.batchEnable(userIds);
        if (StringUtils.isBlank(errMsg)) {
            return ServiceResult.successful();
        }
        return ServiceResult.success(errMsg);
    }

    @ApiOperation(value = "审核用户", notes = "")
    @PostMapping("/approve")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "审核用户", target = AuthLogConstant.TARGET_USER)
    public ServiceResult<String> approve(@RequestBody List<String> userIds) {
        String errMsg = userService.batchApprove(userIds);
        if (StringUtils.isBlank(errMsg)) {
            return ServiceResult.successful();
        }
        return ServiceResult.success(errMsg);
    }

    @ApiOperation(value = "更改用户头像", notes = "")
    @PostMapping("/update_avatar")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "更改头像", target = AuthLogConstant.TARGET_USER)
    public ServiceResult updateAvatar(@RequestParam String userId, MultipartFile file) throws MpRuntimeException {
        boolean success = userService.updateAvatar(userId, file);
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
    public ServiceResult onlineReport(UserOnlineRecordSummaryPageRequest pageRequest) {
        pageRequest.ensure();
        PageInfo pageInfo = userOnlineRecordService.recordSummaryPage(pageRequest);
        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }

    @ApiOperation(value = "在线用户数量", notes = "")
    @GetMapping("/olUserCnt")
    public ServiceResult<UserOnlineCntDto> onlineUserCnt() {
        UserOnlineCntDto userOnlineCntDto = userOnlineRecordService.onlineUserCnt();
        return ServiceResult.success(userOnlineCntDto);
    }

}
