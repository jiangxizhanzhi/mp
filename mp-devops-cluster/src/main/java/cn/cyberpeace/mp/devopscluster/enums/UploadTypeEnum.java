package cn.cyberpeace.mp.devopscluster.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName UploadTypeEnum
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Enum
 */
public enum UploadTypeEnum {

    FTP("ftp"),

    HTTP("http");

    private String desc;

    /**
     * UploadTypeEnum
     *
     * @param desc
     */
    UploadTypeEnum(String desc) {
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

    private static Map<String, UploadTypeEnum> map = new ConcurrentHashMap<>();

    static {
        for (UploadTypeEnum t : UploadTypeEnum.values()) {
            map.put(t.getDesc(), t);
        }
    }

    /**
     * fromValue
     *
     * @param desc
     * @return
     */
    @JsonCreator
    public static UploadTypeEnum fromValue(String desc) {
        return map.get(desc);
    }

    /**
     * toValue
     *
     * @return
     */
    @JsonValue
    public String toValue() {
        return this.getDesc();
    }
}
