package cn.cyberpeace.mp.aggregation.dto.resource;

import cn.cyberpeace.mp.cscene.bo.CsceneInstanceCountByReferenceTypeBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/9/13 11:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("资源统计")
public class ResourceStatisticsResponse {

    private PhysicalTarget physicalTarget;

    private SceneSummary sceneSummary;

    private TargetSummary targetSummary;

    private VirtualTargetSummary virtualTargetSummary;

    /**
     * 靶标类型统计/虚拟靶标/实体靶标统计
     */
    @ApiModelProperty("靶标类型统计/虚拟靶标/实体靶标统计")
    private Map<String, Map<String,Integer>> targetStatics;

    /**
     * 场景统计
     */
    @ApiModelProperty("场景统计")
    private List<CsceneInstanceCountByReferenceTypeBO> sceneStatics;

}
