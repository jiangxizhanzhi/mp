package cn.cyberpeace.mp.cscene.dto.flow;

import cn.cyberpeace.mp.sceneplugincollection.bo.AttackInfo;
import cn.cyberpeace.mp.sceneplugincollection.bo.FlowBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FlowAttackDto
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/12/1 19:04
 * @Version 1.0.0
 * @see FlowBO
 */
@Data
public class FlowAttackDto extends FlowBO {

    @ApiModelProperty("攻击类型信息")
    private AttackInfo attackInfo;

}
