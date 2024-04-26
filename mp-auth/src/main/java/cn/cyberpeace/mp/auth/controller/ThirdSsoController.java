package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.UserMapper;
import cn.cyberpeace.mp.auth.service.ThirdAuthService;
import cn.cyberpeace.mp.auth.service.impl.JiangXiHaiboAuthServiceImpl;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "第三方登录管理接口", tags = "UNAUTH-第三方登录接口")
@Slf4j
@Controller
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/third-login")
public class ThirdSsoController {

    @Resource(name = "jiangXiHaiboAuthServiceImpl")
    private ThirdAuthService thirdLoginService;

    @Resource
    private AuthConfig authConfig;

    @GetMapping
    @ApiOperation("赛宁第三方登录接口，重定向到赛宁个人主页")
    public String loginAndRedirectToHomePage(@RequestParam("token") String token, @RequestParam(name = "type", required = false) String type,
                                             @RequestParam(name = "course_id", required = false) String courseId,
                                             @RequestParam(name = "lesson_id", required = false) String lessonId,
                                             HttpServletRequest request, HttpServletResponse response) {
        log.info("token {} , type {} , course_id {} , lesson_id {}", token, token, courseId, lessonId);
        thirdLoginService.loginByToken(token, request, response);
        // 重定向到另一个域名的页面
        String redirectUrl = authConfig.getMpHomePageViewUrl();
        if (StringUtils.isNotBlank(type)) {
            if ("course".equals(type)) {
                if (StringUtils.isNotBlank(lessonId) && StringUtils.isNotBlank(courseId)) {
                    redirectUrl = String.format(authConfig.getMpLessonViewUrl(), courseId, lessonId);
                } else if (StringUtils.isNotBlank(courseId)) {
                    redirectUrl = String.format(authConfig.getMpCourseViewUrl(), courseId);
                }
            }
        }
        log.info("redirect {}", redirectUrl);
        return String.format("redirect:%s", redirectUrl);
    }

    @ResponseBody
    @GetMapping(value = "/get_user_by_token")
    @ApiOperation("赛宁第三方登录接口，重定向到赛宁个人主页")
    public JiangXiHaiboAuthServiceImpl.HaiboUser getHaiboUser(@RequestParam("token") String token) {
        log.info("token {}", token);
        token = StringUtils.replace(token, " ", "+");
        return BeanCommon.getBean(JiangXiHaiboAuthServiceImpl.class).getHaiboUser(token);
    }
}
