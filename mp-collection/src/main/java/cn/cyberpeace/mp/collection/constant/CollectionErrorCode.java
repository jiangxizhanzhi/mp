package cn.cyberpeace.mp.collection.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

/**
  *@Desciption 
  *@Author zhangjin
  *@Date 9/3/21 10:22 上午
**/
public enum CollectionErrorCode implements ErrorCode {
    CONNECTION_FAIL("MP-110400", "connection_fail"),
    WAZUH_AGENTS_QUERY_ERROR("MP-110413", "wazuh_agents_query_error");


    CollectionErrorCode(String code, String message) {
        setCode(code, CollectionConstant.ERROR_CODE_PREFIX + message);
    }
}
