package cn.cyberpeace.mp.aggregation.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @Author zhanglin
 * @Date 2023/3/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CplcoudResponse<T> {

    public static final String EXCEPTION_CODE = "MP-999999";

    private String code;
    private String msg;
    private T data;

    public void setRequestError() {
        setCode(EXCEPTION_CODE);
    }

    public void setParseError() {
        setCode(EXCEPTION_CODE);
    }

    public boolean isSuccess() {
        return "1000".equalsIgnoreCase(code);
    }

    public String getMessage() {
        return msg;
    }

    public String getDetail() {
        return msg;
    }
}