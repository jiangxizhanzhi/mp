package cn.cyberpeace.mp.base.constant;

import cn.cyberpeace.mp.base.service.code.ServiceCode;

public enum BaseErrorCode implements ServiceCode {
    SUCCESS("0000", "success"),
    ERROR("0001", "error");

    BaseErrorCode(String code, String message) {
        setCode(code, message);
    }
}
