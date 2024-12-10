package cn.cyberpeace.mp.situationaware.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class EsSyslog {

    @JSONField(name = "scene_id")
    private String sceneId;

    @JSONField(name = "node_id")
    private String nodeId;

    @JSONField(name = "node_name")
    private String nodeName;

    private String ip;
    @JSONField(name = "@timestamp")
    private String timestamp;
    private String message;
    @JSONField(name = "log")
    private Log log;

    @Data
    public static class Log {
        @JSONField(name = "syslog")
        private Syslog syslog;
    }

    @Data
    public static class Syslog {
        @JSONField(name = "facility")
        private Facility facility;

        private Integer priority;

        @JSONField(name = "severity")
        private Severity severity;
    }

    @Data
    public static class Facility {
        private String code;
        private String name;
    }

    @Data
    public static class Severity {
        private String code;
        private String name;
    }

}
