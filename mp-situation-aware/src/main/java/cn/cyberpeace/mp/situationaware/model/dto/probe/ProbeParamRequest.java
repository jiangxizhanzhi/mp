 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ProbeParamRequest.java.
 // File size 1008.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model.dto.probe;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 探针参数 请求体， 跟探针一对多关系
 * mp_situation_probe_param
 * @author 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("探针参数请求体")
public class ProbeParamRequest {

    /**
     * 参数名称
     */
    @ApiModelProperty(value = "参数名称")
    private String name;

    /**
     * 0:number 1:string 2:bool 3:ip
     */
    @ApiModelProperty(value = "0:number 1:string 2:bool 3:ip")
    private Integer type;

    /**
     * 默认值
     */
    @ApiModelProperty(value = "默认值")
    private String defaultValue;

    /**
     * 参数说明
     */
    @ApiModelProperty(value = "参数说明")
    private String description;


}
