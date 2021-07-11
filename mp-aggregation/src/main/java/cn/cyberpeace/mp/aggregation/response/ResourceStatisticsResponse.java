package cn.cyberpeace.mp.aggregation.response;

import cn.cyberpeace.mp.aggregation.dto.resource.PhysicalTarget;
import cn.cyberpeace.mp.aggregation.dto.resource.SceneSummary;
import cn.cyberpeace.mp.aggregation.dto.resource.TargetSummary;
import cn.cyberpeace.mp.aggregation.dto.resource.VirtualTargetSummary;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceCountByReferenceTypeBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("资源统计")
public class ResourceStatisticsResponse {

    @ApiModelProperty(value = "实体靶标")
    private PhysicalTarget physicalTarget;

    @ApiModelProperty(value = "场景统计")
    private SceneSummary sceneSummary;

    @ApiModelProperty(value = "靶标类型统计")
    private TargetSummary targetSummary;

    @ApiModelProperty(value = "虚拟靶标")
    private VirtualTargetSummary virtualTargetSummary;

    
    @ApiModelProperty("靶标类型统计/虚拟靶标/实体靶标统计")
    private Map<String, Map<String,Object>> targetStatics;

    
    @ApiModelProperty("场景统计")
    private List<CsceneInstanceCountByReferenceTypeBO> sceneStatics;

}
