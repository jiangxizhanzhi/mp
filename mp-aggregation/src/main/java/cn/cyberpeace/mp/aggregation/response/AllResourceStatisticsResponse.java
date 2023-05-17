package cn.cyberpeace.mp.aggregation.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @Author zhanglin
 * @Date 2023/5/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("所有资源统计信息")
public class AllResourceStatisticsResponse {
    @ApiModelProperty(value = "靶标数量")
    private Integer devopsNum;
    @ApiModelProperty(value = "设备数量")
    private Integer deviceNum;
    @ApiModelProperty(value = "场景数量")
    private Integer sceneNum;
    @ApiModelProperty(value = "流量数量")
    private Integer flowNum;
}