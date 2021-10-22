package cn.cyberpeace.mp.base.exception;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Objects;

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
        log.error(LOGGER_FORMAT, ex.getServiceCode().getMessage(), ex.getDetail());
        return new ServiceResult<>("", ex.getServiceCode(),ex.getDetail()).asResult();
    }

    @ExceptionHandler({BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ServiceResult<String> validationExceptionHandle(BindException ex) {
        log.error(ex.getMessage());
        ObjectError objectError = ex.getBindingResult().getAllErrors().get(0);
        BindingResult bindingResult = ex.getBindingResult();
        if(Objects.nonNull(bindingResult)){
            if(bindingResult.hasErrors()){
                FieldError fieldError = bindingResult.getFieldError();
                String field = fieldError.getField();
                String defaultMessage = fieldError.getDefaultMessage();
                return ServiceResult.fail(BaseErrorCode.PARAMS_INVALID,field + ":" + defaultMessage);
            }else {
                return ServiceResult.fail(BaseErrorCode.PARAMS_INVALID,objectError.getDefaultMessage());
            }
        }else {
            return ServiceResult.fail(BaseErrorCode.PARAMS_INVALID,objectError.getDefaultMessage());
        }
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ServiceResult<String> handleException(Exception ex){
        log.error(LOGGER_FORMAT, ex.getMessage(), ex);
        return ServiceResult.fail("系统异常：" + ex.getMessage());
    }
}
