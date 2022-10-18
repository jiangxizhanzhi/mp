package cn.cyberpeace.mp.collection.request;

import cn.cyberpeace.mp.collection.request.base.BaseQueryPageRequest;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ModifyEventRequest extends BaseQueryPageRequest {
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private List<String> agentNames;

    private List<Integer> levels;

    private String path;
}
