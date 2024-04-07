package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.service.ThirdAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    private AuthConfig authConfig;

    @GetMapping
    @ApiOperation("赛宁第三方登录接口，重定向到赛宁个人主页")
    public String loginAndRedirectToHomePage(@RequestParam("token") String token, HttpServletRequest request, HttpServletResponse response) {
        thirdLoginService.loginByToken(token, request, response);
        // 重定向到另一个域名的页面
        return String.format("redirect:%s", authConfig.getMpHomePageViewUrl());
    }
}
