package cn.cyberpeace.mp.log.aop;

import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.config.LogConfig;
import cn.cyberpeace.mp.log.service.OperateLogService;
import cn.cyberpeace.mp.log.utils.SnUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;


@Aspect
@Slf4j
@Component
public class RequestLogAspect {

    public static final String DEFAULT_PASSWORD_FIELD_REGEX = "((\\w+)Password|((\\w+)_)?password)((_|[A-Z])(\\w+)?)?|((\\w+)Pwd|((\\w+)_)?pwd)((_|[A-Z])(\\w+)?)?";

    private Pattern passwordPattern = Pattern.compile(DEFAULT_PASSWORD_FIELD_REGEX);

    private static final String SUB_SUFFIX = "...(source length is %s)";


    @Autowired
    private LogConfig logConfig;

    private AntPathMatcher pathMatcher = new AntPathMatcher();

    private int maxResultLength = 512;

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private OperateLogService operateLogService;

    
    @Pointcut("execution(* cn.cyberpeace.mp..*Controller.*(..)) || @annotation(cn.cyberpeace.mp.log.annotation.OperateLog)")
    public void pointcut() {}


    @Around(value = "pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        Signature signature = joinPoint.getSignature();

        
        long startTime = System.currentTimeMillis();

        
        Object result;

        
        String methodName = signature.getName();

        
        Object[] args =  joinPoint.getArgs();

        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) attributes;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        HttpServletResponse response =  servletRequestAttributes.getResponse();

        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        String uri = UUID.randomUUID() + "|" + request.getRequestURI();

        if (isExcludePath(requestUri) || isExcludeMethod(method)) {
            result = joinPoint.proceed();
        }else {
            try {

                if (log.isDebugEnabled()) {
                    log.debug("【前置增强】>>  来源IP: {}, 请求地址URL: {}, 头部header: {}, 全路径类名class: {},方法名称method: {}, 参数信息params: {} ", SnUtils.getRealRemoteIp(request), uri, SnUtils.getRequestHeaders(request), signature.getDeclaringTypeName(), methodName, Arrays.asList(args));
                }
                result = joinPoint.proceed();
                if (log.isDebugEnabled()) {
                    log.debug("【返回增强】>> 请求地址URL: {},  方法名称method: {}, 结果: {}, 耗费时长: {},状态码: {}", uri, methodName,Objects.isNull(result) ? null : truncString(result.toString(), maxResultLength), System.currentTimeMillis() - startTime, response.getStatus());
                }
            } catch (Throwable e) {
                String exMsg = SnUtils.getExceptionMessage(e);
                if (log.isDebugEnabled()) {
                    log.debug("【异常增强】>> 请求地址URL: {},  全路径类名class: {} 发生异常, 方法名称method: {}, exMsg: {}", uri, signature.getDeclaringType(), methodName, exMsg);
                }
                throw e;
            }
            if (log.isDebugEnabled()) {
                log.debug("【后置增强】>> 请求地址URL: {} 结束啦 >>>>>>>>>", uri);
            }
        }
        return  result;
    }

    
    @AfterReturning(returning = "result", pointcut = "pointcut()")
    public void afterReturning(JoinPoint joinPoint,Object result) {
        HttpServletRequest request = ServletUtils.getRequest();

        try {

            String requestUri = request.getRequestURI();
            String method = request.getMethod();

            if (isExcludePath(requestUri) || isExcludeMethod(method)) {
                return;
            }

            OperateLog operateLog = getAnnotation(joinPoint,OperateLog.class);

            if (Objects.isNull(operateLog)){
                return;
            }
            operateLogService.create(operateLog, request);

        } catch (Exception e) {
            log.error("====插入操作日志异常:{}==", ExceptionUtils.getStackTrace(e));
        }
    }

    
    private String truncString(String str, int maxLength) {
        if (str == null) {
            return null;
        }

        if (str.length() > maxLength) {
            String subSuffix = String.format(SUB_SUFFIX, str.length());
            return str.substring(0, maxLength - subSuffix.length()) + subSuffix;
        }

        return str;
    }


    
    private <T extends Annotation> T getAnnotation(JoinPoint joinPoint, Class<T> annotation) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (method != null) {
            return method.getAnnotation(annotation);
        }
        return null;
    }

    
    private Boolean isExcludePath(String requestUri) {
        List<String> excludePatterns = logConfig.getExcludeUrls();
        if (CollectionUtils.isEmpty(excludePatterns)) {
            return Boolean.FALSE;
        }
        for (String pattern : excludePatterns) {
            if (pathMatcher.match(pattern.trim(), requestUri)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    
    private boolean isExcludeMethod(String method) {
        List<String> excludeMethods = logConfig.getExcludeMethods();
        if (CollectionUtils.isEmpty(excludeMethods)) {
            return Boolean.FALSE;
        }
        for (String excludeMethod : excludeMethods) {
            if (excludeMethod.equalsIgnoreCase(method)) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }



}
