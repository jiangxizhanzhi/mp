package cn.cyberpeace.mp.collection.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class HostMonitorPageRequest extends HostMonitorBaseRequest{
    @ApiModelProperty("主机基本信息默认展示10条")
    private Integer pageSize = 10;

    @ApiModelProperty("分页页码")
    private Integer pageNum = 1;
}
