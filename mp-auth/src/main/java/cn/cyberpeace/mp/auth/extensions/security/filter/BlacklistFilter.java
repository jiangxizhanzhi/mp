package cn.cyberpeace.mp.auth.extensions.security.filter;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.BlacklistModel;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.PathContainer;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@Slf4j
@Component
public class BlacklistFilter extends OncePerRequestFilter {
    @Resource
    private BlacklistModel blacklistModel;

    @Resource
    private BaseAppConfig baseAppConfig;

    private ObjectMapper mapper = null;

    @Resource
    private MappingJackson2HttpMessageConverter converter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (blacklistModel.isEnabled()) {
            String method = request.getMethod();
            String uri = request.getRequestURI();

            
            for (BlacklistModel.AccessItem item : blacklistModel.getPatterns()) {
                if (StringUtils.isEmpty(item.getMethod()) || method.equals(item.getMethod())) {
                    if (item.getPattern().matches(PathContainer.parsePath(uri))) {
                        denyAccess(response, uri);
                        return;
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private void denyAccess(HttpServletResponse response, String uri) throws IOException {
        log.error("access to {} was forbidden！", uri);
        response.setStatus(HttpServletResponse.SC_OK);
        ServiceResult serviceResult = ResultCommon.fail(AuthErrorCode.DEMO_ACCESS_ERROR);
        mapper = converter.getObjectMapper();

        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        mapper.writeValue(writer, serviceResult.asResult());
        writer.flush();
    }
}
