package cn.cyberpeace.mp.log.enums;

import cn.cyberpeace.mp.base.annotation.SwaggerDisplayEnum;

@SwaggerDisplayEnum
public enum LogDBEnums {

    ES("es"),
    MYSQL("mysql");

    private String code;

    LogDBEnums(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
