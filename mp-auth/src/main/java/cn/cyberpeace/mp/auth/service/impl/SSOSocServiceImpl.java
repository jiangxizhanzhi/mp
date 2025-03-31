package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.sso.SSOLoginBO;
import cn.cyberpeace.mp.auth.bo.sso.SSOLoginOutBO;
import cn.cyberpeace.mp.auth.bo.sso.SocUser;
import cn.cyberpeace.mp.auth.config.SSOConfig;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.SSOSysEnum;
import cn.cyberpeace.mp.auth.extensions.security.filter.JwtAuthenticationFilter;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.RoleMapper;
import cn.cyberpeace.mp.auth.request.user.UserRequest;
import cn.cyberpeace.mp.auth.service.AbstractSSOService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.xml.XmlUtils;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

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

    @Resource
    private UserService userService;

    @Override
    public boolean isSupport(SSOSysEnum sysEnum) {
        return SSOSysEnum.SOC == sysEnum;
    }

    @Override
    public void doLogin(SSOLoginBO ssoLoginBO) {
        log.info("soc sso login ticket is {}, service is {}",ssoLoginBO.getTicket(),ssoLoginBO.getService());
        HttpServletResponse response = ssoLoginBO.getResponse();
        String responseBody = null;
        SocUser socUser = null;
        try {
            String url = ssoConfig.getSocTicketCheckUrl()+"?ticket="+ssoLoginBO.getTicket()+"&service="+ssoLoginBO.getService();
            log.info("url is {}",url);
            responseBody = restTemplate.getForObject(url, String.class);
            log.info("origin response is {}",responseBody);
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
            Map<String, Object> userInfoMap = XmlUtils.extractCustomAttributes(responseBody);
            socUser = JsonCommon.mapToBean(userInfoMap, SocUser.class);
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
        //应用资源根据帐号信息做登录后业务处理
        if(Objects.isNull(socUser)){
            log.warn("soc user is null!");
            throw new MpRuntimeException(AuthErrorCode.SOC_USER_HANDLE_ERROR);
        }
        //用户查询多加类型参数
        List<UserModel> users = userService.getUserBy4AAccountId(socUser.getUserId());
        users = users.stream().filter(userModel -> SSOSysEnum.SOC.getCode().equals(userModel.getOutUserType())).collect(
            Collectors.toList());
        log.info("查找该soc用户在本系统的账户数量: {}", users.size());
        UserRequest addRequest = new UserRequest();
        //信息修改按照实际soc用户信息
        addRequest.setUsername(socUser.getUsername());
        addRequest.setName(socUser.getRealName());
        addRequest.setMobile(socUser.getPhone());
        addRequest.setEmail(socUser.getEmail());
        addRequest.setAddress(socUser.getAttributionScope());
        // 登录凭证ticket
        addRequest.setFourATicket(ssoLoginBO.getTicket());
        UserModel user = new UserModel();
        // 目前无该用户
        if (CollectionUtils.isEmpty(users)) {
            addRequest.setAccountId(socUser.getUserId());
            addRequest.setOutUserType(SSOSysEnum.SOC.getCode());
            addRequest.setOrgIds(Collections.singletonList(AuthConstant.ROOT_ORG_ID));
            /**
             * 山东移动的 超级管理员 管理员 -> 管理员 ， 全部前后台菜单
             * 山东移动的 省管理 专家 -> 教员 ， 全部前后台菜单（不包含后台系统管理、系统运维菜单）
             * 山东移动的 学员 -> 学员，全部前台菜单
             */
            RoleMapper roleMapper = BeanCommon.getBean(RoleMapper.class);
            if ("soc_supper_admin".equals(socUser.getRoleCode()) || "sys_admin".equals(socUser.getRoleCode())) {
                var role = roleMapper.first(new HumanCondition().filterEqual(RoleModel::getName, "管理员"));
                addRequest.setRoleIds(Arrays.asList(role.getId()));
            } else if ("soc_admin".equals(socUser.getRoleCode()) || "soc_expert".equals(socUser.getRoleCode())) {
                var role = roleMapper.first(new HumanCondition().filterEqual(RoleModel::getName, "教员"));
                addRequest.setRoleIds(Arrays.asList(role.getId()));
            } else {
                //默认学员角色
                var role = roleMapper.first(new HumanCondition().filterEqual(RoleModel::getName, "学员"));
                addRequest.setRoleIds(Arrays.asList(role.getId()));
            }
            addRequest.setPassword(AuthConstant.DEFAULT_INITIALPASSWORD);// 该密码为前端加密过后的密码,原始密码为Xctfoj01
            log.info("prepare add soc User: {}" + JSON.toJSONString(addRequest));
            user = userService.addSSoUser(addRequest);
        } else if (users.size() == 1) {// 查找到唯一用户
            // 刷新用户信息
            user = userService.get(users.get(0).getId()).sourceObj();
            addRequest.setId(user.getId());
            log.info("prepare modify soc User: {}" + JSON.toJSONString(addRequest));
            user = userService.modify(addRequest);
        } else {
            log.warn("soc user data duplicate!");
            throw new MpRuntimeException(AuthErrorCode.SOC_USER_HANDLE_ERROR);//用户查询错误
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
