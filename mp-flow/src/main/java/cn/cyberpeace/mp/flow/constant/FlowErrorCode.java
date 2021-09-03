package cn.cyberpeace.mp.flow.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

public enum FlowErrorCode implements ErrorCode {

    FLOW_NOT_EXIST("MP-110300", "flow_not_exist");

    FlowErrorCode(String code, String message) {
        setCode(code, "flow.error_code." + message);
    }
}