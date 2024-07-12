 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SuricataRuleRequest.java.
 // File size 450.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.request;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SuricataRuleRequest {
    @ApiModelProperty("pk, 如果为创建则pk为空，编辑pk不为空")
    private Integer pk;
    @ApiModelProperty("id, 如果为创建则id为空，编辑id不为空")
    private String id;
    @ApiModelProperty("规则原始数据")
    private String metadata;
}
