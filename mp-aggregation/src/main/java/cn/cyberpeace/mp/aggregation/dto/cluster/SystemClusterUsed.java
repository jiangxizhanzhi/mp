package cn.cyberpeace.mp.aggregation.dto.cluster;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/9/13 13:56
 */
@Data
@Builder
@ApiModel("靶场应用运行统计")
public class SystemClusterUsed {

    private ClusterData clusterData;

    private SystemData systemData;

}
