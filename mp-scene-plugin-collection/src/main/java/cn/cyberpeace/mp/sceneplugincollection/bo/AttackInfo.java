 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AttackInfo.java.
 // File size 871.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName AttackInfo
 * @Description
 * @Author zhouxinghuai
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class AttackInfo {

    @ApiModelProperty("告警等级(1:高,2:中,3:低,4:低)")
    private Integer severity;

    @ApiModelProperty("攻击类型")
    private String attackType;

    @ApiModelProperty("攻击类型")
    private String attCkType;


    public AttackInfo(Integer severity, String attackType) {
        this.severity = severity;
        this.attackType = attackType;
    }

    public AttackInfo(Integer severity, String attackType,String attCkType) {
        this.severity = severity;
        this.attackType = attackType;
        this.attCkType = attCkType;
    }

}
