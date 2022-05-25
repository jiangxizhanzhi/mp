package cn.cyberpeace.mp.auth.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 角色名称
 */
public enum RoleNameEnum {

    MANAGER("管理员"),

    TEACHER("教员"),

    STUDENT("学员");

    private final String desc;

    RoleNameEnum(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
