package cn.cyberpeace.mp.devops.log.enums;

import cn.cyberpeace.mp.base.annotation.SwaggerDisplayEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

@SwaggerDisplayEnum
public enum OpTypeEnums {
    CREATE("创建"),

    UPDATE("更新"),

    DELETE("删除"),

    LOGIN_IN_OUT("登录登出"),

    DEFAULT("");


    private String desc;

    OpTypeEnums(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }


    @JsonCreator
    public static OpTypeEnums fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public String toValue() {
        return this.getDesc();
    }

    @Override
    public String toString() {
        return this.ordinal() + ":" + desc;
    }
}
