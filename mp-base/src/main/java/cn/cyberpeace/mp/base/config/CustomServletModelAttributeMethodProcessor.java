package cn.cyberpeace.mp.base.config;

import cn.hutool.core.lang.Assert;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import javax.servlet.ServletRequest;

/**
 * 自定义请求参数映射实体处理器，主要解决将前端传递的下划线格式的字段自动转为驼峰格式
 */
public class CustomServletModelAttributeMethodProcessor extends ServletModelAttributeMethodProcessor {

    public CustomServletModelAttributeMethodProcessor(boolean annotationNotRequired) {
        super(annotationNotRequired);
    }

    @Override
    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
        ServletRequest servletRequest = request.getNativeRequest(ServletRequest.class);
        Assert.state(servletRequest != null,"No servletRequest");
        ServletRequestDataBinder servletRequestDataBinder = (ServletRequestDataBinder) binder;
        //更换请求参数绑定器
        CustomServletRequestDataBinder customServletRequestDataBinder = new CustomServletRequestDataBinder(servletRequestDataBinder.getTarget());
        customServletRequestDataBinder.setConversionService(servletRequestDataBinder.getConversionService());
        customServletRequestDataBinder.bind(servletRequest);


    }
}