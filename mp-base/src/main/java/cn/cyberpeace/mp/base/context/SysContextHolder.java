package cn.cyberpeace.mp.base.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * @Description 用于保存业务线程的上下文信息
 * @Author maojun
 * @Date 2021/8/31
 */
public class SysContextHolder {
    private static Logger log = LoggerFactory.getLogger(SysContextHolder.class);

    private static ThreadLocal<SysContext> contextLocal = new ThreadLocal<>();

    public static SysContext get() {
        if (contextLocal.get() == null) {
            contextLocal.set(new BaseSysContext());
        }
        return contextLocal.get();
    }

    public static void set(SysContext sysContext) {
        if (Optional.ofNullable(contextLocal.get()).isPresent()) {
            // 如果重复设置上下文，打印错误日志，但是不影响后续处理
            log.info("BaseSysContext has already been created!");
        }
        contextLocal.set(sysContext);
    }

    public static void clear() {
        contextLocal.remove();
    }
}