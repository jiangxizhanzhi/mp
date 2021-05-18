package cn.cyberpeace.mp.base.exception;

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
    public BaseResponse b2bRuntimeExceptionHandler(MqRuntimeException ex) {
        log.error(LOGGER_FORMAT, ex.getMessage(), ex);
        return new BaseResponse(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public BaseResponse validationExceptionHandle(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage());
        return BaseResponse.error(StringUtils.isEmpty(ex.getMessage())? "请求参数错误": ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public BaseResponse handleException(Exception ex){
        log.error(LOGGER_FORMAT, ex.getMessage(), ex);
        return BaseResponse.error(StringUtils.isEmpty(ex.getMessage())? "系统异常": ex.getMessage());
    }
}
