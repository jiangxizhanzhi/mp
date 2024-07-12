 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AttackTypeTopRequest.java.
 // File size 735.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.request.flow;

import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName AttackTypeTopRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneElementRequest
 */
@Data
@ApiModel(value = "流量攻击类型查询")
public class AttackTypeTopRequest extends BaseSceneElementRequest {
    @ApiModelProperty(value = "留下前 N 个类型,剩余的聚合成一个其他")
    private Integer topNWithOther;
    @ApiModelProperty("攻击类型是否国际化")
    private boolean attackTypeI18 = true;
}
