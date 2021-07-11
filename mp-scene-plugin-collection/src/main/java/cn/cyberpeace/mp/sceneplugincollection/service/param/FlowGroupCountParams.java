package cn.cyberpeace.mp.sceneplugincollection.service.param;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Setter
@Getter
public class FlowGroupCountParams {
    private Integer topSize;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String queryType;
    private List<String> queryAddress;

    
    private List<String> sourceIps;

    public FlowGroupCountParams(String queryType) {
        this.queryType = queryType;
    }
}
