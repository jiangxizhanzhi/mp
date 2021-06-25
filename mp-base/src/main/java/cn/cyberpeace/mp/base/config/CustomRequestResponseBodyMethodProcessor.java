package cn.cyberpeace.mp.base.config;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义返回结果处理器
 */
@Slf4j
@Component
public class CustomRequestResponseBodyMethodProcessor extends RequestResponseBodyMethodProcessor implements HandlerMethodReturnValueHandler {

    public CustomRequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
       return super.resolveArgument(parameter,mavContainer,webRequest,binderFactory);
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        Method method = returnType.getMethod();

        if (method == null || method.getReturnType().isAssignableFrom(Exception.class)) {
            return false;
        }
        try {
            Constructor constructor = method.getReturnType().getDeclaredConstructor();
            if (constructor.newInstance() instanceof ServiceResult){
                return true;
            }
        } catch (Exception e) {
            log.info("========CustomRequestResponseBodyMethodProcessor实例化报错信息：{}",e.getMessage());
            return super.supportsParameter(returnType);
        }
        return super.supportsParameter(returnType);
    }

    /**
     * 特殊处理返回结果
     * @param returnValue
     * @param returnType
     * @param mavContainer
     * @param webRequest
     * @throws IOException
     * @throws HttpMediaTypeNotAcceptableException
     * @throws HttpMessageNotWritableException
     */
    @Override
    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest)
            throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {
        super.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
    }

    /**
     * 驼峰转下划线
     * @param map
     * @return
     */
    private Map<String,Object> toUnderlineCase(Map<String,Object> map){
        Map<String, Object> resultMap = new HashMap<>();
        if (CollectionUtils.isEmpty(map)){
            return resultMap;
        }
        map.forEach((k,v) -> resultMap.put(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, k), v));
        return resultMap;
    }

}