package cn.cyberpeace.mp.cscene.request.flow;

import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "流量攻击类型查询")
public class AttackTypeTopRequest extends BaseSceneElementRequest {
    @ApiModelProperty(value = "留下前 N 个类型,剩余的聚合成一个其他")
    private Integer topNWithOther;
    @ApiModelProperty("攻击类型是否国际化")
    private boolean attackTypeI18 = true;
}
