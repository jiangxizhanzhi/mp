package cn.cyberpeace.mp.base.exception;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpServerErrorException;


@ControllerAdvice
@Slf4j
public class MpRuntimeExceptionHandler {

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
        var detail = StringUtils.isEmpty(ex.getDetail()) ? ex.getMessage() : ex.getDetail();
        return new ServiceResult<>(null, serviceCode, detail).asResult();
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

    @ExceptionHandler(HttpServerErrorException.GatewayTimeout.class)
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    @ResponseBody
    public ServiceResult handException(Exception ex) {
        log.error(ex.getMessage());
        return new ServiceResult(null,BaseErrorCode.GATEWAY_TIME_OUT).asResult();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ServiceResult<Object> handleException(Exception ex){
        log.error(LOGGER_FORMAT, ex.getMessage(), ExceptionUtils.getStackTrace(ex));
        return new ServiceResult<>(null, BaseErrorCode.ERROR, ex.getMessage()).asResult();
    }
}
