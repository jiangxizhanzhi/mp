// All rights reserved.
// Copyright (C) 2024
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
import cn.cyberpeace.mp.auth.extensions.security.filter.JwtAuthenticationFilter;
import cn.cyberpeace.mp.auth.model.TokenModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.TokenMapper;
import cn.cyberpeace.mp.auth.request.user.UserRequest;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.impl.ThirdSsoService;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;

import static cn.cyberpeace.mp.auth.constant.AuthConstant.ROLE_ADMIN_ID;
import static cn.cyberpeace.mp.auth.constant.AuthConstant.ROOT_ORG_ID;

/**
 * 第三个sso控制器
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

    @Resource
    private UserService userService;

    @GetMapping
    @ApiOperation("赛宁第三方登录接口，重定向到赛宁个人主页")
    public String loginAndRedirectToHomePage(@RequestParam(value = "account", required = false) String account, @RequestParam(value = "login_token", required = false) String loginToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
        var userInfo = thirdLoginService.getuserinfoByLogintoken(loginToken);
        if (userInfo != null && StringUtils.isNotEmpty(userInfo.getUserAccount())) {
            // 查询到用户信息,即登录成功，写上登录态，然后跳转
            try {
                UserModel userModel = userService.getByUserName(userInfo.getUserAccount());
                if (Objects.isNull(userModel)) {
                    // 新增一个用户
                    UserRequest userRequest = new UserRequest();
                    userRequest.setUsername(userInfo.getUserAccount());
                    userRequest.setName(userInfo.getUserName());
                    userRequest.setRoleIds(Collections.singletonList(ROLE_ADMIN_ID));
                    userRequest.setOrgIds(Collections.singletonList(ROOT_ORG_ID));
                    userRequest.setPassword("d8165e80c4abed2dcc1dd06df1a8be31");
                    userModel = userService.add(userRequest, true);
                }
                // 更新下菜单
                UserModel updateModel = new UserModel();
                updateModel.setId(userModel.getId());
                updateModel.setMenusContent(JsonCommon.stringify(userInfo.getRoleMenu()));
                userService.update(updateModel);

                String jwtToken = JwtUtils.generateToken(new JwtUtils.UserInfo(userModel.getId(), userModel.getUsername(), userModel.getTokenSecret()));
                JwtAuthenticationFilter.thirdLoginSuccessRefreshToken(response, jwtToken, loginToken);

                TokenModel tokenModel = new TokenModel();
                tokenModel.setLogonToken(loginToken);
                tokenModel.setAccessToken(loginToken);
                tokenModel.setJwt(jwtToken);
                tokenModel.setUserName(userModel.getUsername());
                tokenModel.setCreateTime(LocalDateTime.now());
                log.info("tokeModel:[{}]", tokenModel);
                tokenMapper.create(tokenModel);
            } catch (Exception e) {
                log.error("faild to loginByToken ", e);
                throw new RuntimeException(e);
            }
            // 重定向到首页
            return String.format("redirect:%s", authConfig.getHomePageV2());
        } else {
            // 登录失败，返回登录页面
            return String.format("redirect:%s", authConfig.getThirdLoginPageV2());
        }
    }

    @GetMapping("/logout_page_url")
    @ResponseBody
    @ApiOperation("获取退出登录后跳转的地址")
    public ServiceResult<String> getLogoutPage() {
        String url = BeanCommon.getBean(AuthConfig.class).getThirdLoginPageV2();
        return new ServiceResult<>(url).asResult();
    }
}
