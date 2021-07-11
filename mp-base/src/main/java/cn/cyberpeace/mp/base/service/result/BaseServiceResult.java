package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.code.MessageServiceCode;
import cn.cyberpeace.mp.base.service.code.ServiceCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseServiceResult<T, R> {

    @ApiModelProperty(value = "响应数据")
    private R data;

    @ApiModelProperty(value = "错误码")
    private String code;

    @ApiModelProperty(value = "错误说明")
    private String message;

    @ApiModelProperty(value = "错误详细说明")
    private String detail;

    @ApiModelProperty(value = "服务器时间UTC毫秒")
    private Long timestamp = System.currentTimeMillis();

    @JsonIgnore
    private ServiceCode serviceCode = BaseErrorCode.SUCCESS;

    @JsonIgnore
    private T source;

    public BaseServiceResult(T source) {
        this();
        setSource(source);
    }

    public BaseServiceResult(T source, ServiceCode code) {
        this(source);
        setServiceCode(code);
    }

    public BaseServiceResult(T source, ServiceCode code, String detail) {
        this(source, code);
        setDetail(detail);
    }

    public boolean judgeSuccess() {
        return BaseErrorCode.SUCCESS.getCode().equals(getServiceCode().getCode());
    }

    
    public boolean isSuccess() {
        return BaseErrorCode.SUCCESS.getCode().equals(getServiceCode().getCode());
    }

    public R sourceToData(T source) {
        if (source == null) {
            return null;
        }

        return (R) source;
    }

    
    public String calcMessage() {
        ServiceCode actualServiceCode = getServiceCode();
        if (actualServiceCode != null) {
            if (actualServiceCode instanceof MessageServiceCode) {
                return ((MessageServiceCode) actualServiceCode).transMessage();
            } else {
                return MessageCommon.get(actualServiceCode.getMessage());
            }
        }
        return null;
    }

    
    public BaseServiceResult<T, R> asResult() {
        setData(sourceToData(getSource()));

        ServiceCode actualServiceCode = getServiceCode();
        if (actualServiceCode != null) {
            setCode(actualServiceCode.getCode());
            setMessage(actualServiceCode.transMessage());
        }

        return this;
    }

}
