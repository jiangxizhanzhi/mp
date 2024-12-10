package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.config.ThirdSsoConfig;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "第三方登录管理接口", tags = "UNAUTH-第三方登录接口")
@Slf4j
@Controller
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH)
public class ThirdLogoutController {

    @Resource
    private ThirdSsoConfig thirdSsoConfig;

    @AuthTarget(value = {TargetEnum.NO_AUTH})
    @GetMapping("/logout_page")
    public String loginAndRedirectToHomePage(HttpServletRequest request, HttpServletResponse response) {
        log.info("logout redirect: {}", thirdSsoConfig.getLogoutredirecturl());
        return "redirect:" + thirdSsoConfig.getLogoutredirecturl();
    }
}
