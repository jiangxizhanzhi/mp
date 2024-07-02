package cn.cyberpeace.mp.log.enums;

import cn.cyberpeace.mp.base.annotation.SwaggerDisplayEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName LogReportTypeEnum
 * @Description 日志上报类型
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Enum
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
        for (LogReportTypeEnum t : LogReportTypeEnum.values()) {
            map.put(t.name(), t);
        }
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
