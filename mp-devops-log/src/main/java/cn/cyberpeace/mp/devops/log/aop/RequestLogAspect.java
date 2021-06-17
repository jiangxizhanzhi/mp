package cn.cyberpeace.mp.devops.log.aop;

import cn.cyberpeace.mp.devops.log.annotation.OperateLog;
import cn.cyberpeace.mp.devops.log.enums.SiteTypeEnums;
import cn.cyberpeace.mp.devops.log.model.OperationLogModel;
import cn.cyberpeace.mp.devops.log.service.OperateLogServiceFactory;
import cn.cyberpeace.mp.devops.log.utils.SnUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;
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
                    log.info("【返回增强】>> 请求地址URL: {},  方法名称method: {}, 结果: {}, 耗费时长: {},状态码: {}", uri, methodName, truncString(result.toString(), maxResultLength), System.currentTimeMillis() - startTime, response.getStatus());
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
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = servletRequestAttributes.getRequest();

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
            operationLogModel.setOpModule(operateLog.module());
            //操作类型
            operationLogModel.setOpType(operateLog.type());
            //操作主题
            operationLogModel.setTitle(operateLog.title());
            //操作描述
            operationLogModel.setDescription(operateLog.desc());
            //用户ID todo 暂时写死，后期从request对象获取
            operationLogModel.setUserId("1");
            //用户名称
            operationLogModel.setUserName("admin");
            //记录操作时间
            operationLogModel.setOpTime(LocalDateTime.now());
            //请求IP
            operationLogModel.setOpIp(SnUtils.getRealRemoteIp(request));

            operateLogServiceFactory.create(operationLogModel);

        } catch (Exception e) {
            log.info("======");
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
     * 获取请求body
     * @param request
     * @return
     */
    private String getRequestBody(HttpServletRequest request) {
        try {
            if (request.getContentType() != null
                    && request.getContentType().contains("multipart/form-data")) {

                // 过滤掉文件上传
                return "multipart/form-data, contentLength is: " + request.getContentLength();
            }

            ServletInputStream servletInputStream = request.getInputStream();
            byte[] bytes;
            if (servletInputStream.isFinished()) { // 判断request流是否读取过
                bytes = request.getInputStream().readAllBytes();
            } else {
                bytes = convertInputStreamToBytes(servletInputStream);
            }

            bytes = bytes == null ? new byte[0] : bytes;

            String charset = request.getCharacterEncoding();
            if (charset != null) {
                return new String(bytes, charset);
            } else {
                return new String(bytes);
            }
        } catch (Exception ex) {
            log.warn("http接口日志参数体封装失败", ex);
        }

        return "";
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


    /**
     * 密码字段处理, 字符串加水印
     */
    private String blurPasswordField(String json) {
        if (StringUtils.isBlank(json)) {
            return json;
        }

        try {
            Map<Object, Object> jsonMap = objectMapper.readValue(json, new TypeReference<>() {});
            return objectMapper.writeValueAsString(blurPasswordField(jsonMap));
        } catch (IOException e) {
            return json;
        }
    }

    /**
     * 密码字段处理, 字符串加水印
     */
    @SuppressWarnings("unchecked")
    public Map<Object, Object> blurPasswordField(Map<Object, Object> jsonMap) {
        Map<Object, Object> resultMap = new LinkedHashMap<>();

        for (Map.Entry<Object, Object> entry : jsonMap.entrySet()) {
            String key = String.valueOf(entry.getKey());

            Object value = entry.getValue();

            if (passwordPattern.matcher(key).matches()) {
                String originPwd = value == null ? null : String.valueOf(value);
                resultMap.put(entry.getKey(), blurString(originPwd));
            } else {
                if (value instanceof Map) {
                    Map<Object, Object> tmpResultMap = blurPasswordField((Map<Object, Object>) value);
                    resultMap.put(entry.getKey(), tmpResultMap);
                } else if (value instanceof List) {
                    List<Object> tmpList = new ArrayList<>();
                    for (Object item : (List) value) {
                        if (item instanceof Map) {
                            Map<Object, Object> tmpResultMap = blurPasswordField((Map<Object, Object>) item);
                            tmpList.add(tmpResultMap);
                        } else {
                            tmpList.add(item);
                        }
                    }
                    resultMap.put(entry.getKey(), tmpList);
                } else {
                    resultMap.put(entry.getKey(), value);
                }
            }
        }

        return resultMap;
    }

    /**
     * 字符串加水印处理
     *
     * <pre>
     *     长度在9位以下的字符串,处理成首字母加*号的方式(*号个数等于字符串长度-1);
     * 长度在9位及以上的字符串, 将最后8位替换成*号。
     * 譬如:
     * 123处理后1**
     * 123456789处理后1********
     * 1234567890处理后12********
     * </pre>
     *
     * @param source 原始字符串
     */
    private static String blurString(String source) {
        if (StringUtils.isBlank(source)) {
            return source;
        }

        if (source.length() < 9) {
            return source.substring(0, 1) + repeat('*', source.length() - 1);
        } else {
            return source.substring(0, source.length() - 8) + repeat('*', 8);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static String repeat(char ch, int repeat) {
        char[] buf = new char[repeat];
        for (int i = repeat - 1; i >= 0; i--) {
            buf[i] = ch;
        }
        return new String(buf);
    }

    private byte[] convertInputStreamToBytes(InputStream input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int n;

        try {
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
        } catch (IOException e) {
            if (log.isDebugEnabled()) {
                log.info("convertInputStreamToBytes failed.", e);
            }
        }
        return output.toByteArray();
    }
}
