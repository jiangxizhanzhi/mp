package cn.cyberpeace.mp.auth.util;

import lombok.Data;

@Data
public class BaseResponse<T> {
    /**
     * 返回码
     */
    protected String code;

    /**
     * 消息
     */
    protected String message;

    /**
     * 返回对象
     */
    protected T      data;

    public BaseResponse() {
    }

    public BaseResponse(String code) {
        this.code = code;
    }

    public BaseResponse(String code, T data) {
        this.code = code;
        this.data = data;
    }

    public BaseResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
