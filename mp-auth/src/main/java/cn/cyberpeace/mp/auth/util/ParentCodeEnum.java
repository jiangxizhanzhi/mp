package cn.cyberpeace.mp.auth.util;

import java.util.HashMap;
import java.util.Map;

public enum ParentCodeEnum {

    UNKNOWN_ERROR("0000", "发生未知错误"),
    PARENT_SYSTEM_TYPE("1001", "操作系统"),

    PARENT_MIXCLOUD_CATEGORY_TYPE("1002", "混合云配置项菜单"),

    PARENT_PRIVATECLOUD_CATEGORY_TYPE("1003", "私有云配置项菜单"),

    PARENT_PUBLICCLOUD_CATEGORY_TYPE("1004", "公有云配置项菜单");

    private String code;
    private String message;

    ParentCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private static final Map<String, ParentCodeEnum> intToEnum = new HashMap<>();

    static {
        for (ParentCodeEnum p : values()) {
            intToEnum.put(p.getCode(), p);
        }
    }

    public static ParentCodeEnum fromInt(Integer type) {
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
