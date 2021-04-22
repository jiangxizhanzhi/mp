package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.code.ServiceCode;
import cn.cyberpeace.mp.base.util.MessageUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResult {

    private Object data = null;

    private String code = null;

    private String message = null;

    private String detail = null;

    @JsonIgnore
    private ServiceCode serviceCode = BaseErrorCode.SUCCESS;

    @JsonIgnore
    private BaseResult dataResult = null;

    public ServiceResult(Object data) {
        setData(data);
    }

    public ServiceResult(Object data, ServiceCode code) {
        setData(data);
        setServiceCode(code);
    }

    public ServiceResult(Object data, ServiceCode code, String detail) {
        setData(data);
        setServiceCode(code);
        setDetail(detail);
    }


    public ServiceResult(BaseResult dataResult) {
        setDataResult(dataResult);
    }

    public ServiceResult(BaseResult dataResult, ServiceCode code) {
        setDataResult(dataResult);
        setServiceCode(code);
    }

    public ServiceResult(BaseResult dataResult, ServiceCode code, String detail) {
        setDataResult(dataResult);
        setServiceCode(code);
        setDetail(detail);
    }

    public ServiceResult asResult() {
        if (dataResult != null) {
            data = dataResult.asResult();
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
