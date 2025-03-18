package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.sso.SSOLoginBO;
import cn.cyberpeace.mp.auth.bo.sso.SSOLoginOutBO;
import cn.cyberpeace.mp.auth.config.SSOConfig;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.SSOSysEnum;
import cn.cyberpeace.mp.auth.extensions.security.HttpLogoutSuccessHandler;
import cn.cyberpeace.mp.auth.extensions.security.filter.JwtAuthenticationFilter;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.request.user.UserRequest;
import cn.cyberpeace.mp.auth.service.AbstractSSOService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import com.boco.iam.sso.client.LoginUtil;
import com.boco.iam.sso.client.ResultCode;
import com.boco.iam.sso.client.TransferTicket;
import com.boco.iam.sso.client.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @BelongsPackage: cn.cyberpeace.mp.auth.service.impl
 * @Author: hhuang
 * @CreateTime: 2025/03/13  16:08
 * @Description: 4a单点
 * @Version: 1.0
 */
@Slf4j
@Service
public class SSOFourAServiceImpl extends AbstractSSOService {

    @Resource
    private LoginUtil loginUtil;

    @Resource
    private SSOConfig ssoConfig;

    @Autowired
    private UserService userService;

    @Autowired
    private HttpLogoutSuccessHandler httpLogoutSuccessHandler;

    @Override
    public boolean isSupport(SSOSysEnum sysEnum) {
        return SSOSysEnum.FOUR_A == sysEnum;
    }

    @Override
    public void doLogin(SSOLoginBO ssoLoginBO) {
        log.info("4a sso login ticket is {}", ssoLoginBO.getTicket());
        HttpServletRequest request = ssoLoginBO.getRequest();
        HttpServletResponse response = ssoLoginBO.getResponse();
        UserInfo userInfo = new UserInfo();
        if (!loginUtil.isEnable()) {
            //服务不可用，认证失败
            log.warn("4a sso service is not available!");
            loginUtil.redirectToServer(request, response);
            return;
        }
        if (!loginUtil.checkTicket(request)) {
            log.warn("4a sso request param ticket is empty!");
            loginUtil.redirectToServer(request, response);
            return;
        }
        //获取ticket信息
        String strTic = loginUtil.getTicket(request);
        TransferTicket ticket = loginUtil.analysTicket(strTic);
        if (Objects.isNull(ticket)) {
            log.warn("4a sso analysis ticket is null!");
            loginUtil.redirectToServer(request, response);
            return;
        }
        String errorMessage = null;
        if (!ResultCode.RESULT_OK.equals(ticket.getRetCode())) {
            //根据错误信息
            errorMessage = loginUtil.getCodeMess(ticket.getRetCode(),
                    request.getLocale().getLanguage());
            log.warn("4a sso analysis ticket failed!! reason is {}", errorMessage);
            loginUtil.redirectToServer(request, response);
            return;
        }
        userInfo = loginUtil.qryUserByTicket(ticket);
        if (Objects.isNull(userInfo)) {
            //用户信息不存在
            log.warn("4a sso get userInfo is failed!");
            loginUtil.redirectToServer(request, response);
            return;
        }
        if (!ResultCode.RESULT_OK.equals(userInfo.getRetCode())) {
            errorMessage = loginUtil.getCodeMess(ticket.getRetCode(),
                    request.getLocale().getLanguage());
            log.warn("4a sso login failed! reason is {}", errorMessage);
            loginUtil.redirectToServer(request, response);
            return;
        }
        //应用资源根据帐号信息做登录后业务处理
        try {
            UserModel userModel = createOrUpdateFourAUser(ssoLoginBO, userInfo);
            successLoginRedirect(userModel, response);
        } catch (Exception e) {
            log.error("4a sso login failed!", e);
        }
    }

