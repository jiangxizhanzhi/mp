package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.config.ThirdSsoConfig;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.extensions.security.common.JwtCommon;
import cn.cyberpeace.mp.auth.extensions.security.filter.JwtAuthenticationFilter;
import cn.cyberpeace.mp.auth.model.TokenModel;
import cn.cyberpeace.mp.auth.repository.TokenMapper;
import cn.cyberpeace.mp.auth.service.impl.GuanWangAuthServiceImpl;
import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    @Resource
    private TokenMapper tokenMapper;

    @Resource
    private ThirdSsoConfig thirdSsoConfig;

    @Resource
    private GuanWangAuthServiceImpl thirdLoginService;

    @AuthTarget(value = {TargetEnum.NO_AUTH})
    @GetMapping
    @ApiOperation("赛宁第三方登录接口")
    public String loginAndRedirectToHomePage(@RequestParam(value = "code", required = false) String code, @RequestParam(name = "state", required = false) String type, HttpServletRequest request, HttpServletResponse response) {
        if (code == null) {
            String url = thirdSsoConfig.getGetcodeurl();
            url = url + "?client_id=" + thirdSsoConfig.getClientid();
            url = url + "&redirect_uri=" + thirdSsoConfig.getRedirecturi();
            url = url + "&response_type=" + thirdSsoConfig.getResponsetype();
            url = url + "&oauth_timestamp=" + System.currentTimeMillis();
            return String.format("redirect:" + url);
        }
        return this.loginAndRedirectToHomePageCode(code, type, request, response);
    }

    @AuthTarget(value = {TargetEnum.NO_AUTH})
    @GetMapping("/code")
    @ApiOperation("赛宁第三方登录接口")
    public String loginAndRedirectToHomePageCode(@RequestParam("code") String code, @RequestParam(name = "state", required = false) String type, HttpServletRequest request, HttpServletResponse response) {
        String url = thirdSsoConfig.getPageurl();
        log.info("第三方登录 , code {} , 跳转url {}", code, url);
        // 如果有 jwt 且为有效的登录状态，则直接跳转到url
        if (this.tokenCheck(request, response)) {
            return String.format("redirect:%s", url);
        } else {
            return thirdLoginService.loginByCode(code, request, response);
        }
    }

    private boolean tokenCheck(HttpServletRequest request, HttpServletResponse response) {
        String thirdToken = JwtCommon.getThirdToken(request);
        log.info("check thirdToken:{}", thirdToken);
        if (StringUtils.isEmpty(thirdToken)) {
            return false;
        }
        HumanCondition humanCondition = new HumanCondition().filterEqual(TokenModel::getAccessToken, thirdToken);
        TokenModel tokenModel = tokenMapper.first(humanCondition);
        if (tokenModel == null) {
            log.info("未查询到 access token对应的jwt token");
            return false;
        }
        JwtAuthenticationFilter.addJWTCookieToResponse(response, tokenModel.getJwt());
        return thirdLoginService.tokenCheck(tokenModel.getAccessToken());
    }

    @Getter
    @Setter
    public static class ThirdToken {
        private String token;
        private String state;
    }
}
