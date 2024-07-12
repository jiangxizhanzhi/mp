 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name EventPercentRequest.java.
 // File size 645.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.request.event;

import cn.cyberpeace.mp.cscene.request.common.BaseTimeRangeRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName EventPercentRequest
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/3/14 14:15
 * @Version 1.0.0
 * @see BaseTimeRangeRequest
 */
@Data
public class EventPercentRequest extends BaseTimeRangeRequest {

    @ApiModelProperty("事件类型 0:所有 1:进程 2:端口 3:网络连接 4:文件 5:用户")
    private Integer type;

    @ApiModelProperty("默认获取前几个节点")
    private Integer topCount;

}
