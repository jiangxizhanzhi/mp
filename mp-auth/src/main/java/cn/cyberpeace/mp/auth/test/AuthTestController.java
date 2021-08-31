package cn.cyberpeace.mp.auth.test;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.dto.activation.ActivationRequest;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserPermissionModel;
import cn.cyberpeace.mp.auth.repository.OrgMapper;
import cn.cyberpeace.mp.auth.repository.SysConfigMapper;
import cn.cyberpeace.mp.auth.repository.UserRoleMapper;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.auth.service.UserHeartbeatService;
import cn.cyberpeace.mp.auth.service.UserLogService;
import cn.cyberpeace.mp.auth.service.sys.LicenseService;
import cn.cyberpeace.mp.auth.service.sys.SysActivationService;
import cn.cyberpeace.mp.auth.util.PasswordStrengthChecker;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
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
    private OrgService orgService;

    @Autowired
    private SysActivationService activationService;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private UserPermissionTestMapper userPermissionTestMapper;

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
    public void testmapper(HumanCondition condition) {

    }


    @GetMapping("/testpermlist")
    public List<TargetModel> testPermList() {
        String userId = "3fsss655d87112f4ae094bbaf7083";

        // 自定义条件
        HumanCondition outerCondition = new HumanCondition();
        outerCondition.filterContains("name", "题");
        outerCondition.filterEqual("parent", "item-bank-folder");
        outerCondition.filterEqual("tenant", "default");
        outerCondition.pagination(1, 10);
        outerCondition.orderBy(TargetModel::getId);
        outerCondition.groupBy(TargetModel::getParent);
        // 自定义返回field
        outerCondition.select(TargetModel::getId, TargetModel::getName, TargetModel::getPath, TargetModel::getIdentifier, TargetModel::getParent);
        outerCondition.setDistinct(true);

        List<TargetModel> list = userPermissionTestMapper.listTargetsByOwner(userId, outerCondition);
        int total = userPermissionTestMapper.totalTargetsByOwner(userId, outerCondition);
        log.info("{}, {}", list, total);


        UserPermissionModel upmodel = new UserPermissionModel();
        upmodel.setTarget("123abcde");
        upmodel.setOwner("123abcd4567");
        boolean result = userPermissionTestMapper.create(upmodel);
        log.info("{}", result);

        return list;
    }

    @GetMapping("/testlicense")
    public void testlicense() {
//        licenseJdkDemo.testlicense();

//        str.p = "{\"machinecode\": \"CHTF-12H1-34S8-HV23-123S\", \"deadline\": 1, \"info\": {\"thj\":2}}";
        ActivationRequest activationRequest = new ActivationRequest();
        activationRequest.setDeadline(1);
        activationRequest.setMachinecode("CHTF-12H1-34S8-HV23-123S");

        activationService.activate(activationRequest);
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
