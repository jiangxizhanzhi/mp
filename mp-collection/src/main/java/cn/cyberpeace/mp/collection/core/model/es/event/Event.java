package cn.cyberpeace.mp.collection.core.model.es.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class Event {

    /**
     * 文件路径
     */
    private String filePath;

    //文件大小
    private String size;

    private String md5Before;

    private String md5After;

    //文件的时间
    private LocalDateTime updateTime;

    private String user;

    /**
     * 文件事件类型
     */
    private String action;

    private String agentId;

    private String agentName;

    private Integer level;

    private String description;

    private String scrollId;

    private String ruleId;

    private String message;

    private String agentIp;
}
