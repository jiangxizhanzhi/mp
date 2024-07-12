 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WazuhMonitorEventResponse.java.
 // File size 622.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 监控指标返回结果
 * @Author qiaokang
 * @Date：2021/6/22 17:20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WazuhMonitorEventResponse {

    @ApiModelProperty("监控指标集合")
    private List<WazuhHostStateModel> monitorEventModelList;

    @ApiModelProperty("指标总数")
    private int total;

}
