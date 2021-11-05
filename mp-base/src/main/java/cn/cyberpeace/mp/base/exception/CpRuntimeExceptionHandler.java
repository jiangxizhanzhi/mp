package cn.cyberpeace.mp.base.exception;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 业务异常统一处理
 */
@ControllerAdvice
@Slf4j
public class CpRuntimeExceptionHandler {

    private static final String LOGGER_FORMAT = "System error: {}, detail: {}";

    @ExceptionHandler(MpRuntimeException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ServiceResult<Object> b2bRuntimeExceptionHandler(MpRuntimeException ex) {
        log.error(LOGGER_FORMAT, ex.getMessage(), ex.getDetail());
        var serviceCode = ex.getServiceCode();
        if (serviceCode == null) {
            serviceCode = BaseErrorCode.ERROR;
        }
        return new ServiceResult<>(null, serviceCode, ex.getDetail()).asResult();
    }

    @ExceptionHandler({BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ServiceResult<Object> validationExceptionHandle(BindException ex) {
        log.error(ex.getMessage());
        var objectError = ex.getBindingResult().getAllErrors().get(0);
        var bindingResult = ex.getBindingResult();
        String detail = objectError.getDefaultMessage();
        if (bindingResult.hasErrors()) {
            var fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                var field = fieldError.getField();
                String defaultMessage = fieldError.getDefaultMessage();
                detail = field + ": " + defaultMessage;
            }
        }
        return new ServiceResult<>(null, BaseErrorCode.PARAMS_INVALID, detail).asResult();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ServiceResult<Object> handleException(Exception ex){
        log.error(LOGGER_FORMAT, ex.getMessage(), ex);
        return new ServiceResult<>(null, BaseErrorCode.ERROR, ex.getMessage()).asResult();
    }
}
