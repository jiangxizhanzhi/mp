package cn.cyberpeace.mp.devops.cluster.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 字体颜色枚举信息
 */
public enum FontColorEnum {

    RED("红色"),

    GREEN("绿色");

    private String desc;

    FontColorEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    @JsonValue
    public int toValue(){
       return this.ordinal();
    }
}