    /**
     * @param userInfo
     * @return UserModel
     * @throws
     * @description: 创建更新用户
     */
    private UserModel createOrUpdateFourAUser(SSOLoginBO ssoLoginBO, UserInfo userInfo) {
        UserModel user = new UserModel();
        if (Objects.nonNull(userInfo) && ResultCode.RESULT_OK.equals(userInfo.getRetCode())) {
            //应用资源根据帐号信息做登录后业务处理
            List<UserModel> users = userService.getUserBy4AAccountId(userInfo.getAccountID());
            log.info("查找该4A用户在本系统的账户数量: {}", users.size());
            UserRequest addRequest = new UserRequest();
            //创建用户(待修改信息)
            addRequest.setUsername(userInfo.getName());
            addRequest.setName(userInfo.getName());
            addRequest.setAccountId(userInfo.getAccountID());
            addRequest.setIdCard(userInfo.getIdCardNum());
            addRequest.setNickname(userInfo.getPhone());
            addRequest.setMobile(userInfo.getMobile());
            addRequest.setEmail(userInfo.getEmail());
            // 登录凭证ticket
            addRequest.setFourATicket(ssoLoginBO.getTicket());
            if (users == null || users.size() == 0) {// 目前无该用户
                addRequest.setOutUserType(SSOSysEnum.FOUR_A.getCode());
                addRequest.setPassword(AuthConstant.DEFAULT_INITIALPASSWORD);// 该密码为前端加密过后的密码,原始密码为Xctfoj01
                // 预设学员角色和默认组织
                addRequest.setOrgIds(Collections.singletonList(AuthConstant.ROOT_ORG_ID));
                addRequest.setRoleIds(Collections.singletonList(AuthConstant.ROLE_XUEYUAN_ID));// 以实际环境的学员角色id为准
                user = userService.addFourAUser(addRequest);
            } else if (users.size() == 1) {// 查找到唯一用户
                // 修改用户,目前无需修改其他信息
                user = userService.get(users.get(0).getId()).sourceObj();
                user = userService.modify(addRequest);
            } else {
                throw new MpRuntimeException(AuthErrorCode.FOUR_A_USER_SELECT_ERROR);//用户查询错误
            }
        } else {
            //用户不存在或者其他非成功的情况
            throw new MpRuntimeException(AuthErrorCode.PERMISSION_DENIED);//登录失败
        }
        return user;
    }

    /**
     * @param userModel
     * @param response
     * @return void
     * @throws Exception
     * @description: 成功登录重定向
     */
    private void successLoginRedirect(UserModel userModel, HttpServletResponse response) throws Exception {
        String newJwt = JwtUtils.generateToken(new JwtUtils.UserInfo(userModel.getId(), userModel.getUsername(), userModel.getTokenSecret()));
        JwtAuthenticationFilter.addJWTCookieToResponse(response, newJwt);
        response.setHeader(AuthConstant.JWT_HEADER_KEY, newJwt);
        response.setHeader(AuthConstant.CP_TOKEN_REFRESHED_HEADER_KEY, "true");
        response.sendRedirect(ssoConfig.getHomePageUrl());
    }

    @Override
    public void doLogout(SSOLoginOutBO ssoLoginOutBO) {
        if (StringUtils.isBlank(ssoLoginOutBO.getTicket())) {
            log.warn("4A logout failed: Ticket is empty!");
            return;
        }

        // 4A 单点登出
        try {
            loginUtil.logout(ssoLoginOutBO.getTicket());
        } catch (Exception e) {
            log.error("4A logout request failed!", e);
        }

        // 处理本系统登出逻辑
        try {
            TransferTicket ticket = loginUtil.analysTicket(ssoLoginOutBO.getTicket());
            UserInfo userInfo = loginUtil.qryUserByTicket(ticket);

            if (Objects.nonNull(userInfo) && ResultCode.RESULT_OK.equals(userInfo.getRetCode())) {
                UserModel user = getSingleUserByAccountId(userInfo.getAccountID());
                if (user != null) {
                    httpLogoutSuccessHandler.onFourALogoutSuccess(ssoLoginOutBO.getRequest(), ssoLoginOutBO.getResponse(), user);
                }
            }
        } catch (Exception e) {
            log.error("System logout failed!", e);
        }
    }

    /**
     * 根据 4A 账号 ID 查询用户，并确保唯一性
     */
    private UserModel getSingleUserByAccountId(String accountId) {
        List<UserModel> users = userService.getUserBy4AAccountId(accountId);
        if (CollectionUtils.isEmpty(users)) {
            log.warn("No user found for 4A account ID: {}", accountId);
            return null;
        }
        if (users.size() > 1) {
            log.warn("Multiple users found for 4A account ID: {}, taking the first one.", accountId);
        }
        return users.get(0);
    }

    /**
     * @param response
     * @return void
     * @throws Exception
     * @description: 退出重定向
     */
    private void successLoginOutRedirect(HttpServletResponse response) throws Exception {
        response.sendRedirect(ssoConfig.getOutHomePageUrl());
    }

    @Override
    public void logoutByUser(HttpServletRequest request, HttpServletResponse response, UserModel loginUser) {
        // 漫游票据登出
        loginUtil.logout(loginUser.getFourATicket());
        // 重定向
        try {
            successLoginOutRedirect(response);
        } catch (Exception e) {
            log.error("Web redirect failed!", e);
        }
    }
}
