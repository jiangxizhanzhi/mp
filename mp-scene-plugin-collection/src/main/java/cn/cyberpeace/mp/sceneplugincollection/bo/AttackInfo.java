package cn.cyberpeace.mp.sceneplugincollection.bo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/1 17:07
 */
public class AttackInfo {

    @ApiModelProperty("告警等级(1:高,2:中,3:中低,4:低)")
    private Integer severity;

    @ApiModelProperty("攻击类型")
    private String attackType;

    public AttackInfo(Integer severity, String attackType) {
        this.severity = severity;
        this.attackType = attackType;
    }

}
