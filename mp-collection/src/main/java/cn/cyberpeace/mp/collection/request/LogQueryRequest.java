package cn.cyberpeace.mp.collection.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class LogQueryRequest {
    private List<String> ips;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer severity;

    private List<String> attackIps;

    private Integer pageNum;

    private Integer pageSize;
}
