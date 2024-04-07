package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Api(value = "第三方登录管理接口", tags = "UNAUTH-第三方登录接口")
@Slf4j
@Controller
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/third-login")
@RequiredArgsConstructor
public class ThirdSsoController {

    @GetMapping
    @ApiOperation("赛宁第三方登录接口，重定向到赛宁个人主页")
    public RedirectView loginAndRedirectToHomePage() {



        // 重定向到另一个域名的页面
        return new RedirectView("https://example.com/redirected-page");
    }
}
