package cn.cyberpeace.mp.sceneplugincollection.service.param;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class FlowQueryParams extends QueryParams {
    @ApiModelProperty(value = "协议")
    private String protocol;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "节点id列表")
    private List<String> nodeIds;

    @ApiModelProperty(value = "相邻数据间的时间间隔,单位：秒")
    private Integer interval;

    /**
     * 源IP集合
     */
    @ApiModelProperty("源IP集合")
    private List<String> sourceIps;

    /**
     * 强制过滤的目标IP集合
     */
    @ApiModelProperty("强制过滤的目标IP集合")
    private List<String> filterDestinationIps;
}
