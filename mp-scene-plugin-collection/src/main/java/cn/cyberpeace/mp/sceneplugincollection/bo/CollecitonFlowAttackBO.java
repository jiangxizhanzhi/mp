package cn.cyberpeace.mp.sceneplugincollection.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollecitonFlowAttackBO {

    @ApiModelProperty("告警等级(1:高,2:中,3:中低,4:低)")
    private Integer severity;

    @ApiModelProperty("攻击类型")
    private String attackType;

    @ApiModelProperty("流量")
    private List<FlowBO> flowBOS;

}
