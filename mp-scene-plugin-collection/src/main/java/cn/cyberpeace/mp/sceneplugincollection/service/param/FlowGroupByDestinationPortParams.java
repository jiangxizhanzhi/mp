package cn.cyberpeace.mp.sceneplugincollection.service.param;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Setter
@Getter
public class FlowGroupByDestinationPortParams {

    private Integer topSize;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private List<String> sceneIds;

    private List<String> nodeIds;

    
    private List<String> sourceIps;
}
