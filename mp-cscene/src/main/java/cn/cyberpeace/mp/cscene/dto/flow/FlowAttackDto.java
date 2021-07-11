package cn.cyberpeace.mp.cscene.dto.flow;

import cn.cyberpeace.mp.sceneplugincollection.bo.AttackInfo;
import cn.cyberpeace.mp.sceneplugincollection.bo.FlowBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class FlowAttackDto extends FlowBO {

    @ApiModelProperty("攻击类型信息")
    private AttackInfo attackInfo;

}
