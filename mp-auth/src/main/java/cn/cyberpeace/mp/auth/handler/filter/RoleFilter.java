package cn.cyberpeace.mp.auth.handler.filter;

import cn.cyberpeace.mp.auth.annotation.UrlKey;
import cn.cyberpeace.mp.auth.handler.AbstractRoleHandler;
import cn.cyberpeace.mp.auth.handler.RoleHandler;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @BelongsPackage: cn.cyberpeace.mp.auth.filter
 * @Author: hhuang
 * @CreateTime: 2023/03/25  10:21
 * @Description: 角色过滤器
 * @Version: 1.0
 */
@Slf4j
public class RoleFilter implements Filter {

    //异常日志格式
    private static final String LOGGER_FORMAT = "System error: {}, detail: {}";

    private static final String FORM_CONTENT_TYPE = "multipart/form-data";

    //url处理器映射
    private final Map<String, Class<? extends AbstractRoleHandler>> urlHandlerMap = new ConcurrentHashMap<>();

    private ObjectMapper mapper;

    public RoleFilter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("RoleFilter init...");
        //初始化核心接口类及URL与类的关系
        Reflections reflections = new Reflections("cn.cyberpeace.mp.auth.handler");
        //AbstractRoleHandler所有子类
        Set<Class<? extends AbstractRoleHandler>> roleHandlers = reflections.getSubTypesOf(AbstractRoleHandler.class);
        roleHandlers.forEach((subRoleHandlerClass) -> {
            var urlKeyAnnotation = subRoleHandlerClass.getAnnotation(UrlKey.class);
            if (urlKeyAnnotation == null) {
                return;
            }
            String[] urlKeys = urlKeyAnnotation.value();
            for (String urlKey: urlKeys) {
                urlHandlerMap.put(urlKey, subRoleHandlerClass);
            }
        });
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request,response);
            return;
        }
        //通过过滤对应url前缀调用对应的接口方法处理请求
        //处理request流数据不可重复读
        HttpServletRequest req = new ContentCachingRequestWrapper((HttpServletRequest) request);
        String servletPath = req.getServletPath();
        Class<? extends AbstractRoleHandler> roleHandlerClass = null;
        Set<String> urlHandlerSet = urlHandlerMap.keySet();
        for (String url :urlHandlerSet) {
            if(servletPath.startsWith(url)){
                roleHandlerClass = urlHandlerMap.get(url);
                break;
            }
        }
        if(Objects.isNull(roleHandlerClass)){
            chain.doFilter(request,response);
            return;
        }
        //调用角色相关处理器
        RoleHandler roleHandler = BeanCommon.getBean(roleHandlerClass);
        if(!Objects.isNull(roleHandler)){
            try {
                roleHandler.handle(req);
            } catch (Exception e) {
                if(e instanceof MpRuntimeException){
                    exceptionHandler(e,response);
                    return;
                }
            }
        }
        //判断传递request
        String contentType = request.getContentType();
        if (contentType != null && contentType.contains(FORM_CONTENT_TYPE)) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(req, response);
        }
    }

    /**
     * @description: 处理过滤器捕获的异常响应
     * @param e MpRuntimeException
     * @param response ServletResponse
     * @throws
     * @return void
     */
    private void exceptionHandler(Exception e,ServletResponse response) throws IOException{
        MpRuntimeException ex = (MpRuntimeException) e;
        log.error(LOGGER_FORMAT, ex.getMessage(), ex.getDetail());
        var serviceCode = ex.getServiceCode();
        if (serviceCode == null) {
            serviceCode = BaseErrorCode.ERROR;
        }
        var detail = StringUtils.isEmpty(ex.getDetail()) ? ex.getMessage() : ex.getDetail();
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = resp.getWriter();
        mapper.writeValue(writer, new ServiceResult<>(null, serviceCode, detail).asResult());
        writer.flush();
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
