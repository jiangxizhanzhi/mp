package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @Description
 * @Author maojun
 * @Date 2022/6/10
 */
@Slf4j
public class CloudApiProxy<T> implements MethodInterceptor {
    private Integer retryTimes;

    private T target;

    public CloudApiProxy(T target) {
        this.target = target;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        retryTimes = BeanCommon.getBean(CloudAppConfig.class).getDeleteRetryTimes();
        Object result = null;
        boolean success = false;
        Exception error = null;
        for (int i = 0; i < retryTimes; i++) {
            log.info("try {}::{} at {} times", target.getClass().getName(), method.getName(), i + 1);
            try {
                result = method.invoke(target, objects);
                success = true;
            } catch (Exception e) {
                error = e;
            }
            if (success) {
                break;
            } else {
                try {
                    // 删除失败等待1秒再次尝试
                    Thread.sleep(1000);
                } catch (Exception e) {

                }
            }
        }
        if (success) {
            return result;
        } else {
            if (this.excludeDeleteException(error)) {
                return true;
            } else {
                throw error;
            }
        }
    }

    private boolean excludeDeleteException(Exception runtimeException) {
        if (runtimeException == null) {
            return true;
        }
        Boolean ignoreDeleteException = BeanCommon.getBean(CloudAppConfig.class).getIgnoreDeleteException();
        if (Boolean.TRUE.equals(ignoreDeleteException)) {
            return true;
        }
        if (StringUtils.isNotEmpty(runtimeException.getMessage()) && runtimeException.getMessage().contains("could not be found")) {
            // 删除时已不存在的错误可以视为删除成功
            return true;
        }
        return false;
    }
}
