 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ILogSettingService.java.
 // File size 1927.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.log.bo.LogLevelBO;
import cn.cyberpeace.mp.log.response.LogLevelResponse;

import java.util.List;

 /**
  * ilog设置服务
  *
  * @author wangssh
  * @date 2023/6/1
  */
 public interface ILogSettingService {

     /**
      * 设置日志级别
      *
      * @param logName  日志模块名
      * @param logLevel 日志级别
      * @return cn.cyberpeace.mp.base.service.result.ServiceResult<cn.cyberpeace.mp.log.response.LogLevelResponse>
      */
     ServiceResult<LogLevelResponse> setLogLevel(String logName, String logLevel);

     /**
      * 设置批处理日志级别
      *
      * @param logLevelRequestList 批量日志级别
      * @return cn.cyberpeace.mp.base.service.result.ServiceResult<java.util.List < cn.cyberpeace.mp.log.response.LogLevelResponse>>
      */
     ServiceResult<List<LogLevelResponse>> setBatchLogLevel(List<LogLevelBO> logLevelRequestList);

     /**
      * 重置默认日志级别
      *
      * @return cn.cyberpeace.mp.base.service.result.ServiceResult<java.util.List < cn.cyberpeace.mp.log.response.LogLevelResponse>>
      */
     ServiceResult<List<LogLevelResponse>> resetDefualtLogLevel();

     /**
      * 获取日志级别
      *
      * @param logName 日志模块名
      * @return cn.cyberpeace.mp.base.service.result.ServiceResult<java.util.List < cn.cyberpeace.mp.log.response.LogLevelResponse>>
      */
     ServiceResult<List<LogLevelResponse>> getLogLevel(String logName);

     /**
      * 负载动态日志级别
      */
     void loadDynamicLogLevel();

}
