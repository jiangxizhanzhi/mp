package cn.cyberpeace.mp.devops.log.enums;

import cn.cyberpeace.mp.base.annotation.SwaggerDisplayEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日志上报类型
 */
@SwaggerDisplayEnum
public enum LogReportTypeEnum {

    MP,

    BC,

    CP;


    LogReportTypeEnum() {

    }

    private static Map<String, LogReportTypeEnum> map = new ConcurrentHashMap<>();

    static {
        Arrays.stream(LogReportTypeEnum.values()).forEach(
                t -> map.put(t.name(), t)
        );
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
