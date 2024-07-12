 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowGroupByDestinationIpAndSeverityBO.java.
 // File size 811.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName FlowGroupByDestinationIpAndSeverityBO
 * @Description
 * @Author zhouxinghuai
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowGroupByDestinationIpAndSeverityBO {

    @ApiModelProperty("危险等级")
    private Integer severity;

    @ApiModelProperty("目的IP集合")
    private List<String> destinationIpList;

    private Integer num;

    public FlowGroupByDestinationIpAndSeverityBO(Integer severity, Integer num) {
        this.severity = severity;
        this.num = num;
    }
}
