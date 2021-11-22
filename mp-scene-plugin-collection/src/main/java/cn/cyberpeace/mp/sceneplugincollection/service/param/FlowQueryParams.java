package cn.cyberpeace.mp.sceneplugincollection.service.param;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class FlowQueryParams extends QueryParams {
    @ApiModelProperty(value = "协议")
    private String protocol;

    @ApiModelProperty(value = "开始时间")
    @NotNull(message = "开始时间不能为空")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    @NotNull(message = "结束时间不能为空")
    private String endTime;

    @ApiModelProperty(value = "节点id列表")
    private List<String> nodeIds;
}
