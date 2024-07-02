package cn.cyberpeace.mp.frontadmin.filter;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.frontadmin.constant.AdminErrorCode;
import cn.cyberpeace.mp.frontadmin.service.SysCtlService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @ClassName SysAdminFilter
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see OncePerRequestFilter
 */
@Slf4j
public class SysAdminFilter extends OncePerRequestFilter {

    private ObjectMapper mapper = null;

    private SysCtlService sysCtlService;

    @Autowired
    private MappingJackson2HttpMessageConverter converter;

    /**
     * SysAdminFilter
     *
     * @param sysCtlService
     * @param converter
     */
    public SysAdminFilter(SysCtlService sysCtlService, MappingJackson2HttpMessageConverter converter) {
        this.sysCtlService = sysCtlService;
        this.converter = converter;
    }

    /**
     * doFilterInternal
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (sysCtlService.isSystemTimestampRollback()) {
            log.error("systime rollback error！");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            ServiceResult serviceResult = ResultCommon.fail(AdminErrorCode.SYS_TIMESTAMP_ROLLBACK);
            mapper = converter.getObjectMapper();

            PrintWriter writer = response.getWriter();
            mapper.writeValue(writer, serviceResult.asResult());
            writer.flush();
            return;
        }

        // todo 拦截license失效？

        filterChain.doFilter(request, response);

    }

}
