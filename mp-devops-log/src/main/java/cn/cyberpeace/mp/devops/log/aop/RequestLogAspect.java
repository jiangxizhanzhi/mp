package cn.cyberpeace.mp.devops.log.aop;

import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.devops.log.annotation.OperateLog;
import cn.cyberpeace.mp.devops.log.constant.LogConstant;
import cn.cyberpeace.mp.devops.log.enums.SiteTypeEnums;
import cn.cyberpeace.mp.devops.log.model.OperationLogModel;
import cn.cyberpeace.mp.devops.log.service.OperateLogServiceFactory;
import cn.cyberpeace.mp.devops.log.utils.SnUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 记录请求日志的切面
 * @author wanggang
 */
@Aspect
@Slf4j
@Component
public class RequestLogAspect {

    public static final String DEFAULT_PASSWORD_FIELD_REGEX = "((\\w+)Password|((\\w+)_)?password|password)((_|[A-Z])(\\w+)?)?|((\\w+)Pwd|((\\w+)_)?pwd|pwd)((_|[A-Z])(\\w+)?)?";

    private Pattern passwordPattern = Pattern.compile(DEFAULT_PASSWORD_FIELD_REGEX);

    private static final String SUB_SUFFIX = "...(source length is %s)";

    /**
     * 不希望记录日志的URL Pattern, 如果有多个可以采用英文半角逗号(",")分隔; 单个Pattern语法支持AntPathMatcher匹配。
     */
    @Value("#{'${mp.log.exclude-uri}'.empty ? null : '${mp.log.exclude-uri}'.split(',')}")
    private List<String> excludePatterns;

    /**
     * 不需要记录日志的请求method。譬如通常可以忽略的method: OPTIONS
     */
    @Value("#{'${mp.log.exclude-method}'.empty ? null : '${mp.log.exclude-method}'.split(',')}")
    private List<String> excludeMethods;

    private AntPathMatcher pathMatcher = new AntPathMatcher();

    private int maxResultLength = 512;

    private static ObjectMapper objectMapper = new ObjectMapper();

    private OperateLogServiceFactory operateLogServiceFactory;

    @Autowired
    public RequestLogAspect(OperateLogServiceFactory operateLogServiceFactory) {
        this.operateLogServiceFactory = operateLogServiceFactory;
    }

    /**
     * 定义公共切面
     */
    @Pointcut("execution(* cn.cyberpeace.mp..*Controller.*(..)) || @annotation(cn.cyberpeace.mp.devops.log.annotation.OperateLog)")
    public void pointcut() {}


    @Around(value = "pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        Signature signature = joinPoint.getSignature();

        //请求开始时间
        long startTime = System.currentTimeMillis();

        //返回对象
        Object result;

        //方法名称
        String methodName = signature.getName();

        //参数
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

                if (log.isInfoEnabled()) {
                    log.info("【前置增强】>>  来源IP: {}, 请求地址URL: {}, 头部header: {}, 全路径类名class: {},方法名称method: {}, 参数信息params: {} ", SnUtils.getRealRemoteIp(request), uri, SnUtils.getRequestHeaders(request), signature.getDeclaringTypeName(), methodName, Arrays.asList(args));
                }
                result = joinPoint.proceed();
                if (log.isInfoEnabled()) {
                    log.info("【返回增强】>> 请求地址URL: {},  方法名称method: {}, 结果: {}, 耗费时长: {},状态码: {}", uri, methodName,Objects.isNull(result) ? null : truncString(result.toString(), maxResultLength), System.currentTimeMillis() - startTime, response.getStatus());
                }
            } catch (Throwable e) {
                String exMsg = SnUtils.getExceptionMessage(e);
                if (log.isDebugEnabled()) {
                    log.debug("【异常增强】>> 请求地址URL: {},  全路径类名class: {} 发生异常, 方法名称method: {}, exMsg: {}", uri, signature.getDeclaringType(), methodName, exMsg);
                }
                throw e;
            }
            if (log.isInfoEnabled()) {
                log.info("【后置增强】>> 请求地址URL: {} 结束啦 >>>>>>>>>", uri);
            }
        }
        return  result;
    }

    /**
     * 不抛异常时记录操作日志
     * @param joinPoint
     * @param result
     */
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

            OperationLogModel operationLogModel = new OperationLogModel();
            //站点类型
            operationLogModel.setSiteType(SiteTypeEnums.MP);
            //操作模块
            operationLogModel.setOpModule(operateLog.module().getDesc());
            //操作类型
            operationLogModel.setOpType(operateLog.type().getDesc());
            //操作对象
            operationLogModel.setOpTarget(operateLog.target());
            //操作主题
            operationLogModel.setTitle(operateLog.title());
            //操作描述
            operationLogModel.setDescription(operateLog.desc());
            //记录操作时间
            operationLogModel.setOpTime(LocalDateTime.now());
            //请求IP
            operationLogModel.setOpIp(SnUtils.getRealRemoteIp(request));

            //操作用户
            handleOperationUser(operationLogModel);

            operateLogServiceFactory.create(operationLogModel);

        } catch (Exception e) {
            log.info("====插入操作日志异常==");
        }
    }

    /**
     * 截取返回结果
     * @param str
     * @param maxLength
     * @return
     */
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


    /**
     * 获取指定注解
     * @param joinPoint
     * @param annotation
     * @param <T>
     * @return
     */
    private <T extends Annotation> T getAnnotation(JoinPoint joinPoint, Class<T> annotation) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (method != null) {
            return method.getAnnotation(annotation);
        }
        return null;
    }

    /**
     * 判断是否为排除的uri
     * @param requestUri 需判定的uri
     */
    private Boolean isExcludePath(String requestUri) {
        if (CollectionUtils.isEmpty(excludePatterns)) {
            return Boolean.FALSE;
        }
        for (String pattern : this.excludePatterns) {
            if (pathMatcher.match(pattern.trim(), requestUri)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 判断是否为排除的method
     * @param method request method(GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE)
     */
    private boolean isExcludeMethod(String method) {
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


    private void handleOperationUser(OperationLogModel operationLogModel) {
        HttpServletRequest request = ServletUtils.getRequest();
        Object userId = request.getAttribute(LogConstant.USER_ID_KEY);
        Object userName = request.getAttribute(LogConstant.USER_NAME_KEY);
        Object userType = request.getAttribute(LogConstant.USER_TYPE_KEY);

        if (Objects.nonNull(userId)) {
            operationLogModel.setUserId(userId.toString());
        }

        if (Objects.nonNull(userName)) {
            operationLogModel.setUserName(userName.toString());
        }

        if (Objects.nonNull(userType)) {
            operationLogModel.setUserType(userType.toString());
        }
    }

}
