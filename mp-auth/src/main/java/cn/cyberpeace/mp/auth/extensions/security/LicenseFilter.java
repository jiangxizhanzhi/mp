package cn.cyberpeace.mp.auth.extensions.security;

import cn.cyberpeace.mp.auth.dto.activation.ActivationContent;
import cn.cyberpeace.mp.auth.extensions.security.common.JwtCommon;
import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetails;
import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetailsService;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.auth.service.sys.SysActivationService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.log.constant.LogConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

//@Component
@Slf4j
public class LicenseFilter extends OncePerRequestFilter {

    private static final PathMatcher MATCHER = new AntPathMatcher();

    private ObjectMapper mapper = null;

    @Autowired
    private SysActivationService sysActivationService;

    @Autowired
    private MappingJackson2HttpMessageConverter converter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        if (MATCHER.match("/api/mp/auth/**", uri)) {

            ActivationContent activationContent = sysActivationService.status();
            if (Objects.isNull(activationContent)) {
                log.error("未查询到激活信息！");
                response.setStatus(HttpServletResponse.SC_OK);
                ServiceResult serviceResult = ServiceResult.fail("查询激活信息失败！");
                mapper = converter.getObjectMapper();

                PrintWriter writer = response.getWriter();
                mapper.writeValue(writer, serviceResult.asResult());
                writer.flush();
            }

            // 校验激活状态，日期等
            DateTime expiredDate = DateTime.parse(activationContent.getExpiredDate(), DateTimeFormat.forPattern("yyyy-MM-dd"));
            DateTime now = DateTime.now();
            if (expiredDate.compareTo(now) >= 0) {
                log.error("激活状态已经过期！");
            }
        }

        filterChain.doFilter(request, response);

    }

}
