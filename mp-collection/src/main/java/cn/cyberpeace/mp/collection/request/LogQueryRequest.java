package cn.cyberpeace.mp.collection.request;

import lombok.Data;

import java.util.List;

@Data
public class LogQueryRequest {
    private List<String> ips;

    private String startTime;

    private String endTime;

    private List<Integer> severity;

    private List<String> attackIps;

    private Integer pageNum;

    private Integer pageSize;
}
