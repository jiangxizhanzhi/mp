package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.code.ServiceCode;
import cn.cyberpeace.mp.base.util.MessageUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一服务返回结果
 * @param <T> 返回结果数据源对象类型
 */
@ApiModel(value = "object", description = "响应结果")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceResult<T> {

    @ApiModelProperty(value = "响应数据")
    private Object data;

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

    /**
     * 从数据源加载最终序列化数据
     * @return this
     */
    public ServiceResult<T> asResult() {
        if (source instanceof BaseObjResult) {
            data = ((BaseObjResult) source).asResult();
        } else {
            data = source;
        }

        if (serviceCode != null) {
            setCode(serviceCode.getCode());
            setMessage(serviceCode.getMessage());
        }

        return this;
    }

    public void setMessage(String message) {
        this.message = MessageUtils.get(message);
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
    public static ServiceResult fail(BaseErrorCode errorCode) {
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

}
