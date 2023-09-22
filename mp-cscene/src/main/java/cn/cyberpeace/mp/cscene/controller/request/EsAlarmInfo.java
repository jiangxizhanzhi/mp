package cn.cyberpeace.mp.cscene.controller.request;

import lombok.Data;

@Data
public class EsAlarmInfo {
    private String time;
    private String agentName;

    @Data
    public static class RegistryAlarmInfo extends EsAlarmInfo {
        private String path;
        private String action;
        private String md5;
    }

    @Data
    public static class FileEsAlarmInfo extends EsAlarmInfo {
        private String path;
        private Integer fileSize;
        private String action;
        private String md5;
    }

    @Data
    public static class ProcessAlarmInfo extends EsAlarmInfo {
        private String sicLaunchOutput;
    }
}