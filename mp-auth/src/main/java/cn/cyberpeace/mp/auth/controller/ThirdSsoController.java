// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name ThirdSsoController.java.
// File size 2939.
// File create date Fri Jan 17 09:57:15 CST 2025.
// File update date Fri Jan 17 11:23:13 CST 2025.
// @Version 1.0.0
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
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.List;

/**
 * 第三个sso控制器
 *
 * @author 二竹
 * @date 2025/01/17
 */
@Api(value = "第三方登录管理接口", tags = "UNAUTH-第三方登录接口")
@Slf4j
@Controller
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/third-login")
public class ThirdSsoController {

    /**
     * 第三登录服务
     */
    @Resource
    private ThirdSsoService thirdLoginService;

    /**
     * auth配置
     */
    @Resource
    private AuthConfig authConfig;

    /**
     *
     */
    @Resource
    private TokenMapper tokenMapper;

    /**
     * 登录并重定向到主页
     *
     * @param code     代码
     * @param url      url
     * @param request  请求
     * @param response 响应
     * @return {@link String }
     */
    @GetMapping
    @ApiOperation("赛宁第三方登录接口，重定向到赛宁个人主页")
    public String loginAndRedirectToHomePage(@RequestParam(value = "code", required = false) String code, @RequestParam(value = "url", required = false) String url, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (StringUtils.isEmpty(url)) {
            url = String.format("%s/page/mg/admin/knowledge-space-home", authConfig.getMpIp());
        }
        // 如果有 jwt 且为有效的登录状态，则直接跳转到url
        TokenCheckResult tokenCheckResult = tokenCheck(request, response);
        if (null != tokenCheckResult.getIsSuccess() && tokenCheckResult.getIsSuccess()) {

            // 更新下菜单信息信息
            thirdLoginService.updateMenus(tokenCheckResult.getAccessToken());

            return String.format("redirect:%s", url);
        } else {
            return thirdLoginService.loginByToken(code, request, response, url);
        }
    }

    /**
     * 令牌检查
     *
     * @param request  请求
     * @param response 响应
     * @return boolean
     */
    private TokenCheckResult tokenCheck(HttpServletRequest request, HttpServletResponse response) {
        TokenCheckResult result = new TokenCheckResult();
        result.setIsSuccess(false);
        String thirdToken = JwtCommon.getThirdToken(request);
        log.info("thirdToken:{}", thirdToken);
        if (StringUtils.isEmpty(thirdToken)) {
            return result;
        }
        List<TokenModel> tokenModels = tokenMapper.getByLoginToken(thirdToken);
        if (CollectionUtils.isEmpty(tokenModels)) {
            return result;
        }
        JwtAuthenticationFilter.addJWTCookieToResponse(response, tokenModels.get(0).getJwt());
        result.setIsSuccess(thirdLoginService.tokenCheck(tokenModels.get(0).getLoginToken()));
        if (result.getIsSuccess()) {
            result.setAccessToken(tokenModels.get(0).getAccessToken());
        }
        return result;
    }

    @Data
    private class TokenCheckResult implements Serializable {
        Boolean isSuccess;
        String accessToken;
    }
}
