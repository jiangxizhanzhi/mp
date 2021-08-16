package cn.cyberpeace.mp.auth.test;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.dto.user.UserOlReportCntDto;
import cn.cyberpeace.mp.auth.repository.OrgMapper;
import cn.cyberpeace.mp.auth.repository.SysConfigMapper;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.UserHeartbeatService;
import cn.cyberpeace.mp.auth.service.UserLogService;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.auth.util.PasswordStrengthChecker;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.hutool.system.oshi.OshiUtil;
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
import oshi.hardware.ComputerSystem;
import oshi.hardware.HWDiskStore;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private OrgService orgService;

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


    @GetMapping("/tmpmenu")
//    @ApiIgnore
    public void testTmpMenu() {
//        tmpMenuService.process();
    }

    @GetMapping("/testmapper")
    public void testmapper() {
//        orgMapper.updatePath("c2e06974488b86f1b126740b242", "123");
//        int res = sysConfigMapper.updateByKeyName("flag_length", "8");
//        log.info("{}", res);

        List<UserOlReportCntDto> list = heartbeatService.groupCntByUserId(LocalDateTime.now().minusMinutes(100L));
        log.info("{}", list);
    }

    @GetMapping("/testlicense")
    public void testlicense() {
//        licenseJdkDemo.testlicense();
    }

    @GetMapping("/testos")
    public List<OrgDto> testos(@RequestParam String org) {
//        licenseJdkDemo.testlicense();
//        List<OrgDto> orgDtos = orgService.listAllParentIgnoreLevelExcludeSelf(org);
        ComputerSystem computerSystem = OshiUtil.getSystem();
        List<HWDiskStore> diskStores = OshiUtil.getDiskStores();

        log.warn("system:{}", computerSystem);
        log.warn("disk:{}", diskStores);
        return null;
    }
}
