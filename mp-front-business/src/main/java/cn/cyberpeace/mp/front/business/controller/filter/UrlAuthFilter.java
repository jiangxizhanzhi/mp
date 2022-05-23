package cn.cyberpeace.mp.front.business.controller.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: url拦截器
 * @Author zhouxinghuai
 * @Date：2022/5/19 16:43
 */
@Slf4j
public class UrlAuthFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("UrlAuthFilter...");

        /*if (sysCtlService.isSystemTimestampRollback()) {
            log.error("systime rollback error！");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            ServiceResult serviceResult = ServiceResult.fail(AdminErrorCode.SYS_TIMESTAMP_ROLLBACK);
            mapper = converter.getObjectMapper();

            PrintWriter writer = response.getWriter();
            mapper.writeValue(writer, serviceResult.asResult());
            writer.flush();
            return;
        }*/

        // todo 拦截license失效？

        filterChain.doFilter(request, response);

    }


}
