package cn.cyberpeace.mp.collection.model.contest;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RuntimeLogModel {
    private String ip;

    private String type;

    private String message;

    private String logType;

    private LocalDateTime time;
}
