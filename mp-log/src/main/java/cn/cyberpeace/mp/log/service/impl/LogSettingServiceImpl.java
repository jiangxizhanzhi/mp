package cn.cyberpeace.mp.log.service.impl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.log.bo.LogLevelBO;
import cn.cyberpeace.mp.log.config.EnvConfig;
import cn.cyberpeace.mp.log.po.MpLogLevelPO;
import cn.cyberpeace.mp.log.repository.MpLogLevelPOMapper;
import cn.cyberpeace.mp.log.response.LogLevelResponse;
import cn.cyberpeace.mp.log.service.ILogSettingService;
import cn.cyberpeace.mp.log.utils.YamlUtils;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
    public static final String PROPERTIES = ".properties";
    public static final String YML = ".yml";
    public static final String YAML = ".yaml";
    public static final String LOGGING = "logging";
    public static final String LEVEL = "level";
    public static final String EXT_VAR = "ext-var";
    public static final String ROOT_KEY = "root";
    public static final String LOGGING_LEVEL_EXT_VAR_ROOT = "logging.level.ext-var.root";
    public static final String ROOT_KEY_FOR_PROP = "ROOT_LEVEL";
    public static final String EQUAL_SIGN = "\\=";

    @Value("${logging.level.root:ERROR}")
    private String defaulLogLevel;

    @Value("${mp.log.dynamic-level-keeping:false}")
    private boolean dynamicLevelKeeping;

    @Value("${logging.level.dynamic-level-only-root:true}")
    private boolean dynamicLevelOnlyRoot;

    @Autowired
    private MpLogLevelPOMapper mpLogLevelMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ServiceResult<LogLevelResponse> setLogLevel(String logName, String logLevel) {
        if (StringUtils.isEmpty(logName) || StringUtils.isEmpty(logLevel)) {
            return new ServiceResult<>((LogLevelResponse) null, BaseErrorCode.PARAMS_INVALID).asResult();
        }
        if (dynamicLevelOnlyRoot && !ROOT.equalsIgnoreCase(logName)) {
            return new ServiceResult<>((LogLevelResponse) null, BaseErrorCode.PARAMS_INVALID).asResult();
        }
        logName = getUpperLogNameIfRoot(logName);
        LogLevelBO loglevelBO = new LogLevelBO();
        loglevelBO.setLogName(logName);
        loglevelBO.setLogLevel(logLevel);
        ServiceResult<List<LogLevelResponse>> result = this.setBatchLogLevel(Arrays.asList(loglevelBO));
        List<LogLevelResponse> batchList = result.getData();
        if (CollectionUtils.isEmpty(batchList)) {
            log.error("logName {} is not valid", logName);
            throw new MpRuntimeException(result.getServiceCode());
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
    public ServiceResult<List<LogLevelResponse>> setBatchLogLevel(List<LogLevelBO> logLevelRequestList) {
        if (logLevelRequestList.isEmpty()) {
            new ServiceResult<>((List<LogLevelResponse>) null).asResult();
        }
        if (dynamicLevelOnlyRoot && (logLevelRequestList.size() > 1 || !ROOT.equalsIgnoreCase(logLevelRequestList.get(0).getLogName()))) {
            return new ServiceResult<>((List<LogLevelResponse>) null, BaseErrorCode.PARAMS_INVALID).asResult();
        }
        List<LogLevelResponse> logLevelList = new ArrayList<>();
        List<MpLogLevelPO> poList = new ArrayList<>();
        final String[] rootLevel = new String[]{""};
        if (LoggerFactory.getILoggerFactory() instanceof LoggerContext && !logLevelRequestList.isEmpty()) {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            batchSetLogLevel(logLevelRequestList, logLevelList, poList, rootLevel, loggerContext);
            if (dynamicLevelKeeping) {
                //批量删除，再插入，事务
                String[] ids = poList.stream().map(e -> e.getLogName()).collect(Collectors.toList()).toArray(new String[poList.size()]);
                mpLogLevelMapper.bulkLogicDelete(ids);
                mpLogLevelMapper.bulkCreate(poList);
                //配置文件中设置日志级别
                if (StringUtils.isNotEmpty(rootLevel[0])) {
                    setLogLevel2ExtenalConfig(rootLevel[0]);
                }
            }
        }
        return new ServiceResult<>(logLevelList).asResult();
    }

    private void batchSetLogLevel(List<LogLevelBO> logLevelRequestList, List<LogLevelResponse> logLevelList, List<MpLogLevelPO> poList, String[] rootLevel, LoggerContext loggerContext) {
        for (Logger logger : loggerContext.getLoggerList()) {
            try {
                logLevelRequestList.stream().filter(e -> logger.getName().equals(getUpperLogNameIfRoot(e.getLogName())))
                        .forEach(e -> {
                            logger.setLevel(getTargetLogLevel(e.getLogName(), e.getLogLevel(), logger));
                            //设置查询设置结果
                            String level = getLevelStr(logger);
                            if (ROOT.equalsIgnoreCase(logger.getName())) {
                                rootLevel[0] = level;
                            }
                            logLevelList.add(new LogLevelResponse(logger.getName(), level));
                            poList.add(new MpLogLevelPO(logger.getName(), logger.getName(), level));
                        });
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
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
                    String level = getLevelStr(logger);
                    if (!defaulLogLevel.equalsIgnoreCase(level)) {
                        logger.setLevel(Level.toLevel(defaulLogLevel, Level.ERROR));
                        level = getLevelStr(logger);
                        setLogLevel2ExtenalConfig(level);
                    }
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
                    setYamlRootLevel(logName, logLevelList, logger);
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
        return new ServiceResult<>(logLevelList).asResult();
    }

    private void setYamlRootLevel(String logName, List<LogLevelResponse> logLevelList, Logger logger) {
        if (StringUtils.isNotEmpty(logName)) {
            //后置星号模糊匹配
            boolean suffixBlurMatch = logName.endsWith("*") && logger.getName().startsWith(logName.substring(0, logName.length() - 1));
            if (logger.getName().equals(logName) || suffixBlurMatch) {
                logLevelList.add(new LogLevelResponse(logger.getName(), getLevelStr(logger)));
            }
        } else {
            logLevelList.add(new LogLevelResponse(logger.getName(), getLevelStr(logger)));
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

    private String getLevelStr(Logger logger) {
        return ObjectUtils.isNotEmpty(logger.getLevel()) ? logger.getLevel().levelStr : "";
    }

    /**
     * @description: 更新外部配置文件日志级别
     * @param: rootLevel
     * @return: void
     * @author: wangssh
     * @date: 2023/6/5
     */
    private synchronized void setLogLevel2ExtenalConfig(String rootLevel) {
        if (StringUtils.isEmpty(rootLevel)) {
            return;
        }
        log.info("####### spring.config.addition-locaiton:{}", EnvConfig.getApplicationExt());
        log.info("####### MP_ENV_PATH:{}", EnvConfig.getMpEnvPath());
        String envProp = EnvConfig.getMpEnvPath();
        if (StringUtils.isNotEmpty(envProp) && envProp.endsWith(PROPERTIES) && FileUtil.exist(envProp)) {
            //优先配置DMP_ENV_PATH变量下的properties日志级别
            setNewRootLevel(ROOT_KEY_FOR_PROP, rootLevel, envProp);
        } else if (StringUtils.isNotEmpty(EnvConfig.getApplicationProp())) {
            String applicationProp = EnvConfig.getApplicationProp();
            //次优先配置spring.config.addition-location中的application-prop.properties日志级别
            if (StringUtils.isNotEmpty(applicationProp) && applicationProp.endsWith(PROPERTIES) && FileUtil.exist(applicationProp)) {
                setNewRootLevel(ROOT_KEY_FOR_PROP, rootLevel, applicationProp);
            }
        } else if (StringUtils.isNotEmpty(EnvConfig.getApplicationExt())) {
            //如果前两者都不存在，配置application-ext.yml日志级别
            setYamlRootLevel(rootLevel,  EnvConfig.getApplicationExt());
        }
    }

    private synchronized void setNewRootLevel(String rootKey, String rootLevel, String file) {
        List<String> linkedList = new LinkedList<>();
        String logLevel = "";
        int rootLineNum = -1;
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            int emptyLineCnt = 0;
            String line = "";
            //最大空行连续允许10，如果超过10个认为文件结束
            while (emptyLineCnt < 10) {
                line = reader.readLine();
                if (StringUtils.isEmpty(line)) {
                    emptyLineCnt++;
                    //如果出现连续空行保留，只保留一个
                    if (emptyLineCnt == 1) {
                        linkedList.add(line);
                    }
                    continue;
                }
                emptyLineCnt = 0;
                linkedList.add(line);
                rootKey = getRootKey(line, rootKey);
                if (line.trim().startsWith(rootKey)) {
                    String[] rootStrs = line.split(EQUAL_SIGN);
                    if (rootKey.equalsIgnoreCase(rootStrs[0].trim()) && StringUtils.isNotEmpty(rootStrs[1].trim())) {
                        rootLineNum = linkedList.size() - 1;
                        logLevel = rootStrs[1].trim();
                    }
                }
            }
            //最后一行如果为空行，移除
            if (!CollectionUtils.isEmpty(linkedList) && StringUtils.isEmpty(linkedList.get(linkedList.size() - 1))) {
                linkedList.remove(linkedList.size() - 1);
            }
            log.info("current ROOT log level :{}", logLevel);
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
        }
        updateLogLevel2ExtPropConfig(rootKey, rootLevel, file, logLevel, linkedList, rootLineNum);
    }

    private String getRootKey(String line, String rootKey) {
        if (StringUtils.isEmpty(line)) {
            return rootKey;
        }
        if (line.trim().startsWith(ROOT_KEY_FOR_PROP)) {
            //对应于application-prop.properties.template中 ROOT_LEVEL 变量
            return ROOT_KEY_FOR_PROP;
        } else if (line.trim().startsWith(LOGGING_LEVEL_EXT_VAR_ROOT)) {
            //对应于application-prop.properties.yml-template中 logging.level.ext-var.root 变量
            return LOGGING_LEVEL_EXT_VAR_ROOT;
        }
        return rootKey;
    }

    private synchronized void updateLogLevel2ExtPropConfig(String rootKey, String rootLevel, String file, String logLevel, List<String> linkedList, int rootLineNum) {
        if (!rootLevel.equalsIgnoreCase(logLevel)) {
            if(rootLineNum >= 0 && rootLineNum < linkedList.size()){
                linkedList.set(rootLineNum, rootKey.concat("=").concat(rootLevel.toUpperCase()));
            }
            try (OutputStream outputStream = new FileOutputStream(new File(file))) {
                IOUtils.writeLines(linkedList, null, outputStream, StandardCharsets.UTF_8);
                log.info("set new ROOT log level :{}", rootLevel);
            } catch (FileNotFoundException ex) {
                log.warn(ex.getMessage(), ex);
            } catch (IOException ex) {
                log.warn(ex.getMessage(), ex);
            }
        }
    }

    private synchronized boolean setYamlRootLevel(String rootLevel, String file) {
        Map<String, Object> map = null;
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        if (StringUtils.isNotEmpty(file) && (file.endsWith(YML) || file.endsWith(YAML)) && FileUtil.exist(file)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                map = yaml.loadAs(reader, Map.class);
                YamlUtils.buildComment(file);
                if (updateLogLevel2ExtYamlConfig(rootLevel, file, map, yaml)) {
                    return true;
                }
            } catch (FileNotFoundException ex) {
                log.warn(ex.getMessage(), ex);
            } catch (IOException ex) {
                log.warn(ex.getMessage(), ex);
            }
        }
        return false;
    }

    private synchronized boolean updateLogLevel2ExtYamlConfig(String rootLevel, String file, Map<String, Object> map, Yaml yaml) {
        String configLogLevel = setMapObjLogLevelAndGet(rootLevel, map);
        if (!rootLevel.equalsIgnoreCase(configLogLevel)) {
            try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
                yaml.dump(map, writer);
                YamlUtils.addComment(file);
                log.info("set new ROOT log level :{}", rootLevel);
                return true;
            } catch (FileNotFoundException ex) {
                log.warn(ex.getMessage(), ex);
            } catch (IOException ex) {
                log.warn(ex.getMessage(), ex);
            }
        }
        return false;
    }

    private String setMapObjLogLevelAndGet(String rootLevel, Map<String, Object> map) {
        String logLevel = "";
        Map<String, Object> subMap = (Map<String, Object>) map.get(LOGGING);
        if (subMap != null && subMap.containsKey(LEVEL)) {
            subMap = (Map<String, Object>) subMap.get(LEVEL);
            if (subMap != null && subMap.containsKey(EXT_VAR)) {
                subMap = (Map<String, Object>) subMap.get(EXT_VAR);
                if (subMap != null && subMap.containsKey(ROOT_KEY)) {
                    logLevel = (String) subMap.get(ROOT_KEY);
                    if (!rootLevel.equalsIgnoreCase(logLevel)) {
                        log.info("current ROOT log level :{}", logLevel);
                        subMap.put(ROOT_KEY, rootLevel.toUpperCase());
                    }
                }
            }
        }
        return logLevel;
    }

}
