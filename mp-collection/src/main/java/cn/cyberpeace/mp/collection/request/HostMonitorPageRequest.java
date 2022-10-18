package cn.cyberpeace.mp.collection.request;

import cn.cyberpeace.mp.collection.request.base.BaseQueryPageRequest;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class HostMonitorPageRequest extends BaseQueryPageRequest {
    private List<String> agentNames;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
