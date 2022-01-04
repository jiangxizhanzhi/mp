package cn.cyberpeace.mp.auth.extensions.security;

import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.extensions.security.common.JwtCommon;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.log.constant.LogConstant;
import cn.cyberpeace.mp.devops.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.devops.log.request.OperationLogRequest;
import cn.cyberpeace.mp.devops.log.service.OperateLogServiceFactory;
import cn.cyberpeace.mp.devops.log.utils.SnUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Slf4j
@Component
public class HttpLogoutSuccessHandler implements LogoutSuccessHandler {
    private final ObjectMapper mapper;

    private final UserService userService;

    @Autowired
    private OperateLogServiceFactory operateLogServiceFactory;

    @Autowired
    public HttpLogoutSuccessHandler(MappingJackson2HttpMessageConverter messageConverter, UserService userService) {
        this.mapper = messageConverter.getObjectMapper();
        this.userService = userService;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        UserModel user = JwtCommon.verifyToken(request);
        if (user != null) {
            var updateUser = new UserModel();
            updateUser.setId(user.getId());
            updateUser.initDefault("tokenSecret");
            userService.update(updateUser);
            recordLogoutLog(user, request);
        }

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        ServiceResult<Object> result = new ServiceResult<>(null);
        PrintWriter writer = response.getWriter();
        mapper.writeValue(writer, result.asResult());
        writer.flush();
    }

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
        operateLogServiceFactory.create(logModel, LocaleContextHolder.getLocale());
    }
}
