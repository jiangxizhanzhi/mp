package cn.cyberpeace.mp.cscene.request.flow;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlowSceneSimulationRequest {

    @ApiModelProperty("单个场景id")
    @NotBlank
    private String sceneId;

    @ApiModelProperty("节点id")
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

    @ApiModelProperty("随机生成攻击手法，默认: false")
    @Builder.Default
    private Boolean shuffleCategory = Boolean.FALSE;

    @ApiModelProperty("时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;

}
