package cn.cyberpeace.mp.auth.util;

public enum ParentCodeEnum {

    PARENT_MIXCLOUD_CATEGORY_TYPE("1002", "混合云配置项菜单"),

    PARENT_PRIVATECLOUD_CATEGORY_TYPE("1003", "私有云配置项菜单"),

    PARENT_PUBLICCLOUD_CATEGORY_TYPE("1004", "公有云配置项菜单");

    private String code;
    private String message;

    ParentCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
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
