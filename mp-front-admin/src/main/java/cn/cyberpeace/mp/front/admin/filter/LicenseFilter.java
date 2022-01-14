package cn.cyberpeace.mp.front.admin.filter;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.front.admin.config.ActivationConfig;
import cn.cyberpeace.mp.front.admin.constant.AdminErrorCode;
import cn.cyberpeace.mp.front.admin.dto.activation.ActivationResponse;
import cn.cyberpeace.mp.front.admin.service.sys.SysActivationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
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

//@Component
@Slf4j
public class LicenseFilter extends OncePerRequestFilter {

    private static final PathMatcher MATCHER = new AntPathMatcher();

    private ObjectMapper mapper = null;

    @Autowired
    private SysActivationService sysActivationService;

    @Autowired
    private ActivationConfig activationConfig;

    @Autowired
    private MappingJackson2HttpMessageConverter converter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("authConfig.isEnable: {}", activationConfig.isEnable());
        if (activationConfig.isEnable()) {
            String uri = request.getRequestURI();

            if (MATCHER.match("/api/mp/auth/**", uri)) {
                boolean activationFailed = false;
                ActivationResponse activationResponse = sysActivationService.status();
                if (Objects.isNull(activationResponse)) {
                    log.error("Can not find activation status！");
                    activationFailed = true;
                }

                // 校验激活状态，日期等
                DateTime expiredDate = DateTime.parse(activationResponse.getExpiredDate(), DateTimeFormat.forPattern("yyyy-MM-dd"));
                DateTime now = DateTime.now();
                if (expiredDate.compareTo(now) >= 0) {
                    log.error("Activation is Expired！");
                    activationFailed = true;
                }

                if(activationFailed) {
                    log.error("Activation status is error！");
                    response.setStatus(HttpServletResponse.SC_OK);
                    // todo message format
                    ServiceResult serviceResult = ServiceResult.fail(AdminErrorCode.SYS_ACTIVATION_ERROR);
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
