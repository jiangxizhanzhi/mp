package cn.cyberpeace.mp.sceneplugincollection.service.param;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/2/24
 */
@Setter
@Getter
public class FlowGroupByDestinationPortParams {

    private Integer topSize;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private List<String> sceneIds;

    private List<String> nodeIds;

    /**
     * 源IP集合
     */
    private List<String> sourceIps;
}
