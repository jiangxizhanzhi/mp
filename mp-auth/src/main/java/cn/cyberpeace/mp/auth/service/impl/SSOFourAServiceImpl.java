package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.sso.SSOLoginBO;
import cn.cyberpeace.mp.auth.config.SSOConfig;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.SSOSysEnum;
import cn.cyberpeace.mp.auth.extensions.security.filter.JwtAuthenticationFilter;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.AbstractSSOService;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import com.boco.iam.sso.client.LoginUtil;
import com.boco.iam.sso.client.ResultCode;
import com.boco.iam.sso.client.TransferTicket;
import com.boco.iam.sso.client.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Override
    public boolean isSupport(SSOSysEnum sysEnum) {
        return SSOSysEnum.FOUR_A == sysEnum;
    }

    @Override
    public void doLogin(SSOLoginBO ssoLoginBO) {
        log.info("4a sso login ticket is {}",ssoLoginBO.getTicket());
        HttpServletRequest request = ssoLoginBO.getRequest();
        HttpServletResponse response = ssoLoginBO.getResponse();
        if(!loginUtil.isEnable()){
            //服务不可用，认证失败
            log.warn("4a sso service is not available!");
            loginUtil.redirectToServer(request, response);
            return;
        }
        if(!loginUtil.checkTicket(request)){
            log.warn("4a sso request param ticket is empty!");
            loginUtil.redirectToServer(request, response);
            return;
        }
        //获取ticket信息
        String strTic = loginUtil.getTicket(request);
        TransferTicket ticket = loginUtil.analysTicket(strTic);
        if(Objects.isNull(ticket)){
            log.warn("4a sso analysis ticket is null!");
            loginUtil.redirectToServer(request, response);
            return;
        }
        String errorMessage = null;
        if(!ResultCode.RESULT_OK.equals(ticket.getRetCode())){
            //根据错误信息
            errorMessage = loginUtil.getCodeMess(ticket.getRetCode(),
                request.getLocale().getLanguage());
            log.warn("4a sso analysis ticket failed!! reason is {}",errorMessage);
            loginUtil.redirectToServer(request, response);
            return;
        }
        UserInfo userInfo = loginUtil.qryUserByTicket(ticket);
        if(Objects.isNull(userInfo)){
            //用户信息不存在
            log.warn("4a sso get userInfo is failed!");
            loginUtil.redirectToServer(request, response);
            return;
        }
        if(!ResultCode.RESULT_OK.equals(userInfo.getRetCode())){
            errorMessage = loginUtil.getCodeMess(ticket.getRetCode(),
                request.getLocale().getLanguage());
            log.warn("4a sso login failed! reason is {}",errorMessage);
            loginUtil.redirectToServer(request, response);
            return;
        }
        //应用资源根据帐号信息做登录后业务处理
        try {
            UserModel userModel = createOrUpdateFourAUser(userInfo);
            successLoginRedirect(userModel,response);
        } catch (Exception e) {
            log.error("4a sso login failed!",e);
        }
    }

    /**
     * @description: 创建更新用户
     * @param userInfo
     * @throws
     * @return UserModel
     */
    private UserModel createOrUpdateFourAUser(UserInfo userInfo) {
        //TODO
        return new UserModel();
    }


    /**
     * @description: 成功登录重定向
     * @param userModel
     * @param response
     * @throws Exception
     * @return void
     */
    private void successLoginRedirect(UserModel userModel,HttpServletResponse response) throws Exception{
        String newJwt = JwtUtils.generateToken(new JwtUtils.UserInfo(userModel.getId(), userModel.getUsername(), userModel.getTokenSecret()));
        JwtAuthenticationFilter.addJWTCookieToResponse(response, newJwt);
        response.setHeader(AuthConstant.JWT_HEADER_KEY, newJwt);
        response.setHeader(AuthConstant.CP_TOKEN_REFRESHED_HEADER_KEY, "true");
        response.sendRedirect(ssoConfig.getHomePageUrl());
    }



    @Override
    public void doLogout() {
//        loginUtil.logout();
    }
}
