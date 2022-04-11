package cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp;

import cn.cyberpeace.mp.base.common.MessageCommon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloudResponse<T> {
    public static final String SUCCESS_CODE = "CP000000";
    public static final String SUCCESS_CODE0 = "0";
    public static final String EXCEPTION_CODE = "CP999999";

    private T data;
    private String code;
    private String message;
    private Object detail;
    private Integer timestamp;
    private Float timeElapse;
    private Boolean success;

    public void setRequestError() {
        setCode(EXCEPTION_CODE);
        setMessage(MessageCommon.get("sceneplugincloud.http.request_error"));
    }

    public void setParseError() {
        setCode(EXCEPTION_CODE);
        setMessage(MessageCommon.get("sceneplugincloud.http.parse_error"));
    }

    public boolean isSuccess() {
        return SUCCESS_CODE.equals(code) || SUCCESS_CODE0.equals(code) || Boolean.TRUE.equals(success);
    }
}
