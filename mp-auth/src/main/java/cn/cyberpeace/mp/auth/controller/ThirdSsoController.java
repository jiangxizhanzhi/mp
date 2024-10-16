package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.service.impl.ThirdSsoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "第三方登录管理接口", tags = "UNAUTH-第三方登录接口")
@Slf4j
@Controller
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/third-login")
public class ThirdSsoController {

    @Resource
    private ThirdSsoService thirdLoginService;

    @GetMapping
    @ApiOperation("赛宁第三方登录接口，重定向到赛宁个人主页")
    public String loginAndRedirectToHomePage(@RequestParam("code") String code, @RequestParam("url") String url, HttpServletRequest request, HttpServletResponse response) {
        // 如果有 jwt 且为有效的登录状态，则直接跳转到url
        if (this.tokenCheck(request)) {
            return String.format("redirect:%s", url);
        } else {
            thirdLoginService.loginByToken(code, request, response);
            return String.format("redirect:%s", url);
        }
    }

    private boolean tokenCheck(HttpServletRequest request) {
        String thirdtoken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                // 查找名为"thirdtoken"的Cookie
                if ("thirdtoken".equals(cookie.getName())) {
                    thirdtoken = cookie.getValue();
                }
            }
        }
        if (StringUtils.isEmpty(thirdtoken)) {
            return false;
        }
        return thirdLoginService.tokenCheck(thirdtoken);
    }
}
