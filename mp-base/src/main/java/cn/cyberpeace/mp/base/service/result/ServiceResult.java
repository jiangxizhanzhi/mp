package cn.cyberpeace.mp.base.service.result;

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
    @JsonIgnore
    @Builder.Default
    private ServiceCode serviceCode = BaseErrorCode.SUCCESS;
    @JsonIgnore
    private T source;

    public ServiceResult(T source) {
        this();
        setSource(source);
        this.asResult();
    }

    public ServiceResult(T source, ServiceCode code) {
        this();
        setSource(source);
        setServiceCode(code);
    }

    public ServiceResult(T source, ServiceCode code, String detail) {
        this();
        setSource(source);
        setServiceCode(code);
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
    public static ServiceResult successful() {
        return ServiceResult.builder()
                .code(BaseErrorCode.SUCCESS.getCode())
                .message(BaseErrorCode.SUCCESS.toString())
                .build();
    }

    /**
     * 返回成功，带数据
     * @param source
     * @return
     */
    public static ServiceResult success(Object source) {
        return ServiceResult.builder()
                .code(BaseErrorCode.SUCCESS.getCode())
                .message(BaseErrorCode.SUCCESS.toString())
                .data(source)
                .build();
    }

    /**
     * 返回失败
     * @param errorCode
     * @return
     */
    public static ServiceResult fail(ServiceCode errorCode) {
        return ServiceResult.builder()
                .code(errorCode.getCode())
                .message(errorCode.toString())
                .build();
    }

    /**
     * 返回通用失败信息
     * @param msg
     * @return
     */
    public static ServiceResult fail(String msg) {
        return ServiceResult.builder()
                .code(BaseErrorCode.ERROR.getCode())
                .message(msg)
                .build();
    }

    /**
     * 返回自定义失败信息
     * @param errorCode
     * @param msg
     * @return
     */
    public static ServiceResult fail(ServiceCode errorCode, String msg) {
        return ServiceResult.builder()
                .code(errorCode.getCode())
                .message(msg)
                .build();
    }

    /**
     * 返回自定义失败信息
     * @param errorCode
     * @param msg
     * @return
     */
    public static ServiceResult fail(ServiceCode errorCode, String msg, String detail) {
        return ServiceResult.builder()
            .code(errorCode.getCode())
            .detail(detail)
            .message(msg)
            .build();
    }

}
