package cn.cyberpeace.mp.devopscluster.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class HypervisorWeightAndMaxInstanceDTO extends BaseDTO {

    
    @ApiModelProperty(value = "主机名/节点名称")
    private String hostname;
}
