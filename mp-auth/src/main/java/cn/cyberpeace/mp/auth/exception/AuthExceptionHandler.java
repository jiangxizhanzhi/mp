package cn.cyberpeace.mp.auth.exception;

import cn.cyberpeace.mp.auth.constant.CustomAuthErrorCode;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.excel.exception.ExcelCommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @Description: Auth模块异常统一处理
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
//@ControllerAdvice(basePackages = "cn.cyberpeace.mp.auth")
//@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class AuthExceptionHandler {


    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ServiceResult<String> illegalArgumentExceptionHandle(IllegalArgumentException ex) {
        String errorMsg = ex.getMessage();
        log.error(ExceptionUtil.stacktraceToString(ex));
        CustomAuthErrorCode customAuthErrorCode = new CustomAuthErrorCode();
        customAuthErrorCode.setCode(BaseErrorCode.PARAMS_INVALID.getCode(), errorMsg);
        return ServiceResult.<String>builder().serviceCode(customAuthErrorCode).build().asResult();
    }

    @ExceptionHandler({AccountStatusException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ServiceResult<String> accountStatusExceptionHandle(AccountStatusException ex) {
        String errorMsg = ex.getMessage();
        log.error(ExceptionUtil.stacktraceToString(ex));
        CustomAuthErrorCode customAuthErrorCode = new CustomAuthErrorCode();
        customAuthErrorCode.setCode(BaseErrorCode.PARAMS_INVALID.getCode(), errorMsg);
        return ServiceResult.<String>builder().serviceCode(customAuthErrorCode).build().asResult();
    }


    @ExceptionHandler({MpRuntimeException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ServiceResult<String> mpRuntimeExceptionHandle(MpRuntimeException ex) {
        String errorMsg = ex.getMessage();
        log.error(ExceptionUtil.stacktraceToString(ex));
        CustomAuthErrorCode customAuthErrorCode = new CustomAuthErrorCode();
        customAuthErrorCode.setCode(BaseErrorCode.PARAMS_INVALID.getCode(), errorMsg);
        return ServiceResult.<String>builder().serviceCode(customAuthErrorCode).build().asResult();
    }


    @ExceptionHandler({ExcelCommonException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ServiceResult<String> excelExceptionHandle(ExcelCommonException ex) {
        String errorMsg = ex.getMessage();
        log.error(ExceptionUtil.stacktraceToString(ex));
        CustomAuthErrorCode customAuthErrorCode = new CustomAuthErrorCode();
        customAuthErrorCode.setCode(BaseErrorCode.PARAMS_INVALID.getCode(), "Excel格式错误!");
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
