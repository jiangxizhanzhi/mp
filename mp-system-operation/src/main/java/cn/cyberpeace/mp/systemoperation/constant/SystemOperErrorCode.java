package cn.cyberpeace.mp.systemoperation.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

public enum SystemOperErrorCode implements ErrorCode {

    FLOW_NOT_EXIST("SYSTEM-OPERATION-0001", "system_operation_not_exist");

    SystemOperErrorCode(String code, String message) {
        setCode(code, "system_operation.error_code." + message);
    }
}