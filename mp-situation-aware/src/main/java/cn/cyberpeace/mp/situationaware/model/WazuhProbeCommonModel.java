 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WazuhProbeCommonModel.java.
 // File size 800.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WazuhProbeCommonModel {

    @ApiModelProperty("agentId")
    private String agentId;

    @ApiModelProperty("agentName")
    private String agentName;

    @JSONField(name = "timestamp")
    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ApiModelProperty("输出数据")
    private String rawOutPut;

    @ApiModelProperty("探针id")
    private String probeId;
}
