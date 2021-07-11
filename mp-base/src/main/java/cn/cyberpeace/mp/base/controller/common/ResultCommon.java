package cn.cyberpeace.mp.base.controller.common;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.code.ServiceCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;

public class ResultCommon {

    public static <T> ServiceResult<T> success() {
        return ServiceResult.<T>builder().build().asResult();
    }

    public static <T> ServiceResult<T> success(T source) {
        return ServiceResult.<T>builder().source(source).build().asResult();
    }

    public static <T1> ServiceResult<T1> fail(ServiceCode errorCode) {
        return ServiceResult.<T1>builder().serviceCode(errorCode).build().asResult();
    }

    
    public static <T1> ServiceResult<T1> fail(String detail) {
        return ServiceResult.<T1>builder().serviceCode(BaseErrorCode.ERROR).detail(detail).build().asResult();
    }

}
