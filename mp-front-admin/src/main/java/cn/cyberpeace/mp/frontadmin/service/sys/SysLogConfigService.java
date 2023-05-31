package cn.cyberpeace.mp.frontadmin.service.sys;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import cn.cyberpeace.mp.base.config.log.LogAsyncAppender;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @BelongsPackage: cn.cyberpeace.mp.frontadminbusiness.service
 * @Author: hhuang
 * @CreateTime: 2023/05/26  17:20
 * @Description: 系统日志Service
 * @Version: 1.0
 */
@Service
public class SysLogConfigService {

    /**
     * @description: 动态处理日志配置实时生效
     * @return void
     */
    public void setLogBackInfo(int maxHistory){
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        //ROOT -> ASYNC -> FILE 为目前logback的配置层级关系
        Logger logger = loggerContext.getLogger("ROOT");
        LogAsyncAppender asyncAppender = (LogAsyncAppender)logger.getAppender("ASYNC");
        //获取需要设置的FILE appender
        Appender appender = asyncAppender.getAppender("FILE");
        RollingFileAppender rollingFileAppender = (RollingFileAppender)appender;
        SizeAndTimeBasedRollingPolicy rollingPolicy = (SizeAndTimeBasedRollingPolicy)rollingFileAppender.getRollingPolicy();
        rollingPolicy.setMaxHistory(maxHistory);
        //rollingPolicy.setMaxFileSize(new FileSize(5120)); 设置日志文件最大size用于测试
        rollingFileAppender.setRollingPolicy(rollingPolicy);
        //loggerContext.putProperty("LOG_FILE_MAX_SIZE","5KB");
        //上下文属性保存，和策略中的值保持一致，可用于查询
        loggerContext.putProperty("LOG_FILE_MAX_HISTORY",String.valueOf(maxHistory));
        rollingFileAppender.setContext(loggerContext);
        appender.stop();
        //重启rollingAppender策略
        rollingFileAppender.getRollingPolicy().start();
        rollingFileAppender.getTriggeringPolicy().start();
        //启动File appender
        appender.start();
    }
}
