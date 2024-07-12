 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowGroupByDestinationPortParams.java.
 // File size 637.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.service.param;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName FlowGroupByDestinationPortParams
 * @Description
 * @Author maojun
 * @Date 2022/2/24
 * @Version 1.0.0
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
