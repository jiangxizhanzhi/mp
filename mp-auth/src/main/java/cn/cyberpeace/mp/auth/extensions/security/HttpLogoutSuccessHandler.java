 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HttpLogoutSuccessHandler.java.
 // File size 4867.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.extensions.security;

import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.extensions.security.common.JwtCommon;
import cn.cyberpeace.mp.auth.extensions.security.event.UserEventPublisher;
import cn.cyberpeace.mp.auth.extensions.security.event.UserLogoutEvent;
import cn.cyberpeace.mp.auth.model.TokenModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.TokenMapper;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.impl.ThirdSsoService;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.log.constant.LogConstant;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.log.request.OperationLogRequest;
import cn.cyberpeace.mp.log.service.OperateLogService;
import cn.cyberpeace.mp.log.utils.SnUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

 /**
  * HTTP注销成功处理程序
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Slf4j
@Component
public class HttpLogoutSuccessHandler implements LogoutSuccessHandler {
     /**
      * 映射器
      */
     private final ObjectMapper mapper;

     /**
      * 用户服务
      */
     private final UserService userService;

     /**
      * 操作日志服务
      */
     @Autowired
    private OperateLogService operateLogService;

     /**
      * 身份验证配置
      */
     @Autowired
    private AuthConfig authConfig;

     /**
      * 用户事件发布者
      */
     @Autowired
    private UserEventPublisher userEventPublisher ;

     /**
      * HTTP注销成功处理程序
      *
      * @param messageConverter 消息转换器
      * @param userService      用户服务
      */
     @Autowired
    public HttpLogoutSuccessHandler(MappingJackson2HttpMessageConverter messageConverter, UserService userService) {
        this.mapper = messageConverter.getObjectMapper();
        this.userService = userService;
    }

     /**
      * 登出成功
      *
      * @param request        请求
      * @param response       响应
      * @param authentication 身份验证
      * @throws IOException ioexception
      */
     @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

         // 调用第三方接口退出登录
         log.info("log out start ----------");
         ThirdSsoService thirdSsoService = BeanCommon.getBean(ThirdSsoService.class);
         // 取本系统的jwt token ，到数据库里置换下第三方的login_token ， 有的话就调用第三方接口退出下登录
         var token = JwtCommon.getToken(request);
         log.info("log out , token is {} ----------", token);
         TokenMapper tokenMapper = BeanCommon.getBean(TokenMapper.class);
         var tokenModel = tokenMapper.first(new HumanCondition().filterEqual(TokenModel::getJwt, token)
                                                    .orderBy(TokenModel::getPk, true));
         log.info("log out , find token model {} ---------- ", JsonCommon.stringify(tokenModel));
         if (tokenModel != null) {
             thirdSsoService.logout(tokenModel.getLogonToken());
         }

        UserModel user = null;
        try {
            user = JwtCommon.verifyToken(request);
        } catch (Exception e) {
            log.error("logout verify token error for {}", e.getMessage());
        }
        String userName = null;
        if (Objects.nonNull(user)) {
            if (Objects.nonNull(authConfig.getLogoutResetSecret()) && authConfig.getLogoutResetSecret()) {
                if (!StringUtils.equals(user.getId(), authConfig.getLogoutKeepSecretUser())) {
                    // 退出是否重置秘钥
                    var updateUser = new UserModel();
                    updateUser.setId(user.getId());
                    updateUser.initDefault("tokenSecret");
                    userService.update(updateUser);
                }
            }
            userName = user.getName();
            recordLogoutLog(user, request);
            userEventPublisher.publishEvent(new UserLogoutEvent(JsonCommon.beanToMap(user)));
        }

        if (StringUtils.isNotBlank(userName)) {
            log.info("user:{} has logout!", userName);
        }

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        ServiceResult<Object> result = new ServiceResult<>(null);
        PrintWriter writer = response.getWriter();
        mapper.writeValue(writer, result.asResult());
        writer.flush();
    }

     /**
      * 记录登出日志
      *
      * @param user    用户
      * @param request 请求
      */
     private void recordLogoutLog(UserModel user, HttpServletRequest request) {
        OperationLogRequest logModel = new OperationLogRequest();
        //操作类型
        logModel.setOpModule(OpModuleEnums.ACCOUNT.ordinal());
        logModel.setOpType(OpTypeEnums.LOGOUT.ordinal());
        logModel.setOpTarget(MessageCommon.get(OpTargetEnums.USER.getCode()));
        //操作主题
        logModel.setTitle(MessageCommon.get(AuthBasei18nCode.LOGOUT));
        //操作描述
        logModel.setDescription(MessageCommon.get(AuthBasei18nCode.LOGOUT));

        //操作用户
        logModel.setUserId(user.getId());
        logModel.setUserName(user.getUsername());
        logModel.setUserType(LogConstant.USER_TYPE_USER);

        logModel.setOpIp(SnUtils.getRealRemoteIp(request));

        // 记录登出日志
        operateLogService.create(logModel);
    }
}
