 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowNodeTxBps.java.
 // File size 617.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FlowNodeTxBps
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@ApiModel("节点实时速率对象")
public class FlowNodeTxBps {

    /**
     * 节点名称
     */
    @ApiModelProperty(value = "节点名称")
    private String nodeName;

    /**
     * 实时发送速率(单位byte/s)
     */
    @ApiModelProperty(value = "实时发送速率(单位byte/s)")
    private Long txBps;

}
