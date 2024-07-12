 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowAttackDto.java.
 // File size 519.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
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
