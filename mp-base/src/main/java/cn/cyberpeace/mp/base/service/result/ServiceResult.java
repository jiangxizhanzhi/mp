package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.code.ServiceCode;
import cn.cyberpeace.mp.base.util.MessageUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceResult<T> {

    private Object data;

    private String code;

    private String message;

    private String detail;

    @JsonIgnore
    private ServiceCode serviceCode = BaseErrorCode.SUCCESS;

    @JsonIgnore
    private T source;

    public ServiceResult(T source) {
        setSource(source);
    }

    public ServiceResult(T source, ServiceCode code) {
        setSource(source);
        setServiceCode(code);
    }

    public ServiceResult(T source, ServiceCode code, String detail) {
        setSource(source);
        setServiceCode(code);
        setDetail(detail);
    }

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
}
