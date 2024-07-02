package cn.cyberpeace.mp.devopscluster.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @ClassName AlarmTypeEnum
 * @Description 告警类型枚举类：0 云平台服务, 1 业务服务 2 服务器
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Enum
 */
public enum AlarmTypeEnum {

    CP("CP"),

    CR("CR"),

    SERVER("SERVER");

    private String desc;

    /**
     * AlarmTypeEnum
     *
     * @param desc
     */
    AlarmTypeEnum(String desc) {
        this.desc = desc;
    }

    /**
     * getDesc
     *
     * @return
     */
    public String getDesc() {
        return desc;
    }

    /**
     * toValue
     *
     * @return int
     */
    @JsonValue
    public int toValue(){
       return this.ordinal();
    }
}
