package cn.cyberpeace.mp.cscene.controller.request;

import lombok.Data;

public class EsAlarmInfo {

    @Data
    public static class FileEsAlarmInfo extends EsAlarmInfo {
        private String path;
        private String agentName;
        private Integer fileSize;
        private String action;
    }
}