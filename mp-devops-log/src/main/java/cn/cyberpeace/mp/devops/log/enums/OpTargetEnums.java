package cn.cyberpeace.mp.devops.log.enums;

import cn.cyberpeace.mp.base.annotation.SwaggerDisplayEnum;
import cn.cyberpeace.mp.devops.log.constant.LogConstant;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

@SwaggerDisplayEnum
public enum OpTargetEnums {

    // "用户"
    USER("user"),
    // "角色"
    ROLE("role"),
    // "组织"
    ORG("org"),
    // "资源"
    TARGET("target"),

    DEFAULT("");


    private String code;

    OpTargetEnums(String code) {
        this.code = LogConstant.LOG_OP_TARGET_PREFIX + "." + code;
    }

    public String getCode() {
        return code;
    }


    @JsonCreator
    public static OpTargetEnums fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public String toValue() {
        return this.getCode();
    }

    @Override
    public String toString() {
        return this.ordinal() + ":" + code;
    }
}
