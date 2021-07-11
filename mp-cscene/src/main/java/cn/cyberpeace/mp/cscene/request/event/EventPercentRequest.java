package cn.cyberpeace.mp.cscene.request.event;

import cn.cyberpeace.mp.cscene.request.common.BaseTimeRangeRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class EventPercentRequest extends BaseTimeRangeRequest {

    @ApiModelProperty("事件类型 0:所有 1:进程 2:端口 3:网络连接 4:文件 5:用户")
    private Integer type;

    @ApiModelProperty("默认获取前几个节点")
    private Integer topCount;

}
