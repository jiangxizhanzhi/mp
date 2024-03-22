package cn.cyberpeace.mp.auth.util;

import com.alibaba.fastjson.JSONObject;

public class ResponseUtil<T> {

    /**
     * 成功响应
     *
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success() {
        return new BaseResponse<>(BaseStatusEnum.SUCCESS.getCode(), BaseStatusEnum.SUCCESS.getMessage());
    }

    /**
     * 成功响应返回数据
     *
     * @param t
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T t) {
        return new BaseResponse<>(BaseStatusEnum.SUCCESS.getCode(), BaseStatusEnum.SUCCESS.getMessage(), t);
    }

    /**
     * 成功响应返回数据
     *
     * @param status
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(BaseStatusEnum status) {
        return new BaseResponse<T>(status.getCode(), status.getMessage());
    }

    /**
     * 成功响应返回数据
     *
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(String code, String message) {
        return new BaseResponse<>(code, message);
    }

    /**
     * 成功响应返回数据
     *
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(String code, String message, T t) {
        return new BaseResponse<>(code, message, t);
    }


    /**
     * 错误响应返回数据
     *
     * @param code
     * @param message
     * @param t
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> error(String code, String message, T t) {
        return new BaseResponse<>(code, message, t);
    }

    /**
     * 返回自定义错误
     *
     * @param code
     * @param message
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> error(String code, String message) {
        return new BaseResponse<>(code, message);
    }

    /**
     * 是否成功
     *
     * @param response
     * @param code
     * @param message
     * @return
     */
    public static boolean isCallSuccess(BaseResponse<?> response, String code, String message) {
        // 业务成功
        if (BaseStatusEnum.SUCCESS.getCode().equals(code)) {
            return true;
        }

        // 业务失败，透传失败message
        response.setMessage(message);
        response.setCode(code);

        return false;
    }

    /**
     * 是否成功
     *
     * @param response
     * @return
     */
    public static boolean isCallSuccess(BaseResponse<?> response) {
        return response != null && BaseStatusEnum.SUCCESS.getCode().equals(response.getCode());
    }

    /**
     * 是否成功
     *
     * @param returnResp
     * @param receivedResp
     * @return
     */
    public static boolean isCallSuccess(BaseResponse<?> returnResp, BaseResponse receivedResp) {
        String code = receivedResp.getCode();
        String msg = receivedResp.getMessage();
        return isCallSuccess(returnResp, code, msg);
    }

    /**
     * 自定义返回数据
     *
     * @param status
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> error(BaseStatusEnum status) {
        return new BaseResponse<T>(status.getCode(), status.getMessage());
    }

    /**
     * 根据通用返回结果封装获取返回结果JsonObject
     *
     * @param error
     * @return
     */
    public static JSONObject getJsonResult(BaseResponse<Object> error) {
        JSONObject result = new JSONObject();
        result.put("code", error.getCode());
        result.put("message", error.getMessage());
        result.put("data", error.getData());
        return result;
    }

    /**
     * 根据结果、日志信息及数据返回结果JsonObject
     *
     * @param code
     * @param message
     * @param t
     * @param <T>
     * @return
     */
    public static <T> JSONObject getJsonResult(String code, String message, T t) {
        JSONObject result = new JSONObject();
        result.put("code", code);
        result.put("message", message);
        result.put("data", t);
        return result;
    }
}
