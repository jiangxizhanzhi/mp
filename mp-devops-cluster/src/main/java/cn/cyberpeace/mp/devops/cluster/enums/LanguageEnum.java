package cn.cyberpeace.mp.devops.cluster.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum LanguageEnum {

    CN("cn"),

    TC("tc"),

    EN("en"),

    FR("fr"),

    RU("ru"),

    JA("ja"),

    KO("ko"),

    VI("vi"),

    AR("ar"),

    OTHER("other");

    private String desc;

    LanguageEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    private static Map<String, LanguageEnum> map = new ConcurrentHashMap<>();

    static {
        Arrays.stream(LanguageEnum.values()).forEach(
                t -> map.put(t.getDesc(), t)
        );
    }

    @JsonCreator
    public static LanguageEnum fromValue(String desc) {
        return map.get(desc);
    }

    @JsonValue
    public String toValue() {
        return this.getDesc();
    }
}
