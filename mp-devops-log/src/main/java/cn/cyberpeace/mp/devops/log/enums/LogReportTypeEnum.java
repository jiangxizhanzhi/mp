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

    REPORT("report_"),
    // 中台
    MP("mp_"),
    // 靶场
    BC("cr_"),
    // 云平台
    CP("cp_"),
    // 竞赛
    AD("ad_"),
    // 实训
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
