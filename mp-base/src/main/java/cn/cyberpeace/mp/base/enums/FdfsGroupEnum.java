package cn.cyberpeace.mp.base.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * FastDFS集群枚举类
 */
public enum FdfsGroupEnum {

    DEFAULT("DEFAULT");

    private final String group;

    FdfsGroupEnum(String group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return group;
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
