package cn.cyberpeace.mp.base.context.http;

import cn.cyberpeace.mp.base.context.SysContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description
 * @Author maojun
 * @Date 2021/8/31
 */
public class HttpContextHandlerInterceptor implements HandlerInterceptor {

    private final static String X_REQUEST_ID = "X-Request-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        IHttpContext sysContext = new HttpContext(request.getHeader(X_REQUEST_ID));
        SysContextHolder.set(sysContext);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        SysContextHolder.clear();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}