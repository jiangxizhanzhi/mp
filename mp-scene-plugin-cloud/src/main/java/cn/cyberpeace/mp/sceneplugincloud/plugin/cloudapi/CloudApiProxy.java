package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualRouterDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualSwitchDeleteData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;


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
                    
                    Thread.sleep(1000);
                } catch (Exception e) {

                }
            }
        }
        if (success) {
            return result;
        } else {
            try {
                var config = BeanCommon.getBean(CloudAppConfig.class);
                if (Boolean.TRUE.equals(config.getEnableForceDelete()) && method.getName().equals("delete")) {
                    if (this.target instanceof VirtualSwitch) {
                        VirtualSwitch vs = (VirtualSwitch) this.target;
                        result = vs.forceDelete((VirtualSwitchDeleteData) objects[0]);
                        success = true;
                    } else if (this.target instanceof VirtualRouter) {
                        VirtualRouter vr = (VirtualRouter) this.target;
                        result = vr.forceDelete((VirtualRouterDeleteData) objects[0]);
                        success = true;
                    } else if (this.target instanceof VirtualMachine) {
                        VirtualMachine vm = (VirtualMachine) this.target;
                        result = vm.forceDelete((VirtualMachineDeleteData) objects[0]);
                        success = true;
                    } else if (this.target instanceof Container) {
                        Container container = (Container) this.target;
                        result = container.forceDelete((ContainerDeleteData) objects[0]);
                        success = true;
                    }
                }
            } catch (Exception e) {
                error = e;
            }
            if (success) {
                return result;
            }
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
            
            return true;
        }
        return false;
    }
}
