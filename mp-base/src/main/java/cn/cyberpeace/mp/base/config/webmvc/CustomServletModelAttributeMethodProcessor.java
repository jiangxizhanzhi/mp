package cn.cyberpeace.mp.base.config.webmvc;

import cn.hutool.core.lang.Assert;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import javax.servlet.ServletRequest;


public class CustomServletModelAttributeMethodProcessor extends ServletModelAttributeMethodProcessor {

    public CustomServletModelAttributeMethodProcessor(boolean annotationNotRequired) {
        super(annotationNotRequired);
    }

    @Override
    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
        ServletRequest servletRequest = request.getNativeRequest(ServletRequest.class);
        Assert.state(servletRequest != null,"No servletRequest");
        ServletRequestDataBinder servletRequestDataBinder = (ServletRequestDataBinder) binder;
        
        CustomServletRequestDataBinder customServletRequestDataBinder = new CustomServletRequestDataBinder(servletRequestDataBinder.getTarget());
        customServletRequestDataBinder.setConversionService(servletRequestDataBinder.getConversionService());
        customServletRequestDataBinder.bind(servletRequest);
    }
}
