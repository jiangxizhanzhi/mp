package cn.cyberpeace.mp.frontbusiness.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;


public enum FrontBizErrorCode implements ErrorCode {

    URL_AC_REQUEST_METHOD_NOT_MATCH("MP-110141", "url_request_method_not_match"),
    URL_AC_AUTHORITY_NOT_MATCH("MP-110142", "url_access_denied");

    FrontBizErrorCode(String code, String message) {
        setCode(code, FrontBizConstant.ERROR_CODE_PREFIX + message);
    }

}
