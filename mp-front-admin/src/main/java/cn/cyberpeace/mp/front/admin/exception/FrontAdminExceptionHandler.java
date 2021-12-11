package cn.cyberpeace.mp.front.admin.exception;

import cn.cyberpeace.mp.auth.constant.CustomAuthErrorCode;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @Description: Admin模块异常统一处理
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
//@ControllerAdvice(basePackages = "cn.cyberpeace.mp.front.admin")
//@ControllerAdvice
@Slf4j
public class FrontAdminExceptionHandler {

    @ExceptionHandler({MpRuntimeException.class})
//    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ServiceResult<String> mpRuntimeExceptionHandle(MpRuntimeException ex) {
        String errorMsg = ex.getMessage();
        log.error(ExceptionUtil.stacktraceToString(ex));
        CustomAuthErrorCode customAuthErrorCode = new CustomAuthErrorCode();
        customAuthErrorCode.setCode(BaseErrorCode.PARAMS_INVALID.getCode(), errorMsg);
        return ServiceResult.<String>builder().serviceCode(customAuthErrorCode).build().asResult();
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ServiceResult<String> exceptionHandle(Exception ex) {
        String errorMsg = ex.getMessage();
        log.error(ExceptionUtil.stacktraceToString(ex));
        CustomAuthErrorCode customAuthErrorCode = new CustomAuthErrorCode();
        customAuthErrorCode.setCode(BaseErrorCode.PARAMS_INVALID.getCode(), errorMsg);
        return ServiceResult.<String>builder().serviceCode(customAuthErrorCode).build().asResult();
    }

}
