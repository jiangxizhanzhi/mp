package cn.cyberpeace.mp.cscene.dto.flow;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName FlowAttackTypeCount
 * @Description
 * @Author maojun
 * @Date 2022/2/22
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowAttackTypeCount {
    @ApiModelProperty("攻击类型")
    private String attackType;

    @ApiModelProperty("攻击次数")
    private Integer times;

}
