package cn.cyberpeace.mp.datasandtable.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

public enum SituationalAwareErrorCode implements ErrorCode {
    PROBE_DELETE_FAIL("SA-1001", "probe delete fail"),
    PROBE_MODIFY_FAIL("SA-1002", "probe update fail"),
    PROBE_MODIFY_PARAM_ERROR("SA-1003", "probe update param error"),
    PROBE_MANAGER_ADD_ERROR("SA-1004", "probe manager add script error");

    SituationalAwareErrorCode(String code, String message) {
        setCode(code, message);
    }
}
