package cn.cyberpeace.mp.auth.exception;

import cn.cyberpeace.mp.auth.constant.CustomAuthErrorCode;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.excel.exception.ExcelCommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Auth模块异常统一处理
 */
@ControllerAdvice(basePackages = "cn.cyberpeace.mp.auth")
@Slf4j
public class AuthExceptionHandler {


    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ServiceResult<String> illegalArgumentExceptionHandle(IllegalArgumentException ex) {
        String errorMsg = ex.getMessage();
        log.error(errorMsg);
        CustomAuthErrorCode customAuthErrorCode = new CustomAuthErrorCode();
        customAuthErrorCode.setCode(BaseErrorCode.PARAMS_INVALID.getCode(), errorMsg);
        return ServiceResult.<String>builder().serviceCode(customAuthErrorCode).build().asResult();
    }

    @ExceptionHandler({AccountStatusException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ServiceResult<String> accountStatusExceptionHandle(AccountStatusException ex) {
        String errorMsg = ex.getMessage();
        log.error(errorMsg);
        CustomAuthErrorCode customAuthErrorCode = new CustomAuthErrorCode();
        customAuthErrorCode.setCode(BaseErrorCode.PARAMS_INVALID.getCode(), errorMsg);
        return ServiceResult.<String>builder().serviceCode(customAuthErrorCode).build().asResult();
    }


    @ExceptionHandler({MpRuntimeException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ServiceResult<String> mpRuntimeExceptionHandle(MpRuntimeException ex) {
        String errorMsg = ex.getMessage();
        log.error(errorMsg);
        CustomAuthErrorCode customAuthErrorCode = new CustomAuthErrorCode();
        customAuthErrorCode.setCode(BaseErrorCode.PARAMS_INVALID.getCode(), errorMsg);
        return ServiceResult.<String>builder().serviceCode(customAuthErrorCode).build().asResult();
    }


    @ExceptionHandler({ExcelCommonException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ServiceResult<String> excelExceptionHandle(ExcelCommonException ex) {
        String errorMsg = ex.getMessage();
        log.error(errorMsg);
        CustomAuthErrorCode customAuthErrorCode = new CustomAuthErrorCode();
        customAuthErrorCode.setCode(BaseErrorCode.PARAMS_INVALID.getCode(), "Excel格式错误!");
        return ServiceResult.<String>builder().serviceCode(customAuthErrorCode).build().asResult();
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ServiceResult<String> exceptionHandle(Exception ex) {
        //String errorMsg = ex.getMessage();
        String errorMsg = ExceptionUtil.stacktraceToString(ex);
        log.error(errorMsg);
        CustomAuthErrorCode customAuthErrorCode = new CustomAuthErrorCode();
        customAuthErrorCode.setCode(BaseErrorCode.PARAMS_INVALID.getCode(), errorMsg);
        return ServiceResult.<String>builder().serviceCode(customAuthErrorCode).build().asResult();
    }

}
