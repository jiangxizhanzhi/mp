package cn.cyberpeace.mp.base.exception;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 业务异常统一处理
 */
@ControllerAdvice
@Slf4j
public class MpRuntimeExceptionHandler {

    private static final String LOGGER_FORMAT = "操作执行异常:,异常信息：{},堆栈信息：{}";

    @ExceptionHandler(MpRuntimeException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ServiceResult<String> b2bRuntimeExceptionHandler(MpRuntimeException ex) {
        log.error(LOGGER_FORMAT, ex.getMessage(), ex.getDetail());
        return ServiceResult.<String>builder().message(ex.getMessage()).serviceCode(ex.getServiceCode()).detail(ex.getDetail()).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ServiceResult<String> validationExceptionHandle(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage());
        ObjectError objectError = ex.getBindingResult().getAllErrors().get(0);
        return ServiceResult.fail(BaseErrorCode.PARAMS_INVALID, objectError.getDefaultMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ServiceResult<String> handleException(Exception ex){
        log.error(LOGGER_FORMAT, ex.getMessage(), ex);
        return ServiceResult.fail("系统异常：" + ex.getMessage());
    }
}
