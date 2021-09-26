package cn.cyberpeace.mp.devops.cluster.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum UploadTypeEnum {

    FTP("ftp"),

    HTTP("http");

    private String desc;

    UploadTypeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    private static Map<String, UploadTypeEnum> map = new ConcurrentHashMap<>();

    static {
        Arrays.stream(UploadTypeEnum.values()).forEach(
                t -> map.put(t.getDesc(), t)
        );
    }

    @JsonCreator
    public static UploadTypeEnum fromValue(String desc) {
        return map.get(desc);
    }

    @JsonValue
    public String toValue() {
        return this.getDesc();
    }
}
