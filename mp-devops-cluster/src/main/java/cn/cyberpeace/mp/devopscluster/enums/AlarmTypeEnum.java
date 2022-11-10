package cn.cyberpeace.mp.devopscluster.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 告警类型枚举类：0 云平台服务, 1 业务服务 2 服务器, 3 虚拟网络(子网数不足), 4 虚拟网络(网络IO负载过高)
 */
public enum AlarmTypeEnum {

    CP("CP"),

    CR("CR"),

    SERVER("SERVER"),

    NETWORK_IP_USAGE("NETWORK_IP_USAGE"),

    NETWORK_IO_USAGE("NETWORK_IO_USAGE");

    private String desc;

    AlarmTypeEnum(String desc) {
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
