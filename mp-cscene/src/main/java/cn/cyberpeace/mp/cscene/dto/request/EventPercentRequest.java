package cn.cyberpeace.mp.cscene.dto.request;

import cn.cyberpeace.mp.cscene.dto.common.BaseTimeRangeRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/3/14 14:15
 */
@Data
public class EventPercentRequest extends BaseTimeRangeRequest {

    @ApiModelProperty("事件类型 0:所有 1:进程 2:端口 3:网络连接 4:文件 5:用户")
    private Integer type;

}
