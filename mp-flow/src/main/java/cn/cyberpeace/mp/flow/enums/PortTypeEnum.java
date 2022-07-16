package cn.cyberpeace.mp.flow.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 网口类型：0 pcap包，1 脚本
 */
public enum PortTypeEnum {

    PCAP("PCAP"),

    SCRIPT("SCRIPT");

    private final String resource;

    PortTypeEnum(String resource) {
        this.resource = resource;
    }

    @Override
    public String toString() {
        return resource;
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
