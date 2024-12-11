package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.extensions.security.common.JwtCommon;
import cn.cyberpeace.mp.auth.extensions.security.filter.JwtAuthenticationFilter;
import cn.cyberpeace.mp.auth.model.TokenModel;
import cn.cyberpeace.mp.auth.repository.TokenMapper;
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

    @Resource
    private AuthConfig authConfig;

    @Resource
    private TokenMapper tokenMapper;

    @GetMapping
    @ApiOperation("赛宁第三方登录接口，重定向到赛宁个人主页")
    public String loginAndRedirectToHomePage(@RequestParam(value = "code", required = false) String code, @RequestParam(value = "url", required = false) String url, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(url)) {
            url = String.format("http://%s/page/mg/admin/knowledge-space-home", authConfig.getMpIp());
        }
        // 如果有 jwt 且为有效的登录状态，则直接跳转到url
        if (this.tokenCheck(request, response)) {
            return String.format("redirect:%s", url);
        } else {
            return thirdLoginService.loginByToken(code, request, response, url);
        }
    }

    private boolean tokenCheck(HttpServletRequest request, HttpServletResponse response) {
        String thirdToken = JwtCommon.getThirdToken(request);
        log.info("thirdToken:{}", thirdToken);
        if (StringUtils.isEmpty(thirdToken)) {
            return false;
        }
        TokenModel tokenModel = tokenMapper.getByLoginToken(thirdToken);
        if (tokenModel == null) {
            return false;
        }
        JwtAuthenticationFilter.addJWTCookieToResponse(response, tokenModel.getJwt());
        return thirdLoginService.tokenCheck(tokenModel.getLoginToken());
    }
}
