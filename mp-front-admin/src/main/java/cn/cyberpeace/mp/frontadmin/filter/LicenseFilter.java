package cn.cyberpeace.mp.frontadmin.filter;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.frontadmin.config.FrontAdminConfig;
import cn.cyberpeace.mp.frontadmin.constant.AdminErrorCode;
import cn.cyberpeace.mp.frontadmin.response.activation.ActivationResponse;
import cn.cyberpeace.mp.frontadmin.service.sys.SysActivationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

@Slf4j
public class LicenseFilter extends OncePerRequestFilter {

    private static final PathMatcher MATCHER = new AntPathMatcher();

    private ObjectMapper mapper = null;


    
    private SysActivationService sysActivationService;
    private FrontAdminConfig frontAdminConfig;



    
    private MappingJackson2HttpMessageConverter converter;

    public LicenseFilter(FrontAdminConfig frontAdminConfig) {
        this.frontAdminConfig = frontAdminConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("activation enabled: {}", frontAdminConfig.isActivEnable());
        if (frontAdminConfig.isActivEnable()) {
            String uri = request.getRequestURI();

            if (MATCHER.match("/api/mp/auth/login", uri)) {
                boolean activationFailed = false;
                ActivationResponse activationResponse = sysActivationService.status();
                if (Objects.isNull(activationResponse)) {
                    log.error("Can not find activation status！");
                    activationFailed = true;
                }

                
                DateTime expiredDate = DateTime.parse(activationResponse.getExpiredDate(), DateTimeFormat.forPattern("yyyy-MM-dd"));
                DateTime now = DateTime.now();
                if (expiredDate.compareTo(now) >= 0) {
                    log.error("Activation is Expired！");
                    activationFailed = true;
                }

                if(activationFailed) {
                    log.error("Activation status is error！");
                    response.setStatus(HttpServletResponse.SC_OK);
                    
                    ServiceResult serviceResult = ResultCommon.fail(AdminErrorCode.SYS_ACTIVATION_ERROR);
                    mapper = converter.getObjectMapper();

                    PrintWriter writer = response.getWriter();
                    mapper.writeValue(writer, serviceResult.asResult());
                    writer.flush();
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);

    }

}
