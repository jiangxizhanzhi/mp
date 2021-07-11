package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.log.bo.LogLevelBO;
import cn.cyberpeace.mp.log.response.LogLevelResponse;

import java.util.List;


public interface ILogSettingService {

    
    ServiceResult<LogLevelResponse> setLogLevel(String logName, String logLevel);

    
    ServiceResult<List<LogLevelResponse>> setBatchLogLevel(List<LogLevelBO> logLevelRequestList);


    
    ServiceResult<List<LogLevelResponse>> resetDefualtLogLevel();

    
    ServiceResult<List<LogLevelResponse>> getLogLevel(String logName);

    
    void loadDynamicLogLevel();

}
