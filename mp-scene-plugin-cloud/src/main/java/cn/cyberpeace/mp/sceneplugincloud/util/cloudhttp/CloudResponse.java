 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudResponse.java.
 // File size 1184.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @ClassName CloudResponse
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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
