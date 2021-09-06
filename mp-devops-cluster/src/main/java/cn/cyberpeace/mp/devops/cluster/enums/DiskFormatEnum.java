package cn.cyberpeace.mp.devops.cluster.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum DiskFormatEnum {

    QCOW2("qcow2"),

    VMDK("vmdk"),

    DOCKER("docker"),

    PLOOP("ploop"),

    VHD("vhd"),

    VDI("vdi"),

    AKI("aki"),

    AMI("ami"),

    ARI("ari"),

    ISO("iso"),

    OVA("ova"),

    RAW("raw");

    private String desc;

    DiskFormatEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    private static Map<String, DiskFormatEnum> map = new ConcurrentHashMap<>();

    static {
        Arrays.stream(DiskFormatEnum.values()).forEach(
                t -> map.put(t.getDesc(), t)
        );
    }

    @JsonCreator
    public static DiskFormatEnum fromValue(String desc) {
        return map.get(desc);
    }

    @JsonValue
    public String toValue() {
        return this.getDesc();
    }

}
