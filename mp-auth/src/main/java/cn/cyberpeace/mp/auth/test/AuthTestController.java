package cn.cyberpeace.mp.auth.test;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.repository.OrgMapper;
import cn.cyberpeace.mp.auth.repository.UserRoleMapper;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.RolePermissionModelService;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.auth.service.UserHeartbeatService;
import cn.cyberpeace.mp.auth.service.UserLogService;
import cn.cyberpeace.mp.auth.service.UserOnlineRecordService;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.PasswordStrengthChecker;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@ApiIgnore
@Api(value = "TEST", tags = "AUTH-TEST")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/test")
public class AuthTestController {

    @Autowired
    private UserLogService userLogService;

    @Autowired
    private TargetService targetService;

    @Autowired
    private OrgMapper orgMapper;

    @Autowired
    private UserHeartbeatService heartbeatService;

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
    private UserHeartbeatService userHeartbeatService;


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


    @ApiOperation("解压缩密文")
    @PostMapping("/decompress")
    public String test(@RequestBody String compressText) {
        byte[] raw = TextUtils.rawDecodeBase64(StringUtils.strip(compressText.trim(), "\""));
        String output = TextUtils.decompress(raw);
        return output;
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

    /*
    @GetMapping("test_exception_handler")
    public ServiceResult testErrorCode() {

        resolveMessage("abc", new String[]{"a"}, Locale.CHINA);
        throw new IllegalArgumentException("illegal from mp.auth");
    }

    @GetMapping("/olrecord/schedule")
    public void scheduleUserOlRecord() {
        userOnlineReportService.scheduleUserOlRecord();
    }


    @GetMapping("/olrecord/check")
    public void checkUserOlRecord() {
        Locale l1 = Locale.CHINESE;
        log.info(l1.toLanguageTag());
        Locale locale = Locale.forLanguageTag("CN");

        userOnlineReportService.checkUserOlRecord();
    }


    @GetMapping("/olrecord/period")
    public List<UserOnlineRecordModel> period(@RequestParam String startTime, @RequestParam String endTime) {
        log.info("{}", startTime);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime stime = LocalDateTime.parse(startTime, df);
        LocalDateTime etime = LocalDateTime.parse(endTime, df);
        List<UserOnlineRecordModel> list = userOnlineRecordService.listOnlinedUserByPeriod(stime, etime);
        log.info("{}", list);
        return list;
    }

    @GetMapping("/olrecord/track/period")
    public List<UserOnlineRecordModel> onlineTrack(@RequestParam String userId, @RequestParam String startTime, @RequestParam String endTime) {
        log.info("{}", startTime);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime stime = LocalDateTime.parse(startTime, df);
        LocalDateTime etime = LocalDateTime.parse(endTime, df);
        List<UserOnlineRecordModel> list = userOnlineRecordService.listOnlineHistoryTrackByUser(userId, stime, etime);
        log.info("{}", list);
        return list;
    }

    @GetMapping("/olrecord/current_onlined")
    public List<UserOnlineRecordModel> currentOnlined(@RequestParam(required = false) String userId) {
        return userOnlineRecordService.listCurrentOnlined(userId);
    }

    @GetMapping("/olrecord/heartmap")
    public ConcurrentHashMap<UserModel, CopyOnWriteArrayList<LocalDateTime>> heartmap() {
        return userHeartbeatService.getHeartbeatMap();
    }


    private String resolveMessage(String code, Object[] args, Locale locale) {
        String pattern = "{0} not empty"; // TODO Load message from database...
        MessageFormat messageFormat = new MessageFormat(pattern, locale);
        StringBuffer formattedMessage = new StringBuffer();
        messageFormat.format(args, formattedMessage, null);
        return formattedMessage.toString();
    }
    */


    public static void main(String[] args) {
        String compressText = "eJytVttunDAQ/ZVon4vluz35gbYvadXLQ5+QAWeDshjCJVfl3zve7VYkFQtpkBCykHVmOHNmzjxtLl3XF5fd5vzsadP1rh/icfPt58XF54uPmw9nm7KJH+JpaPqy8ml84c0qflecMWKk5kCBc6bVM97Lm3xXD0W6HVzuqnrn58CZ4URQQomYCMOsAkW4tVxTENoIE+M8DiG565q2vn+YjUAJQHzU5I+AUZowxiXXAJrHAK5wDWKWdfhPejBvwwjjihtDJRcQUasm9bN8/6VETkCDBKIFGGSdWispjJg/R06yedbnOMHsLRvD3vlsFdA9uXkdelcG3xbrFE/gDa60oKiQiN/6olxOM5+AtkZrVDiVkmow2rzW93zyxwhMwgl1ayIlp0YwqjHimPQy4L2Q+zQS1ta7VSpAxxG6IqSZ63x6265X3jv3OFwllQtu6+dhjyRNUyRxvhAhpBIWJ4B+octdvcUgqWua9TSf5YuT1qdkKYkBbbikB9i68bGe+XWSl6HwbdLlV74Ydgs4WtQFEnWGEwFjihcqum1mZ9gbWvconNVy/0t75drSFe8fM0gGtWgo2Fo8Hu0479xjzu/3jH8aqQyX9Xqo0d5cU66iC4M65Vwohcowr3t/rb55oY3Q5GmoixVd6NFlWXmfoAklYVuG+6R66G7mxyF2KTMnjPSPeVAO6P2Us/0K08ZYfXWzeAaYqezRoRlRUqEagVIGe5K+/ko+ffn+Y7l3TNnTPnlF0DWw7QHrHNGLtlqMPDNMQAkuBCgjDpvLYlw7ubZgJSxYg8RYo8COKtv59hYn4tvKepIZQazWXCkAO56GadRQ3z8k+a70oV8ca2o93cdCLoFRxrHIx+7N66oZ+lVaABcEGrdfND/L2aHMdX69km1IQ3DlVRYVBCP3vi4z9O/lKqXT7o0lJ/gbTElg8lCMPUOhTArnq/kNe/GYw12bGxDYCs+/AWlEqso=";
        byte[] raw = TextUtils.rawDecodeBase64(StringUtils.strip(compressText.trim(), "\""));
        String output = TextUtils.decompress(raw);
        log.info(output);
    }

}
