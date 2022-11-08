package cn.cyberpeace.mp.cscene.dto.flow;

import cn.cyberpeace.mp.sceneplugincollection.bo.AttackInfo;
import cn.cyberpeace.mp.sceneplugincollection.bo.FlowBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/1 19:04
 */
@Data
public class FlowAttackDto extends FlowBO {

    @ApiModelProperty("攻击类型信息")
    private AttackInfo attackInfo;

}
