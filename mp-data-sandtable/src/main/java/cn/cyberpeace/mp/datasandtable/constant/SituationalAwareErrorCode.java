package cn.cyberpeace.mp.datasandtable.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

public enum SituationalAwareErrorCode implements ErrorCode {
    PROBE_DELETE_FAIL("SA-1001", "probe delete fail");

    SituationalAwareErrorCode(String code, String message) {
        setCode(code, message);
    }
}
