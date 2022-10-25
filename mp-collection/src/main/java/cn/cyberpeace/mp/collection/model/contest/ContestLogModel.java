package cn.cyberpeace.mp.collection.model.contest;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContestLogModel {
    private String ip;

    @JSONField(name = "attackIp")
    private String attackIp;

    private Integer severity;

    private String message;

    private LocalDateTime time = LocalDateTime.now();
}
