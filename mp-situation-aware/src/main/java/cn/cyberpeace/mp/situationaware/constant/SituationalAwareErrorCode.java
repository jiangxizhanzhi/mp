package cn.cyberpeace.mp.situationaware.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

/**
  *@Desciption 
  *@Author zhangjin
  *@Date 9/3/21 10:22 上午
**/
public enum SituationalAwareErrorCode implements ErrorCode {
    PROBE_DELETE_FAIL("SA-1001", "probe delete fail"),
    PROBE_MODIFY_FAIL("SA-1002", "probe update fail"),
    PROBE_MODIFY_PARAM_ERROR("SA-1003", "probe update param error"),
    PROBE_MANAGER_ADD_ERROR("SA-1004", "probe manager add script error"),
    PROBE_NAME_EXIST("SA-1005", "probe name already exit"),
    PROBE_MANAGER_ES_QUERY_ERROR("SA-1005", "probe es query error")
    ;

    SituationalAwareErrorCode(String code, String message) {
        setCode(code, message);
    }
}
