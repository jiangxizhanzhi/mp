package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.util.PasswordStrengthChecker;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.auth.util.TmpMenuService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(value = "TEST", tags = "AUTH-TEST")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/test")
public class AuthTestController {

    @Autowired
    private TmpMenuService tmpMenuService;

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


    @GetMapping("test/tmpmenu")
    public void test(@RequestParam String json) {
        /*
        StringBuilder frontendSb = new StringBuilder();
        frontendSb.append("{\n" +
                "    \"name\": \"前台\",\n" +
                "    \"value\": \"front_all\",\n" +
                "    \"authority\": \"front_all\",\n" +
                "    \"path_name\": \"前台/*\",\n" +
                "    \"status\": 1,\n" +
                "    \"read_status\": 1,\n" +
                "    \"write_status\": 1,\n" +
                "    \"execute_status\": 1,\n" +
                "    \"per_status\": \"R/W/X\",\n" +
                "    \"children\": [\n" +
                "      {\n" +
                "        \"name\": \"课程训练\",\n" +
                "        \"value\": \"course-folder\",\n" +
                "        \"authority\": \"course-folder\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"name\": \"课程训练\",\n" +
                "            \"value\": \"oj-course\",\n" +
                "            \"authority\": \"oj-course\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/课程训练/课程训练\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"课程详情\",\n" +
                "            \"value\": \"oj-course-detail\",\n" +
                "            \"authority\": \"oj-course-detail\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/课程训练/课程详情\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"课后习题\",\n" +
                "            \"value\": \"oj-course-exercises\",\n" +
                "            \"authority\": \"oj-course-exercises\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/课程训练/课后习题\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"章节详情\",\n" +
                "            \"value\": \"oj-course-lesson\",\n" +
                "            \"authority\": \"oj-course-lesson\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/课程训练/章节详情\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"path_name\": \"前台/课程训练\",\n" +
                "        \"status\": 1,\n" +
                "        \"read_status\": 1,\n" +
                "        \"write_status\": 1,\n" +
                "        \"execute_status\": 1,\n" +
                "        \"per_status\": \"R/W/X\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"攻防实训\",\n" +
                "        \"value\": \"practice-folder\",\n" +
                "        \"authority\": \"practice-folder\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"name\": \"理论题\",\n" +
                "            \"value\": \"oj-practice\",\n" +
                "            \"authority\": \"oj-practice\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/攻防实训/理论题\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"实操题\",\n" +
                "            \"value\": \"oj-practice-capture-flag\",\n" +
                "            \"authority\": \"oj-practice-capture-flag\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/攻防实训/实操题\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"理论基础\",\n" +
                "            \"value\": \"rationale\",\n" +
                "            \"authority\": \"rationale\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/攻防实训/理论基础\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"实践操作\",\n" +
                "            \"value\": \"capture-flag\",\n" +
                "            \"authority\": \"capture-flag\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/攻防实训/实践操作\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"收藏习题详情\",\n" +
                "            \"value\": \"collection-exercise--details\",\n" +
                "            \"authority\": \"collection-exercise--details\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/攻防实训/收藏习题详情\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"习题详情\",\n" +
                "            \"value\": \"exercise-details\",\n" +
                "            \"authority\": \"exercise-details\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/攻防实训/习题详情\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"习题详情\",\n" +
                "            \"value\": \"exercise-capture-detail\",\n" +
                "            \"authority\": \"exercise-capture-detail\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/攻防实训/习题详情\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"path_name\": \"前台/攻防实训\",\n" +
                "        \"status\": 1,\n" +
                "        \"read_status\": 1,\n" +
                "        \"write_status\": 1,\n" +
                "        \"execute_status\": 1,\n" +
                "        \"per_status\": \"R/W/X\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"竞技演练\",\n" +
                "        \"value\": \"competition-folder\",\n" +
                "        \"authority\": \"competition-folder\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"name\": \"演练首页\",\n" +
                "            \"value\": \"competition\",\n" +
                "            \"authority\": \"competition\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/竞技演练/演练首页\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"演练详情\",\n" +
                "            \"value\": \"competition-details\",\n" +
                "            \"authority\": \"competition-details\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/竞技演练/演练详情\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"path_name\": \"前台/竞技演练\",\n" +
                "        \"status\": 1,\n" +
                "        \"read_status\": 1,\n" +
                "        \"write_status\": 1,\n" +
                "        \"execute_status\": 1,\n" +
                "        \"per_status\": \"R/W/X\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"靶场测试\",\n" +
                "        \"value\": \"software-test\",\n" +
                "        \"authority\": \"software-test\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"name\": \"新增\",\n" +
                "            \"value\": \"software-add\",\n" +
                "            \"authority\": \"software-add\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/靶场测试/新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑\",\n" +
                "            \"value\": \"software-edit\",\n" +
                "            \"authority\": \"software-edit\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/靶场测试/编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"详情\",\n" +
                "            \"value\": \"software-detail\",\n" +
                "            \"authority\": \"software-detail\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/靶场测试/详情\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"测试记录\",\n" +
                "            \"value\": \"test-record\",\n" +
                "            \"authority\": \"test-record\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/靶场测试/测试记录\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑测试环境\",\n" +
                "            \"value\": \"software-detail-edit\",\n" +
                "            \"authority\": \"software-detail-edit\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/靶场测试/编辑测试环境\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增测试环境\",\n" +
                "            \"value\": \"software-detail-add\",\n" +
                "            \"authority\": \"software-detail-add\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/靶场测试/新增测试环境\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"测试环境\",\n" +
                "            \"value\": \"test-environment\",\n" +
                "            \"authority\": \"test-environment\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/靶场测试/测试环境\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"path_name\": \"前台/靶场测试\",\n" +
                "        \"status\": 1,\n" +
                "        \"read_status\": 1,\n" +
                "        \"write_status\": 1,\n" +
                "        \"execute_status\": 1,\n" +
                "        \"per_status\": \"R/W/X\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"红蓝对抗\",\n" +
                "        \"value\": \"red-vs-blue\",\n" +
                "        \"authority\": \"red-vs-blue\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"name\": \"详情\",\n" +
                "            \"value\": \"scenario-content\",\n" +
                "            \"authority\": \"scenario-content\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/红蓝对抗/详情\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"详情\",\n" +
                "            \"value\": \"referee-home\",\n" +
                "            \"authority\": \"referee-home\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/红蓝对抗/详情\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"详情\",\n" +
                "            \"value\": \"access-center-member\",\n" +
                "            \"authority\": \"access-center-member\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/红蓝对抗/详情\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"详情\",\n" +
                "            \"value\": \"director-home\",\n" +
                "            \"authority\": \"director-home\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/红蓝对抗/详情\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"path_name\": \"前台/红蓝对抗\",\n" +
                "        \"status\": 1,\n" +
                "        \"read_status\": 1,\n" +
                "        \"write_status\": 1,\n" +
                "        \"execute_status\": 1,\n" +
                "        \"per_status\": \"R/W/X\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"资源中心\",\n" +
                "        \"value\": \"recourse-center-folder\",\n" +
                "        \"authority\": \"recourse-center-folder\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"name\": \"工具库\",\n" +
                "            \"value\": \"tool-magazine\",\n" +
                "            \"authority\": \"tool-magazine\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/资源中心/工具库\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"漏洞库\",\n" +
                "            \"value\": \"vulnerability-library\",\n" +
                "            \"authority\": \"vulnerability-library\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/资源中心/漏洞库\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"EXP库\",\n" +
                "            \"value\": \"tool-exp\",\n" +
                "            \"authority\": \"tool-exp\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/资源中心/EXP库\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"ShellCode库\",\n" +
                "            \"value\": \"tool-shell\",\n" +
                "            \"authority\": \"tool-shell\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/资源中心/ShellCode库\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"GHDB库\",\n" +
                "            \"value\": \"ghdb-library\",\n" +
                "            \"authority\": \"ghdb-library\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/资源中心/GHDB库\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"工具库详情\",\n" +
                "            \"value\": \"tool-magazine-details\",\n" +
                "            \"authority\": \"tool-magazine-details\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/资源中心/工具库详情\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"漏洞库详情\",\n" +
                "            \"value\": \"vulnerability-library-detail\",\n" +
                "            \"authority\": \"vulnerability-library-detail\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/资源中心/漏洞库详情\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"EXP库详情\",\n" +
                "            \"value\": \"tool-exp-details\",\n" +
                "            \"authority\": \"tool-exp-details\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/资源中心/EXP库详情\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"ShellCode库详情\",\n" +
                "            \"value\": \"tool-shell-details\",\n" +
                "            \"authority\": \"tool-shell-details\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/资源中心/ShellCode库详情\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"GHDB库详情\",\n" +
                "            \"value\": \"ghdb-library-detail\",\n" +
                "            \"authority\": \"ghdb-library-detail\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/资源中心/GHDB库详情\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"path_name\": \"前台/资源中心\",\n" +
                "        \"status\": 1,\n" +
                "        \"read_status\": 1,\n" +
                "        \"write_status\": 1,\n" +
                "        \"execute_status\": 1,\n" +
                "        \"per_status\": \"R/W/X\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"个人中心\",\n" +
                "        \"value\": \"personal-center\",\n" +
                "        \"authority\": \"personal-center\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"name\": \"修改资料\",\n" +
                "            \"value\": \"personal-edit\",\n" +
                "            \"authority\": \"personal-edit\",\n" +
                "            \"children\": [],\n" +
                "            \"path_name\": \"前台/个人中心/修改资料\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"path_name\": \"前台/个人中心\",\n" +
                "        \"status\": 1,\n" +
                "        \"read_status\": 1,\n" +
                "        \"write_status\": 1,\n" +
                "        \"execute_status\": 1,\n" +
                "        \"per_status\": \"R/W/X\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }");


         */


        /*
        StringBuilder backendSb = new StringBuilder();
        backendSb.append("{\n" +
                "    \"name\": \"后台\",\n" +
                "    \"value\": \"back_all\",\n" +
                "    \"authority\": \"back_all\",\n" +
                "    \"path_name\": \"后台/*\",\n" +
                "    \"status\": 1,\n" +
                "    \"read_status\": 1,\n" +
                "    \"write_status\": 1,\n" +
                "    \"execute_status\": 1,\n" +
                "    \"per_status\": \"R/W/X\",\n" +
                "    \"children\": [\n" +
                "      {\n" +
                "        \"name\": \"仪表盘\",\n" +
                "        \"value\": \"index\",\n" +
                "        \"authority\": \"index\",\n" +
                "        \"path_name\": \"后台/仪表盘\",\n" +
                "        \"status\": 1,\n" +
                "        \"read_status\": 1,\n" +
                "        \"write_status\": 1,\n" +
                "        \"execute_status\": 1,\n" +
                "        \"per_status\": \"R/W/X\",\n" +
                "        \"children\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"课程管理\",\n" +
                "        \"value\": \"courses-manage-folder\",\n" +
                "        \"authority\": \"courses-manage-folder\",\n" +
                "        \"path_name\": \"后台/课程管理\",\n" +
                "        \"status\": 1,\n" +
                "        \"read_status\": 1,\n" +
                "        \"write_status\": 1,\n" +
                "        \"execute_status\": 1,\n" +
                "        \"per_status\": \"R/W/X\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"name\": \"课程管理\",\n" +
                "            \"value\": \"courses\",\n" +
                "            \"authority\": \"courses\",\n" +
                "            \"path_name\": \"后台/课程管理/课程管理\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"课程表\",\n" +
                "            \"value\": \"class-card\",\n" +
                "            \"authority\": \"class-card\",\n" +
                "            \"path_name\": \"后台/课程管理/课程表\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"学习统计\",\n" +
                "            \"value\": \"learn-statistics\",\n" +
                "            \"authority\": \"learn-statistics\",\n" +
                "            \"path_name\": \"后台/课程管理/学习统计\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"职业体系\",\n" +
                "            \"value\": \"career-system\",\n" +
                "            \"authority\": \"career-system\",\n" +
                "            \"path_name\": \"后台/课程管理/职业体系\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增\",\n" +
                "            \"value\": \"courses-add\",\n" +
                "            \"authority\": \"courses-add\",\n" +
                "            \"path_name\": \"后台/课程管理/新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑\",\n" +
                "            \"value\": \"courses-edit\",\n" +
                "            \"authority\": \"courses-edit\",\n" +
                "            \"path_name\": \"后台/课程管理/编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"职业体系新增\",\n" +
                "            \"value\": \"career-add\",\n" +
                "            \"authority\": \"career-add\",\n" +
                "            \"path_name\": \"后台/课程管理/职业体系新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"职业体系编辑\",\n" +
                "            \"value\": \"career-edit\",\n" +
                "            \"authority\": \"career-edit\",\n" +
                "            \"path_name\": \"后台/课程管理/职业体系编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"批量授权操作\",\n" +
                "            \"value\": \"course-batch-auth\",\n" +
                "            \"authority\": \"course-batch-auth\",\n" +
                "            \"path_name\": \"后台/课程管理/批量授权操作\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"批量授权访问\",\n" +
                "            \"value\": \"course-visiblity-batch\",\n" +
                "            \"authority\": \"course-visiblity-batch\",\n" +
                "            \"path_name\": \"后台/课程管理/批量授权访问\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"管理课程\",\n" +
                "            \"value\": \"courses-manage\",\n" +
                "            \"authority\": \"courses-manage\",\n" +
                "            \"path_name\": \"后台/课程管理/管理课程\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"学习统计\",\n" +
                "            \"value\": \"theory-learn-count\",\n" +
                "            \"authority\": \"theory-learn-count\",\n" +
                "            \"path_name\": \"后台/课程管理/学习统计\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"授权所有\",\n" +
                "            \"value\": \"courses-visiblity\",\n" +
                "            \"authority\": \"courses-visiblity\",\n" +
                "            \"path_name\": \"后台/课程管理/授权所有\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"共享所有\",\n" +
                "            \"value\": \"courses-auth\",\n" +
                "            \"authority\": \"courses-auth\",\n" +
                "            \"path_name\": \"后台/课程管理/共享所有\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"方向管理\",\n" +
                "            \"value\": \"courses-aspect\",\n" +
                "            \"authority\": \"courses-aspect\",\n" +
                "            \"path_name\": \"后台/课程管理/方向管理\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"课时管理\",\n" +
                "            \"value\": \"lessons-manage\",\n" +
                "            \"authority\": \"lessons-manage\",\n" +
                "            \"path_name\": \"后台/课程管理/课时管理\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"进入课堂\",\n" +
                "            \"value\": \"classroom-manage\",\n" +
                "            \"authority\": \"classroom-manage\",\n" +
                "            \"path_name\": \"后台/课程管理/进入课堂\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增\",\n" +
                "            \"value\": \"courses-aspect-add\",\n" +
                "            \"authority\": \"courses-aspect-add\",\n" +
                "            \"path_name\": \"后台/课程管理/新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑\",\n" +
                "            \"value\": \"courses-aspect-edit\",\n" +
                "            \"authority\": \"courses-aspect-edit\",\n" +
                "            \"path_name\": \"后台/课程管理/编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"练习\",\n" +
                "            \"value\": \"lessons-manage-practice\",\n" +
                "            \"authority\": \"lessons-manage-practice\",\n" +
                "            \"path_name\": \"后台/课程管理/练习\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增\",\n" +
                "            \"value\": \"lessons-add\",\n" +
                "            \"authority\": \"lessons-add\",\n" +
                "            \"path_name\": \"后台/课程管理/新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑\",\n" +
                "            \"value\": \"lessons-edit\",\n" +
                "            \"authority\": \"lessons-edit\",\n" +
                "            \"path_name\": \"后台/课程管理/编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"实验报告\",\n" +
                "            \"value\": \"experiment-report\",\n" +
                "            \"authority\": \"experiment-report\",\n" +
                "            \"path_name\": \"后台/课程管理/实验报告\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"学习监控\",\n" +
                "            \"value\": \"learn-monitor\",\n" +
                "            \"authority\": \"learn-monitor\",\n" +
                "            \"path_name\": \"后台/课程管理/学习监控\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"练习题库\",\n" +
                "        \"value\": \"practices-folder\",\n" +
                "        \"authority\": \"practices-folder\",\n" +
                "        \"path_name\": \"后台/练习题库\",\n" +
                "        \"status\": 1,\n" +
                "        \"read_status\": 1,\n" +
                "        \"write_status\": 1,\n" +
                "        \"execute_status\": 1,\n" +
                "        \"per_status\": \"R/W/X\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"name\": \"习题集管理\",\n" +
                "            \"value\": \"exercises\",\n" +
                "            \"authority\": \"exercises\",\n" +
                "            \"path_name\": \"后台/练习题库/习题集管理\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"理论基础\",\n" +
                "            \"value\": \"theory-base\",\n" +
                "            \"authority\": \"theory-base\",\n" +
                "            \"path_name\": \"后台/练习题库/理论基础\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"实践操作\",\n" +
                "            \"value\": \"flags-solve\",\n" +
                "            \"authority\": \"flags-solve\",\n" +
                "            \"path_name\": \"后台/练习题库/实践操作\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增\",\n" +
                "            \"value\": \"add-exercises\",\n" +
                "            \"authority\": \"add-exercises\",\n" +
                "            \"path_name\": \"后台/练习题库/新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑\",\n" +
                "            \"value\": \"edit-exercises\",\n" +
                "            \"authority\": \"edit-exercises\",\n" +
                "            \"path_name\": \"后台/练习题库/编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"授权操作\",\n" +
                "            \"value\": \"exercises-auth\",\n" +
                "            \"authority\": \"exercises-auth\",\n" +
                "            \"path_name\": \"后台/练习题库/授权操作\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"授权操作\",\n" +
                "            \"value\": \"exercises-batch-auth\",\n" +
                "            \"authority\": \"exercises-batch-auth\",\n" +
                "            \"path_name\": \"后台/练习题库/授权操作\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"授权访问\",\n" +
                "            \"value\": \"exercises-visibility\",\n" +
                "            \"authority\": \"exercises-visibility\",\n" +
                "            \"path_name\": \"后台/练习题库/授权访问\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"授权访问\",\n" +
                "            \"value\": \"exercises-batch-visibility\",\n" +
                "            \"authority\": \"exercises-batch-visibility\",\n" +
                "            \"path_name\": \"后台/练习题库/授权访问\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增\",\n" +
                "            \"value\": \"add-flags\",\n" +
                "            \"authority\": \"add-flags\",\n" +
                "            \"path_name\": \"后台/练习题库/新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑\",\n" +
                "            \"value\": \"edit-flags\",\n" +
                "            \"authority\": \"edit-flags\",\n" +
                "            \"path_name\": \"后台/练习题库/编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"方向管理\",\n" +
                "            \"value\": \"flags-to-type\",\n" +
                "            \"authority\": \"flags-to-type\",\n" +
                "            \"path_name\": \"后台/练习题库/方向管理\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增\",\n" +
                "            \"value\": \"add-theory\",\n" +
                "            \"authority\": \"add-theory\",\n" +
                "            \"path_name\": \"后台/练习题库/新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑\",\n" +
                "            \"value\": \"edit-theory\",\n" +
                "            \"authority\": \"edit-theory\",\n" +
                "            \"path_name\": \"后台/练习题库/编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"方向管理\",\n" +
                "            \"value\": \"theory-to-type\",\n" +
                "            \"authority\": \"theory-to-type\",\n" +
                "            \"path_name\": \"后台/练习题库/方向管理\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增\",\n" +
                "            \"value\": \"theory-add-type\",\n" +
                "            \"authority\": \"theory-add-type\",\n" +
                "            \"path_name\": \"后台/练习题库/新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑\",\n" +
                "            \"value\": \"theory-edit-type\",\n" +
                "            \"authority\": \"theory-edit-type\",\n" +
                "            \"path_name\": \"后台/练习题库/编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增\",\n" +
                "            \"value\": \"flags-add-type\",\n" +
                "            \"authority\": \"flags-add-type\",\n" +
                "            \"path_name\": \"后台/练习题库/新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑\",\n" +
                "            \"value\": \"flags-edit-type\",\n" +
                "            \"authority\": \"flags-edit-type\",\n" +
                "            \"path_name\": \"后台/练习题库/编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"竞赛题库\",\n" +
                "        \"value\": \"item-bank-folder\",\n" +
                "        \"authority\": \"item-bank-folder\",\n" +
                "        \"path_name\": \"后台/竞赛题库\",\n" +
                "        \"status\": 1,\n" +
                "        \"read_status\": 1,\n" +
                "        \"write_status\": 1,\n" +
                "        \"execute_status\": 1,\n" +
                "        \"per_status\": \"R/W/X\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"name\": \"试卷库\",\n" +
                "            \"value\": \"ad-test-paper-list\",\n" +
                "            \"authority\": \"ad-test-paper-list\",\n" +
                "            \"path_name\": \"后台/竞赛题库/试卷库\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"理论赛题目\",\n" +
                "            \"value\": \"ad-theory-list\",\n" +
                "            \"authority\": \"ad-theory-list\",\n" +
                "            \"path_name\": \"后台/竞赛题库/理论赛题目\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"解题赛题目\",\n" +
                "            \"value\": \"ad-jeopardy-list\",\n" +
                "            \"authority\": \"ad-jeopardy-list\",\n" +
                "            \"path_name\": \"后台/竞赛题库/解题赛题目\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"攻防赛题目\",\n" +
                "            \"value\": \"ad-defense-list\",\n" +
                "            \"authority\": \"ad-defense-list\",\n" +
                "            \"path_name\": \"后台/竞赛题库/攻防赛题目\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"运维赛题目\",\n" +
                "            \"value\": \"ad-Dev-ops-list\",\n" +
                "            \"authority\": \"ad-Dev-ops-list\",\n" +
                "            \"path_name\": \"后台/竞赛题库/运维赛题目\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"RealWord题目\",\n" +
                "            \"value\": \"ad-rw-list\",\n" +
                "            \"authority\": \"ad-rw-list\",\n" +
                "            \"path_name\": \"后台/竞赛题库/RealWord题目\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"渗透赛题目\",\n" +
                "            \"value\": \"ad-penetration-list\",\n" +
                "            \"authority\": \"ad-penetration-list\",\n" +
                "            \"path_name\": \"后台/竞赛题库/渗透赛题目\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增试卷\",\n" +
                "            \"value\": \"ad-add-test-paper\",\n" +
                "            \"authority\": \"ad-add-test-paper\",\n" +
                "            \"path_name\": \"后台/竞赛题库/新增试卷\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑试卷\",\n" +
                "            \"value\": \"add-edit-test-paper\",\n" +
                "            \"authority\": \"add-edit-test-paper\",\n" +
                "            \"path_name\": \"后台/竞赛题库/编辑试卷\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"试卷库授权\",\n" +
                "            \"value\": \"ad-empower-test\",\n" +
                "            \"authority\": \"ad-empower-test\",\n" +
                "            \"path_name\": \"后台/竞赛题库/试卷库授权\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增题目\",\n" +
                "            \"value\": \"ad-add-theory\",\n" +
                "            \"authority\": \"ad-add-theory\",\n" +
                "            \"path_name\": \"后台/竞赛题库/新增题目\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑题目\",\n" +
                "            \"value\": \"ad-edit-theory\",\n" +
                "            \"authority\": \"ad-edit-theory\",\n" +
                "            \"path_name\": \"后台/竞赛题库/编辑题目\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增题目\",\n" +
                "            \"value\": \"ad-add-jeopardy\",\n" +
                "            \"authority\": \"ad-add-jeopardy\",\n" +
                "            \"path_name\": \"后台/竞赛题库/新增题目\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑题目\",\n" +
                "            \"value\": \"ad-edit-jeopardy\",\n" +
                "            \"authority\": \"ad-edit-jeopardy\",\n" +
                "            \"path_name\": \"后台/竞赛题库/编辑题目\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"解题赛题目授权\",\n" +
                "            \"value\": \"ad-empower-jeopardy\",\n" +
                "            \"authority\": \"ad-empower-jeopardy\",\n" +
                "            \"path_name\": \"后台/竞赛题库/解题赛题目授权\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增题目\",\n" +
                "            \"value\": \"ad-add-defense\",\n" +
                "            \"authority\": \"ad-add-defense\",\n" +
                "            \"path_name\": \"后台/竞赛题库/新增题目\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑题目\",\n" +
                "            \"value\": \"ad-edit-defense\",\n" +
                "            \"authority\": \"ad-edit-defense\",\n" +
                "            \"path_name\": \"后台/竞赛题库/编辑题目\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"攻防赛题目授权\",\n" +
                "            \"value\": \"ad-empower-defense\",\n" +
                "            \"authority\": \"ad-empower-defense\",\n" +
                "            \"path_name\": \"后台/竞赛题库/攻防赛题目授权\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增题目\",\n" +
                "            \"value\": \"ad-add-dev-ops\",\n" +
                "            \"authority\": \"ad-add-dev-ops\",\n" +
                "            \"path_name\": \"后台/竞赛题库/新增题目\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑题目\",\n" +
                "            \"value\": \"ad-edit-dev-ops\",\n" +
                "            \"authority\": \"ad-edit-dev-ops\",\n" +
                "            \"path_name\": \"后台/竞赛题库/编辑题目\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"运维赛题目授权\",\n" +
                "            \"value\": \"ad-empower-dev-ops\",\n" +
                "            \"authority\": \"ad-empower-dev-ops\",\n" +
                "            \"path_name\": \"后台/竞赛题库/运维赛题目授权\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增题目\",\n" +
                "            \"value\": \"ad-add-r-w\",\n" +
                "            \"authority\": \"ad-add-r-w\",\n" +
                "            \"path_name\": \"后台/竞赛题库/新增题目\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑题目\",\n" +
                "            \"value\": \"ad-edit-r-w\",\n" +
                "            \"authority\": \"ad-edit-r-w\",\n" +
                "            \"path_name\": \"后台/竞赛题库/编辑题目\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"RealWord题目授权\",\n" +
                "            \"value\": \"ad-empower-r-w\",\n" +
                "            \"authority\": \"ad-empower-r-w\",\n" +
                "            \"path_name\": \"后台/竞赛题库/RealWord题目授权\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增题目\",\n" +
                "            \"value\": \"ad-add-penetration\",\n" +
                "            \"authority\": \"ad-add-penetration\",\n" +
                "            \"path_name\": \"后台/竞赛题库/新增题目\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑题目\",\n" +
                "            \"value\": \"ad-edit-penetration\",\n" +
                "            \"authority\": \"ad-edit-penetration\",\n" +
                "            \"path_name\": \"后台/竞赛题库/编辑题目\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"渗透题目授权\",\n" +
                "            \"value\": \"ad-empower-penetration\",\n" +
                "            \"authority\": \"ad-empower-penetration\",\n" +
                "            \"path_name\": \"后台/竞赛题库/渗透题目授权\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"靶标管理\",\n" +
                "        \"value\": \"cr-standard-device-folder\",\n" +
                "        \"authority\": \"cr-standard-device-folder\",\n" +
                "        \"path_name\": \"后台/靶标管理\",\n" +
                "        \"status\": 1,\n" +
                "        \"read_status\": 1,\n" +
                "        \"write_status\": 1,\n" +
                "        \"execute_status\": 1,\n" +
                "        \"per_status\": \"R/W/X\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"name\": \"镜像管理\",\n" +
                "            \"value\": \"image-manage\",\n" +
                "            \"authority\": \"image-manage\",\n" +
                "            \"path_name\": \"后台/靶标管理/镜像管理\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"授权操作\",\n" +
                "            \"value\": \"cr-image-manage-auth-operation\",\n" +
                "            \"authority\": \"cr-image-manage-auth-operation\",\n" +
                "            \"path_name\": \"后台/靶标管理/镜像管理/授权操作\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"实装管理\",\n" +
                "            \"value\": \"real-manage\",\n" +
                "            \"authority\": \"real-manage\",\n" +
                "            \"path_name\": \"后台/靶标管理/实装管理\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"TGM管理\",\n" +
                "            \"value\": \"TGM-manage\",\n" +
                "            \"authority\": \"TGM-manage\",\n" +
                "            \"path_name\": \"后台/靶标管理/TGM管理\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"服务管理\",\n" +
                "            \"value\": \"service-manage\",\n" +
                "            \"authority\": \"service-manage\",\n" +
                "            \"path_name\": \"后台/靶标管理/服务管理\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"快速构建\",\n" +
                "            \"value\": \"image-manage-quick-create\",\n" +
                "            \"authority\": \"image-manage-quick-create\",\n" +
                "            \"path_name\": \"后台/靶标管理/快速构建\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增\",\n" +
                "            \"value\": \"image-manage-create\",\n" +
                "            \"authority\": \"image-manage-create\",\n" +
                "            \"path_name\": \"后台/靶标管理/新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"导入已存在镜像\",\n" +
                "            \"value\": \"image-manage-create-exist-image\",\n" +
                "            \"authority\": \"image-manage-create-exist-image\",\n" +
                "            \"path_name\": \"后台/靶标管理/导入已存在镜像\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑\",\n" +
                "            \"value\": \"image-manage-edit\",\n" +
                "            \"authority\": \"image-manage-edit\",\n" +
                "            \"path_name\": \"后台/靶标管理/编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增\",\n" +
                "            \"value\": \"real-create\",\n" +
                "            \"authority\": \"real-create\",\n" +
                "            \"path_name\": \"后台/靶标管理/新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑\",\n" +
                "            \"value\": \"real-edit\",\n" +
                "            \"authority\": \"real-edit\",\n" +
                "            \"path_name\": \"后台/靶标管理/编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增\",\n" +
                "            \"value\": \"TGM-manage-create\",\n" +
                "            \"authority\": \"TGM-manage-create\",\n" +
                "            \"path_name\": \"后台/靶标管理/新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑\",\n" +
                "            \"value\": \"TGM-manage-edit\",\n" +
                "            \"authority\": \"TGM-manage-edit\",\n" +
                "            \"path_name\": \"后台/靶标管理/编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增\",\n" +
                "            \"value\": \"service-manage-create\",\n" +
                "            \"authority\": \"service-manage-create\",\n" +
                "            \"path_name\": \"后台/靶标管理/新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑\",\n" +
                "            \"value\": \"service-manage-edit\",\n" +
                "            \"authority\": \"service-manage-edit\",\n" +
                "            \"path_name\": \"后台/靶标管理/编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"任务方案\",\n" +
                "        \"value\": \"mission-arrange-management\",\n" +
                "        \"authority\": \"mission-arrange-management\",\n" +
                "        \"path_name\": \"后台/任务方案\",\n" +
                "        \"status\": 1,\n" +
                "        \"read_status\": 1,\n" +
                "        \"write_status\": 1,\n" +
                "        \"execute_status\": 1,\n" +
                "        \"per_status\": \"R/W/X\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"name\": \"新增\",\n" +
                "            \"value\": \"combat-tech-create\",\n" +
                "            \"authority\": \"combat-tech-create\",\n" +
                "            \"path_name\": \"后台/任务方案/新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑\",\n" +
                "            \"value\": \"combat-tech-update\",\n" +
                "            \"authority\": \"combat-tech-update\",\n" +
                "            \"path_name\": \"后台/任务方案/编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"流量管理\",\n" +
                "        \"value\": \"traffic-manage-folder\",\n" +
                "        \"authority\": \"traffic-manage-folder\",\n" +
                "        \"path_name\": \"后台/流量管理\",\n" +
                "        \"status\": 1,\n" +
                "        \"read_status\": 1,\n" +
                "        \"write_status\": 1,\n" +
                "        \"execute_status\": 1,\n" +
                "        \"per_status\": \"R/W/X\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"name\": \"背景流量\",\n" +
                "            \"value\": \"static-traffic-list\",\n" +
                "            \"authority\": \"static-traffic-list\",\n" +
                "            \"path_name\": \"后台/流量管理/背景流量\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"攻击流量\",\n" +
                "            \"value\": \"dynamic-traffic-list\",\n" +
                "            \"authority\": \"dynamic-traffic-list\",\n" +
                "            \"path_name\": \"后台/流量管理/攻击流量\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"流量集合\",\n" +
                "            \"value\": \"traffic-scheme-list\",\n" +
                "            \"authority\": \"traffic-scheme-list\",\n" +
                "            \"path_name\": \"后台/流量管理/流量集合\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增\",\n" +
                "            \"value\": \"static-traffic-create\",\n" +
                "            \"authority\": \"static-traffic-create\",\n" +
                "            \"path_name\": \"后台/流量管理/新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑\",\n" +
                "            \"value\": \"static-traffic-update\",\n" +
                "            \"authority\": \"static-traffic-update\",\n" +
                "            \"path_name\": \"后台/流量管理/编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增\",\n" +
                "            \"value\": \"dynamic-traffic-create\",\n" +
                "            \"authority\": \"dynamic-traffic-create\",\n" +
                "            \"path_name\": \"后台/流量管理/新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑\",\n" +
                "            \"value\": \"dynamic-traffic-update\",\n" +
                "            \"authority\": \"dynamic-traffic-update\",\n" +
                "            \"path_name\": \"后台/流量管理/编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增\",\n" +
                "            \"value\": \"traffic-scheme-create\",\n" +
                "            \"authority\": \"traffic-scheme-create\",\n" +
                "            \"path_name\": \"后台/流量管理/新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑\",\n" +
                "            \"value\": \"traffic-scheme-edit\",\n" +
                "            \"authority\": \"traffic-scheme-edit\",\n" +
                "            \"path_name\": \"后台/流量管理/编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"流量管理\",\n" +
                "            \"value\": \"traffic-scheme-traffic-list\",\n" +
                "            \"authority\": \"traffic-scheme-traffic-list\",\n" +
                "            \"path_name\": \"后台/流量管理/流量管理\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"探针管理\",\n" +
                "        \"value\": \"collection-system\",\n" +
                "        \"authority\": \"collection-system\",\n" +
                "        \"path_name\": \"后台/探针管理\",\n" +
                "        \"status\": 1,\n" +
                "        \"read_status\": 1,\n" +
                "        \"write_status\": 1,\n" +
                "        \"execute_status\": 1,\n" +
                "        \"per_status\": \"R/W/X\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"name\": \"新增\",\n" +
                "            \"value\": \"probe-create\",\n" +
                "            \"authority\": \"probe-create\",\n" +
                "            \"path_name\": \"后台/探针管理/新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑\",\n" +
                "            \"value\": \"probe-edit\",\n" +
                "            \"authority\": \"probe-edit\",\n" +
                "            \"path_name\": \"后台/探针管理/编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"场景管理\",\n" +
                "        \"value\": \"scene-folder\",\n" +
                "        \"authority\": \"scene-folder\",\n" +
                "        \"path_name\": \"后台/场景管理\",\n" +
                "        \"status\": 1,\n" +
                "        \"read_status\": 1,\n" +
                "        \"write_status\": 1,\n" +
                "        \"execute_status\": 1,\n" +
                "        \"per_status\": \"R/W/X\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"name\": \"训练场景编排\",\n" +
                "            \"value\": \"trainingScene\",\n" +
                "            \"authority\": \"trainingScene\",\n" +
                "            \"path_name\": \"后台/场景管理/训练场景编排\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"渗透场景编排\",\n" +
                "            \"value\": \"ad-scene-list\",\n" +
                "            \"authority\": \"ad-scene-list\",\n" +
                "            \"path_name\": \"后台/场景管理/渗透场景编排\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"对抗拓扑模板\",\n" +
                "            \"value\": \"template\",\n" +
                "            \"authority\": \"template\",\n" +
                "            \"path_name\": \"后台/场景管理/对抗拓扑模板\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"对抗场景编排\",\n" +
                "            \"value\": \"cr-event-list\",\n" +
                "            \"authority\": \"cr-event-list\",\n" +
                "            \"path_name\": \"后台/场景管理/对抗场景编排\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增\",\n" +
                "            \"value\": \"senes-add\",\n" +
                "            \"authority\": \"senes-add\",\n" +
                "            \"path_name\": \"后台/场景管理/新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑\",\n" +
                "            \"value\": \"senes-edit\",\n" +
                "            \"authority\": \"senes-edit\",\n" +
                "            \"path_name\": \"后台/场景管理/编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增渗透场景\",\n" +
                "            \"value\": \"ad-create-scene\",\n" +
                "            \"authority\": \"ad-create-scene\",\n" +
                "            \"path_name\": \"后台/场景管理/新增渗透场景\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑渗透场景\",\n" +
                "            \"value\": \"ad-edit-scene\",\n" +
                "            \"authority\": \"ad-edit-scene\",\n" +
                "            \"path_name\": \"后台/场景管理/编辑渗透场景\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"渗透场景授权\",\n" +
                "            \"value\": \"ad-scene-empower\",\n" +
                "            \"authority\": \"ad-scene-empower\",\n" +
                "            \"path_name\": \"后台/场景管理/渗透场景授权\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增\",\n" +
                "            \"value\": \"topology-template-create\",\n" +
                "            \"authority\": \"topology-template-create\",\n" +
                "            \"path_name\": \"后台/场景管理/新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑\",\n" +
                "            \"value\": \"topology-template-edit\",\n" +
                "            \"authority\": \"topology-template-edit\",\n" +
                "            \"path_name\": \"后台/场景管理/编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"场景测试\",\n" +
                "            \"value\": \"scene-config-test\",\n" +
                "            \"authority\": \"scene-config-test\",\n" +
                "            \"path_name\": \"后台/场景管理/场景测试\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增红蓝对抗\",\n" +
                "            \"value\": \"cr-event-red-vs-blue-create\",\n" +
                "            \"authority\": \"cr-event-red-vs-blue-create\",\n" +
                "            \"path_name\": \"后台/场景管理/新增红蓝对抗\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑红蓝对抗\",\n" +
                "            \"value\": \"cr-event-red-vs-blue-edit\",\n" +
                "            \"authority\": \"cr-event-red-vs-blue-edit\",\n" +
                "            \"path_name\": \"后台/场景管理/编辑红蓝对抗\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"竞赛管理\",\n" +
                "        \"value\": \"competition-folder\",\n" +
                "        \"authority\": \"competition-folder\",\n" +
                "        \"path_name\": \"后台/竞赛管理\",\n" +
                "        \"status\": 1,\n" +
                "        \"read_status\": 1,\n" +
                "        \"write_status\": 1,\n" +
                "        \"execute_status\": 1,\n" +
                "        \"per_status\": \"R/W/X\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"name\": \"竞赛管理\",\n" +
                "            \"value\": \"ad-match-list\",\n" +
                "            \"authority\": \"ad-match-list\",\n" +
                "            \"path_name\": \"后台/竞赛管理/竞赛管理\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增竞赛\",\n" +
                "            \"value\": \"ad-create-new-match\",\n" +
                "            \"authority\": \"ad-create-new-match\",\n" +
                "            \"path_name\": \"后台/竞赛管理/新增竞赛\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑竞赛\",\n" +
                "            \"value\": \"ad-edit-new-match\",\n" +
                "            \"authority\": \"ad-edit-new-match\",\n" +
                "            \"path_name\": \"后台/竞赛管理/编辑竞赛\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"数据分析\",\n" +
                "            \"value\": \"ad-new-match-data\",\n" +
                "            \"authority\": \"ad-new-match-data\",\n" +
                "            \"path_name\": \"后台/竞赛管理/数据分析\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"竞赛授权\",\n" +
                "            \"value\": \"ad-empower-new-match\",\n" +
                "            \"authority\": \"ad-empower-new-match\",\n" +
                "            \"path_name\": \"后台/竞赛管理/竞赛授权\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"赛场管理\",\n" +
                "        \"value\": \"competition-folder\",\n" +
                "        \"authority\": \"competition-folder\",\n" +
                "        \"path_name\": \"后台/赛场管理\",\n" +
                "        \"status\": 1,\n" +
                "        \"read_status\": 1,\n" +
                "        \"write_status\": 1,\n" +
                "        \"execute_status\": 1,\n" +
                "        \"per_status\": \"R/W/X\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"name\": \"理论赛赛场\",\n" +
                "            \"value\": \"ad-theory-list-match\",\n" +
                "            \"authority\": \"ad-theory-list-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/理论赛赛场\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"解题赛赛场\",\n" +
                "            \"value\": \"ad-jeopardy-list-match\",\n" +
                "            \"authority\": \"ad-jeopardy-list-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/解题赛赛场\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"闯关赛赛场\",\n" +
                "            \"value\": \"ad-layered-list-match\",\n" +
                "            \"authority\": \"ad-layered-list-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/闯关赛赛场\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"攻防赛赛场\",\n" +
                "            \"value\": \"ad-defense-list-match\",\n" +
                "            \"authority\": \"ad-defense-list-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/攻防赛赛场\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"运维赛赛场\",\n" +
                "            \"value\": \"ad-Dev-ops-list-match\",\n" +
                "            \"authority\": \"ad-Dev-ops-list-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/运维赛赛场\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"渗透赛赛场\",\n" +
                "            \"value\": \"ad-penetration-list-match\",\n" +
                "            \"authority\": \"ad-penetration-list-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/渗透赛赛场\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"RealWorld赛场\",\n" +
                "            \"value\": \"ad-rw-list-match\",\n" +
                "            \"authority\": \"ad-rw-list-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/RealWorld赛场\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增赛场\",\n" +
                "            \"value\": \"ad-create-theory-match\",\n" +
                "            \"authority\": \"ad-create-theory-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/新增赛场\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑赛场\",\n" +
                "            \"value\": \"ad-edit-theory-match\",\n" +
                "            \"authority\": \"ad-edit-theory-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/编辑赛场\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"导入题目\",\n" +
                "            \"value\": \"ad-import-topic\",\n" +
                "            \"authority\": \"ad-import-topic\",\n" +
                "            \"path_name\": \"后台/赛场管理/导入题目\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"理论赛赛场管理\",\n" +
                "            \"value\": \"ad-score-manage\",\n" +
                "            \"authority\": \"ad-score-manage\",\n" +
                "            \"path_name\": \"后台/赛场管理/理论赛赛场管理\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"检查成绩\",\n" +
                "            \"value\": \"ad-inspect\",\n" +
                "            \"authority\": \"ad-inspect\",\n" +
                "            \"path_name\": \"后台/赛场管理/检查成绩\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"阅卷\",\n" +
                "            \"value\": \"ad-scoring\",\n" +
                "            \"authority\": \"ad-scoring\",\n" +
                "            \"path_name\": \"后台/赛场管理/阅卷\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"快速阅卷\",\n" +
                "            \"value\": \"ad-fastScoring\",\n" +
                "            \"authority\": \"ad-fastScoring\",\n" +
                "            \"path_name\": \"后台/赛场管理/快速阅卷\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增赛场\",\n" +
                "            \"value\": \"ad-create-jeopardy-match\",\n" +
                "            \"authority\": \"ad-create-jeopardy-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/新增赛场\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑赛场\",\n" +
                "            \"value\": \"ad-edit-jeopardy-match-view\",\n" +
                "            \"authority\": \"ad-edit-jeopardy-match-view\",\n" +
                "            \"path_name\": \"后台/赛场管理/编辑赛场\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑赛题\",\n" +
                "            \"value\": \"ad-edit-jeopardy-match-topic\",\n" +
                "            \"authority\": \"ad-edit-jeopardy-match-topic\",\n" +
                "            \"path_name\": \"后台/赛场管理/编辑赛题\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"增加网络\",\n" +
                "            \"value\": \"ad-add-jeopardy-inetnet\",\n" +
                "            \"authority\": \"ad-add-jeopardy-inetnet\",\n" +
                "            \"path_name\": \"后台/赛场管理/增加网络\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"授权管理\",\n" +
                "            \"value\": \"ad-accredit-jeopardy-match\",\n" +
                "            \"authority\": \"ad-accredit-jeopardy-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/授权管理\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"竞赛运维\",\n" +
                "            \"value\": \"ad-operation-jeopardy-home\",\n" +
                "            \"authority\": \"ad-operation-jeopardy-home\",\n" +
                "            \"path_name\": \"后台/赛场管理/竞赛运维\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"赛题编辑\",\n" +
                "            \"value\": \"ad-edit-jeopardy-question\",\n" +
                "            \"authority\": \"ad-edit-jeopardy-question\",\n" +
                "            \"path_name\": \"后台/赛场管理/赛题编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新建赛题\",\n" +
                "            \"value\": \"ad-add-jeopardy-question\",\n" +
                "            \"authority\": \"ad-add-jeopardy-question\",\n" +
                "            \"path_name\": \"后台/赛场管理/新建赛题\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增赛场\",\n" +
                "            \"value\": \"ad-create-jeopardy-match\",\n" +
                "            \"authority\": \"ad-create-jeopardy-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/新增赛场\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑赛场\",\n" +
                "            \"value\": \"ad-edit-defense-match\",\n" +
                "            \"authority\": \"ad-edit-defense-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/编辑赛场\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑赛题\",\n" +
                "            \"value\": \"ad-edit-defense-match-topic\",\n" +
                "            \"authority\": \"ad-edit-defense-match-topic\",\n" +
                "            \"path_name\": \"后台/赛场管理/编辑赛题\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"增加网络\",\n" +
                "            \"value\": \"ad-add-defense-inetnet\",\n" +
                "            \"authority\": \"ad-add-defense-inetnet\",\n" +
                "            \"path_name\": \"后台/赛场管理/增加网络\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑网络\",\n" +
                "            \"value\": \"ad-edit-defense-inetnet\",\n" +
                "            \"authority\": \"ad-edit-defense-inetnet\",\n" +
                "            \"path_name\": \"后台/赛场管理/编辑网络\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"竞赛运维\",\n" +
                "            \"value\": \"ad-operation-defense-match\",\n" +
                "            \"authority\": \"ad-operation-defense-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/竞赛运维\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增赛场\",\n" +
                "            \"value\": \"ad-create-dev-ops-match\",\n" +
                "            \"authority\": \"ad-create-dev-ops-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/新增赛场\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑赛场\",\n" +
                "            \"value\": \"ad-edit-dev-ops-match\",\n" +
                "            \"authority\": \"ad-edit-dev-ops-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/编辑赛场\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑赛题\",\n" +
                "            \"value\": \"ad-edit-dev-ops-match-topic\",\n" +
                "            \"authority\": \"ad-edit-dev-ops-match-topic\",\n" +
                "            \"path_name\": \"后台/赛场管理/编辑赛题\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"增加网络\",\n" +
                "            \"value\": \"ad-add-dev-ops-inetnet\",\n" +
                "            \"authority\": \"ad-add-dev-ops-inetnet\",\n" +
                "            \"path_name\": \"后台/赛场管理/增加网络\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑网络\",\n" +
                "            \"value\": \"ad-edit-dev-ops-inetnet\",\n" +
                "            \"authority\": \"ad-edit-dev-ops-inetnet\",\n" +
                "            \"path_name\": \"后台/赛场管理/编辑网络\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"竞赛运维\",\n" +
                "            \"value\": \"ad-operation-dev-ops-match\",\n" +
                "            \"authority\": \"ad-operation-dev-ops-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/竞赛运维\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新建赛场\",\n" +
                "            \"value\": \"ad-create-layered-match\",\n" +
                "            \"authority\": \"ad-create-layered-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/新建赛场\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑赛场\",\n" +
                "            \"value\": \"ad-edit-layered-match\",\n" +
                "            \"authority\": \"ad-edit-layered-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/编辑赛场\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑赛题\",\n" +
                "            \"value\": \"ad-edit-layered-match-topic\",\n" +
                "            \"authority\": \"ad-edit-layered-match-topic\",\n" +
                "            \"path_name\": \"后台/赛场管理/编辑赛题\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"增加网络\",\n" +
                "            \"value\": \"ad-add-layered-inetnet\",\n" +
                "            \"authority\": \"ad-add-layered-inetnet\",\n" +
                "            \"path_name\": \"后台/赛场管理/增加网络\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑网络\",\n" +
                "            \"value\": \"ad-edit-layered-inetnet\",\n" +
                "            \"authority\": \"ad-edit-layered-inetnet\",\n" +
                "            \"path_name\": \"后台/赛场管理/编辑网络\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"竞赛运维\",\n" +
                "            \"value\": \"ad-operation-layered-match\",\n" +
                "            \"authority\": \"ad-operation-layered-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/竞赛运维\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增赛场\",\n" +
                "            \"value\": \"ad-create-rw-match\",\n" +
                "            \"authority\": \"ad-create-rw-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/新增赛场\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑赛场\",\n" +
                "            \"value\": \"ad-edit-rw-match\",\n" +
                "            \"authority\": \"ad-edit-rw-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/编辑赛场\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"竞赛运维\",\n" +
                "            \"value\": \"ad-operation-rw-home\",\n" +
                "            \"authority\": \"ad-operation-rw-home\",\n" +
                "            \"path_name\": \"后台/赛场管理/竞赛运维\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"赛题编辑\",\n" +
                "            \"value\": \"ad-edit-rw-question\",\n" +
                "            \"authority\": \"ad-edit-rw-question\",\n" +
                "            \"path_name\": \"后台/赛场管理/赛题编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"战队申请记录\",\n" +
                "            \"value\": \"ad-apply-record-team\",\n" +
                "            \"authority\": \"ad-apply-record-team\",\n" +
                "            \"path_name\": \"后台/赛场管理/战队申请记录\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"裁判审核记录\",\n" +
                "            \"value\": \"ad-apply-record-audit\",\n" +
                "            \"authority\": \"ad-apply-record-audit\",\n" +
                "            \"path_name\": \"后台/赛场管理/裁判审核记录\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增赛场\",\n" +
                "            \"value\": \"ad-create-penetration-match\",\n" +
                "            \"authority\": \"ad-create-penetration-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/新增赛场\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑赛场\",\n" +
                "            \"value\": \"ad-edit-penetration-match\",\n" +
                "            \"authority\": \"ad-edit-penetration-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/编辑赛场\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"增加网络\",\n" +
                "            \"value\": \"ad-add-penetration-inetnet\",\n" +
                "            \"authority\": \"ad-add-penetration-inetnet\",\n" +
                "            \"path_name\": \"后台/赛场管理/增加网络\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑网络\",\n" +
                "            \"value\": \"ad-edit-penetration-inetnet\",\n" +
                "            \"authority\": \"ad-edit-penetration-inetnet\",\n" +
                "            \"path_name\": \"后台/赛场管理/编辑网络\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"授权管理\",\n" +
                "            \"value\": \"ad-accredit-penetration-match\",\n" +
                "            \"authority\": \"ad-accredit-penetration-match\",\n" +
                "            \"path_name\": \"后台/赛场管理/授权管理\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"竞赛运维\",\n" +
                "            \"value\": \"ad-operation-penetration-home\",\n" +
                "            \"authority\": \"ad-operation-penetration-home\",\n" +
                "            \"path_name\": \"后台/赛场管理/竞赛运维\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"资源管理\",\n" +
                "        \"value\": \"resource-manage-folder\",\n" +
                "        \"authority\": \"resource-manage-folder\",\n" +
                "        \"path_name\": \"后台/资源管理\",\n" +
                "        \"status\": 1,\n" +
                "        \"read_status\": 1,\n" +
                "        \"write_status\": 1,\n" +
                "        \"execute_status\": 1,\n" +
                "        \"per_status\": \"R/W/X\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"name\": \"工具库\",\n" +
                "            \"value\": \"tool\",\n" +
                "            \"authority\": \"tool\",\n" +
                "            \"path_name\": \"后台/资源管理/工具库\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"漏洞库\",\n" +
                "            \"value\": \"vulnerability\",\n" +
                "            \"authority\": \"vulnerability\",\n" +
                "            \"path_name\": \"后台/资源管理/漏洞库\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增\",\n" +
                "            \"value\": \"tool-create\",\n" +
                "            \"authority\": \"tool-create\",\n" +
                "            \"path_name\": \"后台/资源管理/新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑\",\n" +
                "            \"value\": \"tool-update\",\n" +
                "            \"authority\": \"tool-update\",\n" +
                "            \"path_name\": \"后台/资源管理/编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"工具库分类\",\n" +
                "            \"value\": \"tool-category-list\",\n" +
                "            \"authority\": \"tool-category-list\",\n" +
                "            \"path_name\": \"后台/资源管理/工具库分类\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增\",\n" +
                "            \"value\": \"vulnerability-add\",\n" +
                "            \"authority\": \"vulnerability-add\",\n" +
                "            \"path_name\": \"后台/资源管理/新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑\",\n" +
                "            \"value\": \"vulnerability-edit\",\n" +
                "            \"authority\": \"vulnerability-edit\",\n" +
                "            \"path_name\": \"后台/资源管理/编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增\",\n" +
                "            \"value\": \"tool-category-create\",\n" +
                "            \"authority\": \"tool-category-create\",\n" +
                "            \"path_name\": \"后台/资源管理/新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑\",\n" +
                "            \"value\": \"tool-category-update\",\n" +
                "            \"authority\": \"tool-category-update\",\n" +
                "            \"path_name\": \"后台/资源管理/编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"系统管理\",\n" +
                "        \"value\": \"system-management\",\n" +
                "        \"authority\": \"system-management\",\n" +
                "        \"path_name\": \"后台/系统管理\",\n" +
                "        \"status\": 1,\n" +
                "        \"read_status\": 1,\n" +
                "        \"write_status\": 1,\n" +
                "        \"execute_status\": 1,\n" +
                "        \"per_status\": \"R/W/X\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"name\": \"系统配置\",\n" +
                "            \"value\": \"system-config\",\n" +
                "            \"authority\": \"system-config\",\n" +
                "            \"path_name\": \"后台/系统管理/系统配置\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"审计日志\",\n" +
                "            \"value\": \"audit-log\",\n" +
                "            \"authority\": \"audit-log\",\n" +
                "            \"path_name\": \"后台/系统管理/审计日志\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"运行日志\",\n" +
                "            \"value\": \"system-log\",\n" +
                "            \"authority\": \"system-log\",\n" +
                "            \"path_name\": \"后台/系统管理/运行日志\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"用户管理\",\n" +
                "            \"value\": \"user-manage\",\n" +
                "            \"authority\": \"user-manage\",\n" +
                "            \"path_name\": \"后台/系统管理/用户管理\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"角色管理\",\n" +
                "            \"value\": \"role-management\",\n" +
                "            \"authority\": \"role-management\",\n" +
                "            \"path_name\": \"后台/系统管理/角色管理\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"赛题环境\",\n" +
                "            \"value\": \"ad-env-config\",\n" +
                "            \"authority\": \"ad-env-config\",\n" +
                "            \"path_name\": \"后台/系统管理/赛题环境\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"战队管理\",\n" +
                "            \"value\": \"ad-team-list\",\n" +
                "            \"authority\": \"ad-team-list\",\n" +
                "            \"path_name\": \"后台/系统管理/战队管理\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"用户新增\",\n" +
                "            \"value\": \"user-add\",\n" +
                "            \"authority\": \"user-add\",\n" +
                "            \"path_name\": \"后台/系统管理/用户新增\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"用户编辑\",\n" +
                "            \"value\": \"user-edit\",\n" +
                "            \"authority\": \"user-edit\",\n" +
                "            \"path_name\": \"后台/系统管理/用户编辑\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"用户审核\",\n" +
                "            \"value\": \"user-check\",\n" +
                "            \"authority\": \"user-check\",\n" +
                "            \"path_name\": \"后台/系统管理/用户审核\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增角色权限\",\n" +
                "            \"value\": \"role-add\",\n" +
                "            \"authority\": \"role-add\",\n" +
                "            \"path_name\": \"后台/系统管理/新增角色权限\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑角色权限\",\n" +
                "            \"value\": \"role-edit\",\n" +
                "            \"authority\": \"role-edit\",\n" +
                "            \"path_name\": \"后台/系统管理/编辑角色权限\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"添加角色中用户\",\n" +
                "            \"value\": \"add-role-users\",\n" +
                "            \"authority\": \"add-role-users\",\n" +
                "            \"path_name\": \"后台/系统管理/添加角色中用户\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"新增战队\",\n" +
                "            \"value\": \"ad-add-team\",\n" +
                "            \"authority\": \"ad-add-team\",\n" +
                "            \"path_name\": \"后台/系统管理/新增战队\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑战队\",\n" +
                "            \"value\": \"ad-edit-team\",\n" +
                "            \"authority\": \"ad-edit-team\",\n" +
                "            \"path_name\": \"后台/系统管理/编辑战队\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"战队成员管理\",\n" +
                "            \"value\": \"ad-team-manage\",\n" +
                "            \"authority\": \"ad-team-manage\",\n" +
                "            \"path_name\": \"后台/系统管理/战队成员管理\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"系统运维\",\n" +
                "        \"value\": \"system-operation-folder\",\n" +
                "        \"authority\": \"system-operation-folder\",\n" +
                "        \"path_name\": \"后台/系统运维\",\n" +
                "        \"status\": 1,\n" +
                "        \"read_status\": 1,\n" +
                "        \"write_status\": 1,\n" +
                "        \"execute_status\": 1,\n" +
                "        \"per_status\": \"R/W/X\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"name\": \"计算实例管理\",\n" +
                "            \"value\": \"cluster-management\",\n" +
                "            \"authority\": \"cluster-management\",\n" +
                "            \"path_name\": \"后台/系统运维/计算实例管理\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"网路实例管理\",\n" +
                "            \"value\": \"network-management\",\n" +
                "            \"authority\": \"network-management\",\n" +
                "            \"path_name\": \"后台/系统运维/网路实例管理\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"靶场实例监控\",\n" +
                "            \"value\": \"instance-monitoring\",\n" +
                "            \"authority\": \"instance-monitoring\",\n" +
                "            \"path_name\": \"后台/系统运维/靶场实例监控\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"资源用量管理\",\n" +
                "            \"value\": \"resource-usage\",\n" +
                "            \"authority\": \"resource-usage\",\n" +
                "            \"path_name\": \"后台/系统运维/资源用量管理\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"MAC地址管理\",\n" +
                "            \"value\": \"ad-MAC-manage\",\n" +
                "            \"authority\": \"ad-MAC-manage\",\n" +
                "            \"path_name\": \"后台/系统运维/MAC地址管理\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"监控详情\",\n" +
                "            \"value\": \"scene-detail\",\n" +
                "            \"authority\": \"scene-detail\",\n" +
                "            \"path_name\": \"后台/系统运维/监控详情\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"MAC地址详情\",\n" +
                "            \"value\": \"ad-MAC-detail\",\n" +
                "            \"authority\": \"ad-MAC-detail\",\n" +
                "            \"path_name\": \"后台/系统运维/MAC地址详情\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"系统公告\",\n" +
                "        \"value\": \"system-notice\",\n" +
                "        \"authority\": \"system-notice\",\n" +
                "        \"path_name\": \"后台/系统公告\",\n" +
                "        \"status\": 1,\n" +
                "        \"read_status\": 1,\n" +
                "        \"write_status\": 1,\n" +
                "        \"execute_status\": 1,\n" +
                "        \"per_status\": \"R/W/X\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"name\": \"新增公告\",\n" +
                "            \"value\": \"notice-add\",\n" +
                "            \"authority\": \"notice-add\",\n" +
                "            \"path_name\": \"后台/系统公告/新增公告\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"编辑公告\",\n" +
                "            \"value\": \"notice-edit\",\n" +
                "            \"authority\": \"notice-edit\",\n" +
                "            \"path_name\": \"后台/系统公告/编辑公告\",\n" +
                "            \"status\": 1,\n" +
                "            \"read_status\": 1,\n" +
                "            \"write_status\": 1,\n" +
                "            \"execute_status\": 1,\n" +
                "            \"per_status\": \"R/W/X\",\n" +
                "            \"children\": []\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }");
                */

        tmpMenuService.convert();
    }


}
