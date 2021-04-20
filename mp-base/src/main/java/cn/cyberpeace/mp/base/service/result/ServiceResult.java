package cn.cyberpeace.mp.base.service.result;

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

    @JsonIgnore
    private BaseResult dataResult = null;

    public ServiceResult(Object data) {
        setData(data);
    }

    public ServiceResult(Object data, String code, String message) {
        setData(data);
        setCode(code);
        setMessage(message);
    }

    public ServiceResult(BaseResult dataResult) {
        setDataResult(dataResult);
    }

    public ServiceResult(BaseResult dataResult, String code, String message) {
        setDataResult(dataResult);
        setCode(code);
        setMessage(message);
    }

    public ServiceResult asResult() {
        if (dataResult != null) {
            data = dataResult.asResult();
        }

        return this;
    }
}
