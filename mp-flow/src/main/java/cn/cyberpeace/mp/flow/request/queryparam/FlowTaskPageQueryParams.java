package cn.cyberpeace.mp.flow.request.queryparam;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("查询流量任务列表请求体")
public class FlowTaskPageQueryParams extends QueryParams {

    @ApiModelProperty("场景实例id")
    private String id;

    @ApiModelProperty("sceneId")
    private String sceneId;

    @ApiModelProperty("流量类型")
    private String interactiveType;

    @ApiModelProperty("目标设备id")
    private String nodeId;

    private Integer offset;
}
