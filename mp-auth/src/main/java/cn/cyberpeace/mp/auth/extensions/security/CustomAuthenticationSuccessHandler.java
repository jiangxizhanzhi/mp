package cn.cyberpeace.mp.auth.extensions.security;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.dto.login.LoginResp;
import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetails;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.fields.UserFields;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final ObjectMapper mapper;

    @Autowired
    public CustomAuthenticationSuccessHandler(MappingJackson2HttpMessageConverter messageConverter) {
        this.mapper = messageConverter.getObjectMapper();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        UserModel user = null;
        ServiceResult<Object> result;

        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthUserDetails) {
            user = ((AuthUserDetails) principal).getUser();
        }

        if (user != null) {
            String jwt = JwtUtils.generateToken(new JwtUtils.UserInfo(user.getId(), user.getUsername(), user.getTokenSecret()));
            ObjResult<UserModel> userResult = new ObjResult(user, new UserFields().getAvailableFields());
            LoginResp loginResp = new LoginResp();
            loginResp.setUser(userResult.asResult());
            loginResp.setToken(jwt);
            result = ServiceResult.builder().source(loginResp).build();

            if (user.getInitCheck() == UserModel.INT_CHECK_YES) {
                result = ServiceResult.builder().source(loginResp).serviceCode(AuthErrorCode.NEED_UPDATE_CREDENTIALS).build();
            }

        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result = new ServiceResult<>(null, BaseErrorCode.ERROR);
        }

        PrintWriter writer = response.getWriter();
        mapper.writeValue(writer, result.asResult());
        writer.flush();
    }
}
