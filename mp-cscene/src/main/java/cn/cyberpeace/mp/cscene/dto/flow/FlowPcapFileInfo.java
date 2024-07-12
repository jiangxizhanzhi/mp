 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowPcapFileInfo.java.
 // File size 464.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.dto.flow;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName FlowPcapFileInfo
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowPcapFileInfo {
    @ApiModelProperty("文件生成任务id")
    private String taskId ;
}
