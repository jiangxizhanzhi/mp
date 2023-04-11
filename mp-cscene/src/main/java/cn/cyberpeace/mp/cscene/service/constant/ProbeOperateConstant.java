package cn.cyberpeace.mp.cscene.service.constant;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @BelongsPackage: cn.cyberpeace.mp.cscene.service.constant
 * @Author: hhuang
 * @CreateTime: 2023/04/11  11:56
 * @Description: 探针状态开关相关常量
 * @Version: 1.0
 */
public class ProbeOperateConstant {

    //记录数为1
    public static final int ONE_NUM = 1;
    //开关开启
    public static final int OPEN_SWITCH = 1;
    //开关关闭
    public static final int CLOSE_SWITCH = 0;
    //一个较早的时间作为最小时间
    public static final LocalDateTime  minTime = LocalDateTime.parse("2013-10-28 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    public enum PROBEOPERATE_TYPE {

        CHANGE_EVENT_STATE("changeEventState","变更事件开关状态"),
        ORIGIN_LOG("originLog","原始日志开关状态"),
        LOG_EVENT("logEvent","日志事件开关状态"),
        CUSTOM_COLLECTION("customCollection","自定义采集开关状态");

        private final String value;

        private final String name;

        PROBEOPERATE_TYPE(String value,String name) {
            this.value = value;
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }
}
