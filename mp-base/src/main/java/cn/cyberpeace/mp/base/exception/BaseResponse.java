package cn.cyberpeace.mp.base.exception;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 返回值
 * @param <T>
 */
@Data
@Builder
@AllArgsConstructor
public class BaseResponse<T> implements Serializable {


    public BaseResponse(String message){
        this.message = message;
    }

    /**
     * 消息内容
     */
    private String message;

    /**
     * 内容
     */
    private T content;

    private String status;

    /**
     * 失败
     * @param message 消息
     * @return
     */
    public static <T> BaseResponse<T> error(String message){
        return new BaseResponse<>(message, null, "fail");
    }

    /**
     * 成功
     * @param context 内容
     * @return
     */
    public static <T> BaseResponse<T> success(T context){
        return new BaseResponse<>("", context, "success");
    }

    /**
     * 成功
     * @return
     */
    public static BaseResponse<String> success(){
        return new BaseResponse<String>("", null, "success");
    }
}
