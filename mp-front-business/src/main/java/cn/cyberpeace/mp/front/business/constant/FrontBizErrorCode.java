package cn.cyberpeace.mp.front.business.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/21 17:36
 */
public enum FrontBizErrorCode implements ErrorCode {

    URL_AC_REQUEST_METHOD_NOT_MATCH("MP-110141", "url_request_method_not_match"),
    URL_AC_AUTHORITY_NOT_MATCH("MP-110142", "url_access_denied");

    FrontBizErrorCode(String code, String message) {
        setCode(code, FrontBizConstant.ERROR_CODE_PREFIX + message);
    }

}
