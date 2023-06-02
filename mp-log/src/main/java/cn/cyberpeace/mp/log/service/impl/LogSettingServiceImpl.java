package cn.cyberpeace.mp.log.service.impl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.code.ServiceCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.log.bo.LogLevelBO;
import cn.cyberpeace.mp.log.po.MpLogLevelPO;
import cn.cyberpeace.mp.log.repository.MpLogLevelPOMapper;
import cn.cyberpeace.mp.log.response.LogLevelResponse;
import cn.cyberpeace.mp.log.service.ILogSettingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wangssh
 * @version 1.0
 * @description 日志级别设置
 * @date 2023/5/30
 **/
@Slf4j
@Service
public class LogSettingServiceImpl implements ILogSettingService {

    public static final String ROOT = "ROOT";

    @Value("${logging.level.root:ERROR}")
    private String defaulLogLevel;

    /**
     * 是否记录动态日志级别设置
     */
    @Value("${mp.log.dynamic-level-keeping:false}")
    private boolean dynamicLevelKeeping;

    @Autowired
    private MpLogLevelPOMapper mpLogLevelMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ServiceResult<LogLevelResponse> setLogLevel(String logName, String logLevel) {
        if (StringUtils.isEmpty(logName) || StringUtils.isEmpty(logLevel)) {
            return new ServiceResult<>((LogLevelResponse) null).asResult();
        }
        logName = getUpperLogNameIfRoot(logName);
        LogLevelBO loglevelBO = new LogLevelBO();
        loglevelBO.setLogName(logName);
        loglevelBO.setLogLevel(logLevel);
        ServiceResult<List<LogLevelResponse>> result = this.setLogLevel(Arrays.asList(loglevelBO));
        List<LogLevelResponse> batchList = result.getData();
        if (CollectionUtils.isEmpty(batchList)) {
            log.error("logName {} is not valid", logName);
            throw new MpRuntimeException(BaseErrorCode.DATA_NOT_EXIST);
        }
        return new ServiceResult<>(batchList.get(0)).asResult();
    }

    private String getUpperLogNameIfRoot(String logName) {
        if (ROOT.equalsIgnoreCase(logName)) {
            return logName.toUpperCase();
        }
        return logName;
    }

    private Level getTargetLogLevel(String logName, String logLevel, Logger targetLogger) {
        if (StringUtils.isEmpty(logLevel)) {
            return null;
        }
        logLevel = logLevel.toUpperCase();
        //如果是全局设置，默认为INFO级别
        Level curLevel = targetLogger.getLevel();
        if (ROOT.equalsIgnoreCase(logName) && curLevel == null) {
            curLevel = Level.toLevel(defaulLogLevel, Level.ERROR);
        }
        return Level.toLevel(logLevel, curLevel);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ServiceResult<List<LogLevelResponse>> setLogLevel(List<LogLevelBO> logLevelRequestList) {
        List<LogLevelResponse> logLevelList = new ArrayList<>();
        List<MpLogLevelPO> poList = new ArrayList<>();
        if (LoggerFactory.getILoggerFactory() instanceof LoggerContext && !logLevelRequestList.isEmpty()) {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            for (Logger logger : loggerContext.getLoggerList()) {
                try {
                    logLevelRequestList.stream().filter(e -> logger.getName().equals(getUpperLogNameIfRoot(e.getLogName())))
                            .forEach(e -> {
                                logger.setLevel(getTargetLogLevel(e.getLogName(), e.getLogLevel(), logger));
                                //设置查询设置结果
                                String level = ObjectUtils.isNotEmpty(logger.getLevel()) ? logger.getLevel().levelStr : "";
                                logLevelList.add(new LogLevelResponse(logger.getName(), level));
                                poList.add(new MpLogLevelPO(logger.getName(), logger.getName(), level));
                            });
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }
            }
            if (dynamicLevelKeeping) {
                //批量删除，再插入，事务
                String[] ids = poList.stream().map(e -> e.getLogName()).collect(Collectors.toList()).toArray(new String[poList.size()]);
                mpLogLevelMapper.bulkLogicDelete(ids);
                mpLogLevelMapper.bulkCreate(poList);
            }
        }
        return new ServiceResult<>(logLevelList).asResult();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ServiceResult<List<LogLevelResponse>> resetDefualtLogLevel() {
        if (LoggerFactory.getILoggerFactory() instanceof LoggerContext) {
            //删除所有日志级别记录
            mpLogLevelMapper.deleteAll();
            setRootDefaultAndOtherNull();
        }
        return this.getLogLevel(null);
    }

    private void setRootDefaultAndOtherNull() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        for (Logger logger : loggerContext.getLoggerList()) {
            try {
                //重置只设置全局配置，其他配置为空
                if (ROOT.equalsIgnoreCase(logger.getName())) {
                    logger.setLevel(Level.toLevel(defaulLogLevel, Level.ERROR));
                } else {
                    logger.setLevel(null);
                }
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

    @Override
    public ServiceResult<List<LogLevelResponse>> getLogLevel(String logName) {
        List<LogLevelResponse> logLevelList = new ArrayList<>();
        logName = getUpperLogNameIfRoot(logName);
        if (LoggerFactory.getILoggerFactory() instanceof LoggerContext) {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            for (Logger logger : loggerContext.getLoggerList()) {
                try {
                    extracted(logName, logLevelList, logger);
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
        return new ServiceResult<>(logLevelList).asResult();
    }

    private void extracted(String logName, List<LogLevelResponse> logLevelList, Logger logger) {
        if (StringUtils.isNotEmpty(logName)) {
            //后置星号模糊匹配
            boolean suffixBlurMatch = logName.endsWith("*") && logger.getName().startsWith(logName.substring(0, logName.length() - 1));
            if (logger.getName().equals(logName) || suffixBlurMatch) {
                logLevelList.add(new LogLevelResponse(logger.getName(), ObjectUtils.isNotEmpty(logger.getLevel()) ? logger.getLevel().levelStr : ""));
            }
        } else {
            logLevelList.add(new LogLevelResponse(logger.getName(), ObjectUtils.isNotEmpty(logger.getLevel()) ? logger.getLevel().levelStr : ""));
        }
    }

    @Override
    public void loadDynamicLogLevel() {
        HumanCondition humanCondition = new HumanCondition();
        List<MpLogLevelPO> list = mpLogLevelMapper.list(humanCondition);
        if (CollectionUtils.isEmpty(list)) {
            log.warn("There are no dynamic log levels to be loaded");
            return;
        }
        if (LoggerFactory.getILoggerFactory() instanceof LoggerContext) {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            list.forEach(e -> {
                try {
                    Logger logger = loggerContext.getLogger(e.getLogName());
                    Level level = getTargetLogLevel(e.getLogName(), e.getLogLevel(), logger);
                    logger.setLevel(level);
                } catch (Exception ex) {
                    log.warn(ex.getMessage(), ex);
                }

            });
        }
    }


}
