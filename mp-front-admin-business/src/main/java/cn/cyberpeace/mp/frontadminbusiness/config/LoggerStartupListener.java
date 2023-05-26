package cn.cyberpeace.mp.frontadminbusiness.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import org.springframework.stereotype.Component;

/**
 * @BelongsPackage: cn.cyberpeace.mp.log
 * @Author: hhuang
 * @CreateTime: 2023/05/26  10:01
 * @Description: 系统启动时日志处理
 * @Version: 1.0
 */
public class LoggerStartupListener extends ContextAwareBase
    implements LoggerContextListener, LifeCycle {

    @Override
    public boolean isResetResistant() {
        return false;
    }

    @Override
    public void onStart(LoggerContext context) {}

    @Override
    public void onReset(LoggerContext context) {}

    @Override
    public void onStop(LoggerContext context) {
    }

    @Override
    public void onLevelChange(Logger logger, Level level) {
    }

    private boolean started = false;
    @Override
    public void start() {
        if (started) {
            return;
        }
        Context context = getContext();
        //TODO 查询系统配置对应日志保留天数，设置天数
        context.putProperty("LOG_FILE_MAX_HISTORY", "10");
        started = true;
    }

    @Override
    public void stop() {}

    @Override
    public boolean isStarted() {
        return false;
    }
}
