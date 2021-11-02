package cn.cyberpeace.mp.base.context.http;

import cn.cyberpeace.mp.base.context.SysContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/27
 */
public class HttpContextFilter extends OncePerRequestFilter {

    private final static String X_REQUEST_ID = "X-Request-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            IHttpContext sysContext = new HttpContext(request.getHeader(X_REQUEST_ID));
            SysContextHolder.set(sysContext);
            filterChain.doFilter(request, response);
        } finally {
            SysContextHolder.clear();
        }
    }
}
