package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.sso.SSOLoginBO;
import cn.cyberpeace.mp.auth.bo.sso.SSOLoginOutBO;
import cn.cyberpeace.mp.auth.bo.sso.SocUser;
import cn.cyberpeace.mp.auth.config.SSOConfig;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.SSOSysEnum;
import cn.cyberpeace.mp.auth.extensions.security.filter.JwtAuthenticationFilter;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.AbstractSSOService;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.base.util.xml.XmlUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

/**
 * @BelongsPackage: cn.cyberpeace.mp.auth.service.impl
 * @Author: hhuang
 * @CreateTime: 2025/03/13  16:09
 * @Description: soc单点
 * @Version: 1.0
 */
@Slf4j
@Service
public class SSOSocServiceImpl extends AbstractSSOService {


    @Resource
    private SSOConfig ssoConfig;

    @Resource(name = "restTemplate")
    private RestTemplate restTemplate;

    @Override
    public boolean isSupport(SSOSysEnum sysEnum) {
        return SSOSysEnum.SOC == sysEnum;
    }

    @Override
    public void doLogin(SSOLoginBO ssoLoginBO) {
        log.info("soc sso login ticket is {}, service is {}",ssoLoginBO.getTicket(),ssoLoginBO.getService());
        HttpServletResponse response = ssoLoginBO.getResponse();
        String responseBody = null;
        XmlMapper xmlMapper = new XmlMapper();
        SocUser socUser = null;
        try {
            String url = ssoConfig.getSocTicketCheckUrl()+"?ticket="+ssoLoginBO.getTicket()+"&service="+ssoLoginBO.getService();
            responseBody = restTemplate.getForObject(url, String.class);
            //错误信息处理
            String authenticationFailure = XmlUtils.getTextForElement(responseBody, "authenticationFailure");
            if(StringUtils.isNotEmpty(authenticationFailure)){
                log.warn("soc sso get user info failed! fail info is {}",authenticationFailure);
                return;
            }
            //principal
            String user = XmlUtils.getTextForElement(responseBody, "user");
            if(StringUtils.isEmpty(user)){
                log.warn("soc sso no principal was found in the response from the CAS server!");
                return;
            }
            socUser = xmlMapper.readValue(responseBody, new TypeReference<SocUser>() {
            });
        } catch (Exception e) {
            log.warn("failed to check ticket , response is {}", responseBody, e);
            throw new RuntimeException(e);
        }
        try {
            //入库用户处理
            log.info("soc sso login ,starting create or update user!");
            UserModel userModel = createOrUpdateSocUser(ssoLoginBO, socUser);
            //重定向
            successLoginRedirect(userModel, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * @description:
     * @param ssoLoginBO
     * @param socUser
     * @throws
     * @return UserModel
     */
    private UserModel createOrUpdateSocUser(SSOLoginBO ssoLoginBO, SocUser socUser) {

        return new UserModel();
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
        log.info("soc sso login to redirect url:" + ssoConfig.getHomePageUrl());
        response.sendRedirect(ssoConfig.getHomePageUrl());
    }


    @Override
    public void doLogout(SSOLoginOutBO ssoLoginOutBO) {
        if(StringUtils.isEmpty(ssoLoginOutBO.getTicket())){
            log.warn("soc logout param ticket is empty!");
            return;
        }
        //目前现场提示soc无单点登出接口
        try {
            HttpServletResponse response = ssoLoginOutBO.getResponse();
            String originalService = StringUtils.isNotEmpty(ssoLoginOutBO.getService())?
                URLEncoder.encode(ssoLoginOutBO.getService()):URLEncoder.encode(ssoConfig.getSocLoginUrl());
            //这里将配置改为重定向登出地址，soc-login-url为服务前缀
            response.sendRedirect(ssoConfig.getSocLogoutUrl()+"?service="+originalService);
        } catch (Exception e) {
            log.error("soc logout request failed!", e);
        }
    }
}
