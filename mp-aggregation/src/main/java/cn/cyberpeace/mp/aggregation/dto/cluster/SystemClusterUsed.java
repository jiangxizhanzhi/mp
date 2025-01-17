package cn.cyberpeace.mp.aggregation.dto.cluster;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 使用系统集群
 *
 * @author 二竹
 * @date 2025/01/17
 */
@Data
@Builder
@ApiModel("靶场应用运行统计")
public class SystemClusterUsed {

    @ApiModelProperty(value = "集群数据")
    private ClusterData clusterData;

    @ApiModelProperty(value = "系统数据")
    private SystemData systemData;

}
