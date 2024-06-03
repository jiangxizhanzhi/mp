package cn.cyberpeace.mp.cscene.dto.element;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 节点监控统计信息
 * @Author zhouxinghuai
 * @Date：2021/11/22 14:03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "漏洞信息")
public class VulnExportState {

    @ApiModelProperty("Top10")
    private List<ElementVulnerabilityCountInfo> vulnerabilityCountInfo;

    @ApiModelProperty("饼状图")
    private List<ElementVulnerabilityPieChartInfo> vulnerabilityPieChartInfo;

    @ApiModelProperty("列表")
    private List<ElementVulnerabilityInfo> list;
}
