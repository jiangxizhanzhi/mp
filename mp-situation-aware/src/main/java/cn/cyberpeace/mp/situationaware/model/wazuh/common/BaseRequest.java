 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseRequest.java.
 // File size 583.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model.wazuh.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/24 16:08
 */
@Data
@ApiModel("基础请求体")
public class BaseRequest {

    @ApiModelProperty(value = "查询返回限制数量")
    private Integer limit;

    @ApiModelProperty(value = "查询偏移量")
    private Integer offset;

    @ApiModelProperty(value = "查询返回的字段")
    private String select;

}
