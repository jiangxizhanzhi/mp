package cn.cyberpeace.mp.auth.test;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.constant.CustomAuthErrorCode;
import cn.cyberpeace.mp.auth.model.UserOnlineRecordModel;
import cn.cyberpeace.mp.auth.repository.OrgMapper;
import cn.cyberpeace.mp.auth.repository.SysConfigMapper;
import cn.cyberpeace.mp.auth.repository.UserRoleMapper;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.RolePermissionModelService;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.auth.service.UserHeartbeatService;
import cn.cyberpeace.mp.auth.service.UserLogService;
import cn.cyberpeace.mp.auth.service.UserOnlineRecordService;
import cn.cyberpeace.mp.auth.service.UserOnlineReportService;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.sys.LicenseService;
import cn.cyberpeace.mp.auth.service.sys.SysActivationService;
import cn.cyberpeace.mp.auth.util.PasswordStrengthChecker;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "TEST", tags = "AUTH-TEST")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/test")
public class AuthTestController {

    @Autowired
    private TmpMenuService tmpMenuService;

    @Autowired
    private UserLogService userLogService;

    @Autowired
    private TargetService targetService;

    @Autowired
    private OrgMapper orgMapper;

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private UserHeartbeatService heartbeatService;

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private SysActivationService activationService;

    @Autowired
    private OrgService orgService;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private UserPermissionTestMapper userPermissionTestMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserPermissionService userPermissionService;

    @Autowired
    private RolePermissionModelService rolePermissionModelService;

    @Autowired
    private UserOnlineRecordService userOnlineRecordService;

    @Autowired
    private UserOnlineReportService userOnlineReportService;

    @ApiOperation("DES加密")
    @PostMapping("/desEncryptPwd")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pwd", value = "密码字段（明文）", dataTypeClass = String[].class),
    })
    public String desEncryptPwd(@RequestParam String pwd) {
        return PasswordUtils.desEncrypt(pwd);
    }

    @ApiOperation("DES解密")
    @PostMapping("/desDecryptPwd")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pwd", value = "密码字段（密文）", dataTypeClass = String[].class),
    })
    public String desDecryptPwd(@RequestParam String pwd) {
        return PasswordUtils.desDecrypt(pwd);
    }

    @ApiOperation("spring security密码加密")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pwd", value = "密码字段（明文）", dataTypeClass = String[].class),
    })
    @PostMapping("/encryptPwd")
    public String encryptPwd(@RequestParam String pwd) {
        return PasswordUtils.encode(pwd);
    }


    @ApiOperation("密码强度查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pwd", value = "密码字段（明文）", dataTypeClass = String[].class),
    })
    @PostMapping("/pwdscore")
    public Map<String, Object> pwdScore(@RequestParam String pwd) {
        int score = PasswordStrengthChecker.checkPasswordStrength(pwd);
        var level = PasswordStrengthChecker.getPasswordLevel(pwd);
        boolean isLowLevel = PasswordUtils.isPasswordStrengthLowLevel(pwd);

        Map<String, Object> map = new HashMap();
        map.put("score", score);
        map.put("level", level);
        map.put("isLowLevel", isLowLevel);
        return map;
    }


    @GetMapping("/sysUUID")
    public String sysUUID() {
        String code = activationService.querySysUUID();
        return code;

    }

    @GetMapping("testErrorcode")
    public ServiceResult testErrorCode() {
        String errorMsg = "auth.error_code.account_not_existed";
        CustomAuthErrorCode customAuthErrorCode = new CustomAuthErrorCode();
        customAuthErrorCode.setCode(BaseErrorCode.PARAMS_INVALID.getCode(), errorMsg);
        return ServiceResult.<String>builder().serviceCode(customAuthErrorCode).build().asResult();
    }

    @GetMapping("/tmpmenu")
    @ApiIgnore
    public void testTmpMenu() {
//        tmpMenuService.process();
    }

    @GetMapping("/olrecord/schedule")
    public void scheduleUserOlRecord() {
        userOnlineReportService.scheduleUserOlRecord();
    }


    @GetMapping("/olrecord/check")
    public void checkUserOlRecord() {
        userOnlineReportService.checkUserOlRecord();
    }

    @GetMapping("/olrecord/period")
    public void period() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse("2021-10-26 10:14:00", df);
        LocalDateTime endTime = LocalDateTime.parse("2021-10-26 18:50:00", df);
        List<UserOnlineRecordModel> list = userOnlineRecordService.listOnlinedUserByPeriod(startTime, endTime);
        log.info("{}", list);
    }

}
