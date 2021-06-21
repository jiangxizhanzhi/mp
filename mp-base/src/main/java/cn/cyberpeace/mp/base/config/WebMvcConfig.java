package cn.cyberpeace.mp.base.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 统一返回结果风格，例如：驼峰命名字段转换成蛇形命名
 */
@Configuration
public class WebMvcConfig  {

    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    /**
     * 自定义的返回值处理器
     */
    private CustomRequestResponseBodyMethodProcessor customRequestResponseBodyMethodProcessor;

    /**
     * 构造注入
     * @param requestMappingHandlerAdapter
     * @param customRequestResponseBodyMethodProcessor
     */
    public WebMvcConfig(RequestMappingHandlerAdapter requestMappingHandlerAdapter,
                        CustomRequestResponseBodyMethodProcessor customRequestResponseBodyMethodProcessor) {
        this.requestMappingHandlerAdapter = requestMappingHandlerAdapter;
        this.customRequestResponseBodyMethodProcessor = customRequestResponseBodyMethodProcessor;
    }

    /**
     * 初始化RequestMappingHandlerAdapter，注入自定义处理器
     */
    @PostConstruct
    public void init() {
        final List<HandlerMethodReturnValueHandler> newHandlers = new LinkedList<>();
        final List<HandlerMethodReturnValueHandler> originalHandlers = requestMappingHandlerAdapter.getReturnValueHandlers();
        if (Objects.nonNull(originalHandlers)) {
            newHandlers.addAll(originalHandlers);
            // 获取处理器应处于的位置，需要在RequestResponseBodyMethodProcessor之前
            final int index = obtainValueHandlerPosition(originalHandlers, RequestResponseBodyMethodProcessor.class);
            newHandlers.add(index, customRequestResponseBodyMethodProcessor);
        } else {
            newHandlers.add(customRequestResponseBodyMethodProcessor);
        }
        requestMappingHandlerAdapter.setReturnValueHandlers(newHandlers);
    }

    /**
     * 获取让自定义处理器生效应处于的位置
     *
     * @param originalHandlers 已经添加的返回值处理器
     * @param handlerClass     返回值处理器的类型
     * @return 自定义处理器需要的位置
     */
    private int obtainValueHandlerPosition(final List<HandlerMethodReturnValueHandler> originalHandlers, Class<?> handlerClass) {
        for (int i = 0; i < originalHandlers.size(); i++) {
            final HandlerMethodReturnValueHandler valueHandler = originalHandlers.get(i);
            if (handlerClass.isAssignableFrom(valueHandler.getClass())) {
                return i;
            }
        }
        return -1;
    }
}
