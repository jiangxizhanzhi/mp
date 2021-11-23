package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.code.ServiceCode;
import cn.cyberpeace.mp.base.common.MessageCommon;
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
     * @Deprecated 不允许返回没有类型的ServiceResult
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
     * @Deprecated 不允许返回没有类型的ServiceResult
     * 返回成功，带数据
     * @param source
     * @return
     */
    public static ServiceResult success(Object source) {
        return ServiceResult.builder()
                .code(BaseErrorCode.SUCCESS.getCode())
                .message(BaseErrorCode.SUCCESS.toString())
                .data(source)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * @Deprecated 不允许返回没有类型的ServiceResult
     * 返回失败
     * @param errorCode
     * @return
     */
    public static ServiceResult fail(ServiceCode errorCode) {
        return new ServiceResult<>("", errorCode, MessageCommon.get(errorCode.getMessage())).asResult();
    }

    /**
     * @Deprecated 不允许返回没有类型的ServiceResult
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
     * @Deprecated 不允许返回没有类型的ServiceResult
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

}
