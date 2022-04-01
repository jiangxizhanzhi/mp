package cn.cyberpeace.mp.sceneplugincollection.service.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;


/**
 * 流量分析Request
 *
 * @author wanggang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlowSimulationParams {

    @ApiModelProperty("场景ID")
    @NotBlank
    private List<String> sceneIds;

    private List<String> nodeIds;

    @ApiModelProperty("源IP")
    private String sourceIp;

    @ApiModelProperty("源Mac地址")
    private List<String> sourceMacs;

    @ApiModelProperty("目标IP")
    private String destinationIp;

    @ApiModelProperty("目标Mac地址")
    private List<String> destinationMacs;

    @ApiModelProperty("流量大小")
    private Integer networkBytes;

    @ApiModelProperty("时间")
    private LocalDateTime dateTime;

    @ApiModelProperty("随机生成攻击手法")
    private Boolean shuffleCategory = Boolean.FALSE;


}
