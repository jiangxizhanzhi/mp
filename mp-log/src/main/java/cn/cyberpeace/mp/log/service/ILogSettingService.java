 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ILogSettingService.java.
 // File size 2011.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.log.bo.LogLevelBO;
import cn.cyberpeace.mp.log.response.LogLevelResponse;

import java.util.List;

/**
 * @ClassName ILogSettingService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface ILogSettingService {

    /**
     * @description setLogLevel
     *
     * @param logName 日志模块名
     * @param logLevel 日志级别
     * @return cn.cyberpeace.mp.base.service.result.ServiceResult<cn.cyberpeace.mp.log.response.LogLevelResponse>
     * @author wangssh
     * @date 2023/6/1
     */
    ServiceResult<LogLevelResponse> setLogLevel(String logName, String logLevel);

    /**
     * @description setBatchLogLevel
     *
     * @param logLevelRequestList 批量日志级别
     * @return cn.cyberpeace.mp.base.service.result.ServiceResult<java.util.List<cn.cyberpeace.mp.log.response.LogLevelResponse>>
     * @author wangssh
     * @date 2023/5/31
     */
    ServiceResult<List<LogLevelResponse>> setBatchLogLevel(List<LogLevelBO> logLevelRequestList);


    /**
     * @description resetDefualLogLevel
     *
     * @return cn.cyberpeace.mp.base.service.result.ServiceResult<java.util.List<cn.cyberpeace.mp.log.response.LogLevelResponse>>
     * @author wangssh
     * @date 2023/5/31
     */
    ServiceResult<List<LogLevelResponse>> resetDefualtLogLevel();

    /**
     * @description getLogLevel
     *
     * @param logName 日志模块名
     * @return cn.cyberpeace.mp.base.service.result.ServiceResult<java.util.List<cn.cyberpeace.mp.log.response.LogLevelResponse>>
     * @author wangssh
     * @date 2023/5/31
     */
    ServiceResult<List<LogLevelResponse>> getLogLevel(String logName);

    /**
     * @description loadDynamicLogLevel
     *
     * @return void
     * @author wangssh
     * @date 2023/6/1
     */
    void loadDynamicLogLevel();

}
