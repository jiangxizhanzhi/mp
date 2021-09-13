package cn.cyberpeace.mp.aggregation.dto.resource;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/9/13 11:06
 */
@Data
@Builder
@ApiModel("资源统计")
public class ResourceStatisticsResponse {

    private PhysicalTarget physicalTarget;

    private SceneSummary sceneSummary;

    private TargetSummary targetSummary;

    private VirtualTargetSummary virtualTargetSummary;

}
