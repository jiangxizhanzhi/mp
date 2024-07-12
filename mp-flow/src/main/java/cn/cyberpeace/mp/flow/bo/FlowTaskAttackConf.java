 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowTaskAttackConf.java.
 // File size 863.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FlowTaskAttackConf
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@ApiModel("攻击流量参数配置")
public class FlowTaskAttackConf {

    /**
     * 参数名
     */
    @ApiModelProperty("参数名")
    private String confKey;

    /**
     * 参数值
     */
    @ApiModelProperty("参数值")
    private String confValue;

    /**
     * 参数类型：0 string,1 IP,2 number,3 bool
     */
    @ApiModelProperty(value = "参数类型：0 string,1 IP,2 number,3 bool")
    private Integer confType;

    /**
     * 参数说明
     */
    @ApiModelProperty(value = "参数说明")
    private String confDesc;
}
