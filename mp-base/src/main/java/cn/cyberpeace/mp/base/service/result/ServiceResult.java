package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.code.ServiceCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一服务返回结果
 * @param <T> 返回结果数据源对象类型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceResult<T> extends BaseServiceResult<T, T> {

    // 重复定义for Builder
    @ApiModelProperty(value = "响应数据")
    private T data;
    @ApiModelProperty(value = "错误码")
    private String code;
    @ApiModelProperty(value = "错误说明")
    private String message;
    @ApiModelProperty(value = "错误详细说明")
    private String detail;
    @ApiModelProperty(value = "服务器时间UTC毫秒")
    @Builder.Default
    private Long timestamp = System.currentTimeMillis();
    @JsonIgnore
    @Builder.Default
    private ServiceCode serviceCode = BaseErrorCode.SUCCESS;
    @JsonIgnore
    private T source;

    public ServiceResult(T source) {
        this();
        setSource(source);
    }

    public ServiceResult(T source, ServiceCode code) {
        this(source);
        setServiceCode(code);
    }

    public ServiceResult(T source, ServiceCode code, String detail) {
        this(source, code);
        setDetail(detail);
    }

    @Override
    public ServiceResult<T> asResult() {
        super.asResult();
        return this;
    }

    /**
     * 返回成功
     * @return
     */
    public static <T> ServiceResult<T> successful() {
        return new ServiceResult<>((T) null, BaseErrorCode.SUCCESS, MessageCommon.get(BaseErrorCode.SUCCESS.getMessage())).asResult();
    }

    /**
     * 返回成功，带数据
     * @param source
     * @return
     */
    public static <T> ServiceResult<T> success(T source) {
        return new ServiceResult<>(source).asResult();
    }

    /**
     * 返回失败
     * @param errorCode
     * @return
     */
    public static <T> ServiceResult<T> fail(ServiceCode errorCode) {
        return new ServiceResult<>((T) null, errorCode, MessageCommon.get(errorCode.getMessage())).asResult();
    }

    /**
     * 返回通用失败信息
     * @param msg
     * @return
     */
    public static <T> ServiceResult<T> fail(String msg) {
        return new ServiceResult<>((T) null, BaseErrorCode.ERROR, msg).asResult();
    }

    /**
     * 返回自定义失败信息
     * @param errorCode
     * @param msg
     * @return
     */
    public static <T> ServiceResult<T> fail(ServiceCode errorCode, String msg) {
        return new ServiceResult<>((T) null, errorCode, msg).asResult();
    }

}
