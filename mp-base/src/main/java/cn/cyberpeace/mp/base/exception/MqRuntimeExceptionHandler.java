package cn.cyberpeace.mp.base.exception;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import javax.naming.directory.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
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
public class MqRuntimeExceptionHandler {

    private static final String LOGGER_FORMAT = "操作执行异常:,异常信息：{},堆栈信息：{}";

    @ExceptionHandler(MqRuntimeException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ServiceResult<String> b2bRuntimeExceptionHandler(MqRuntimeException ex) {
        log.error(LOGGER_FORMAT, ex.getMessage(), ex);
        return ServiceResult.<String>builder().message(ex.getMessage()).serviceCode(BaseErrorCode.ERROR).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ServiceResult<String> validationExceptionHandle(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage());
        return ServiceResult.<String>builder().message("参数异常" + ex.getMessage()).serviceCode(BaseErrorCode.PARAMS_INVALID).build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ServiceResult<String> handleException(Exception ex){
        log.error(LOGGER_FORMAT, ex.getMessage(), ex);
        return ServiceResult.<String>builder().message("系统异常" + ex.getMessage()).serviceCode(BaseErrorCode.PARAMS_INVALID).build();
    }
}
