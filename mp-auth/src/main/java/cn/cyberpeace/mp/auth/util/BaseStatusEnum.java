package cn.cyberpeace.mp.auth.util;

import java.util.HashMap;
import java.util.Map;

public enum BaseStatusEnum {


    SUCCESS("200", "成功"),
    UNKNOWN_ERROR("201", "发生未知错误"),
    DUPLICATE_OPERATE("202", "重复操作"),
    RPC_ERROR("203", "RPC调用失败"),
    RECORD_NOT_FOUND("204", "记录不存在"),
    PARAM_ERROR("205", "参数错误"),
    NOT_SUPPORTED("206", "不支持此方法"),
    PARAMETER_IS_NULL("207", "请求参数为空"),
    DATA_NOT_EXIST("208", "查询结果为空"),
    TOKEN_IS_NULL("209", "用户信息为空"),
    FILE_ERROR("210", "文件下载失败"),
    WORKFLOWCODE_ERROR("211", "查询流程编码失败"),
    SESSION_INVALIDATE("212", "用户会话失效"),
    DB_OPERATE_ERROR("213", "数据库操作失败"),
    INFO_HAS_EXIST("214", "信息已存在"),
    HTTPREST_ERROR("215", "第三方HTTP_REST接口调用失败"),
    URL_IS_NULL("216", "URL为空"),
    CONNECTION_ERROR("217", "测试连接失败"),
    CONNECTION_IS_SUCCESS("218", "请先测试成功，才能保存"),
    CONNECTION_IS_ERROE("500", "该表所处数据库连接异常，无法查出表属性"),
    USER_UN_AUTH("219", "未检测到服务器登录认证"),
    USER_AUTH_ERROR("220", "用户登录认证异常"),
    FEIGN_CALL_ERROR("221", "feign服务调用失败"),
    PARAMETER_FORMAT_ERROR("222", "参数格式不正确"),
    FILE_IS_NULL("223", "文件为空"),
    FILEINFOS_IS_NULL("224", "文件内信息为空"),
    FILENAME_ERROR("225", "文件名称不合法"),
    FILETYPE_ERROR("226", "文件名称不合法"),
    FILE_SIZE_MAX("227", "上传文件不超过10M"),
    FILE_SIZE_MIN("228", "上传文件不不能低于0M"),
    USERNAME_IS_NULL("229", "用户名为空"),
    ID_IS_NULL("230", "ID为空"),
    SAVE_IS_ERROR("231", "该库连接异常，同步失败"),
    METHOD_IS_USE("232", "该方法正在调用，请稍后再点"),
    LISTING_IS_NULL("233", "列名为空，请配置!"),
    VALIDATION_IS_NULL("411", "用户名或密码为空"),
    VALIDATION_FAILED("410", "用户名或密码错误"),
    URL_NOT_FOUND("404", "访问路径不存在"),
    CODE_500("500", "系统异常");


    private String code;
    private String message;

    BaseStatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private static final Map<String, BaseStatusEnum> intToEnum = new HashMap<>();

    static {
        for (BaseStatusEnum p : values()) {
            intToEnum.put(p.getCode(), p);
        }
    }

    public static BaseStatusEnum fromInt(Integer type) {
        try {
            return intToEnum.get(type);
        } catch (Exception e) {
            return UNKNOWN_ERROR;
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
