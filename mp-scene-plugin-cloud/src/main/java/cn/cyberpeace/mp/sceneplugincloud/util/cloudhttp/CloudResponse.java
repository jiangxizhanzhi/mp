package cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloudResponse<T> {
    public static final String SUCCESS_CODE = "0";
    public static final String EXCEPTION_CODE = "CP-099999";

    private T data;
    private List<T> items;
    private Integer total;
    private String code;
    private String message;
    private Object detail;
    private Long timestamp;
    private Boolean success;


    /**
     * setRequestError
     */
    public void setRequestError() {
        setCode(EXCEPTION_CODE);
    }

    /**
     * setParseError
     */
    public void setParseError() {
        setCode(EXCEPTION_CODE);
    }

    /**
     * isSuccess
     *
     * @return boolean
     */
    public boolean isSuccess() {
        return StringUtils.isEmpty(code) || SUCCESS_CODE.equals(code);
    }
}
