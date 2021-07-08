package cn.cyberpeace.mp.auth.test;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.user.UserLogPageRequest;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.service.UserLogService;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.auth.util.PasswordStrengthChecker;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.log.model.OperationLogModel;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "TEST", tags = "AUTH-TEST")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/test")
public class AuthTestController {

    @Autowired
    private TmpMenuService tmpMenuService;

    @Autowired
    private UserLogService userLogService;

    @Autowired
    private TargetService targetService;

    @PostMapping("/desEncryptPwd")
    public String desEncryptPwd(@RequestParam String pwd) {
        return PasswordUtils.desEncrypt(pwd);
    }

    @PostMapping("/desDecryptPwd")
    public String desDecryptPwd(@RequestParam String pwd) {
        return PasswordUtils.desDecrypt(pwd);
    }


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
    @ApiIgnore
    public void testTmpMenu() {
//        tmpMenuService.process();
    }


    @GetMapping("/userlog")
    public ListObjServiceResult userlog(UserLogPageRequest logPageRequest) {
        PageInfo pageInfo = userLogService.getUserLog(logPageRequest);
        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }


    @GetMapping("/targets")
    public void targets(Integer[] types) {
        log.info("types:{}", types);
        List<TargetModel> logs = targetService.listByMultiTypes(AuthConstant.DEFAULT_TENANT, types);
        log.info("logs:{}", logs);
    }
}
