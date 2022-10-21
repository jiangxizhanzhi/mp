package cn.cyberpeace.mp.collection.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContestLogModel {
    private String ip;

    private String attackIp;

    private Integer severity;

    private String logType;

    private String message;

    private LocalDateTime time;

    public static class Severity {
        private Severity() {

        }

        public static final Integer NORMAL = 0;

        public static final Integer ABNORMAL = 1;
    }
}
