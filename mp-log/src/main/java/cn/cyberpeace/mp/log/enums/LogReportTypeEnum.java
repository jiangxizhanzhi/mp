package cn.cyberpeace.mp.log.enums;

import cn.cyberpeace.mp.base.annotation.SwaggerDisplayEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@SwaggerDisplayEnum
public enum LogReportTypeEnum {

    REPORT("report_"),
    
    MP("mp_"),
    
    BC("cr_"),
    
    CP("cp_"),
    
    AD("ad_"),
    
    OJ("oj_");

    private String prefix;

    LogReportTypeEnum(String prefix) {
        this.prefix = prefix;
    }

    private static Map<String, LogReportTypeEnum> map = new ConcurrentHashMap<>();

    static {
        Arrays.stream(LogReportTypeEnum.values()).forEach(
                t -> map.put(t.name(), t)
        );
    }

    public String getPrefix() {
        return prefix;
    }

    @JsonCreator
    public static LogReportTypeEnum fromValue(String desc) {
        return map.get(desc);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
