 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AgentRequest.java.
 // File size 751.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model.wazuh.common.request;

import cn.cyberpeace.mp.situationaware.model.wazuh.common.BasePageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/24 16:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("wazuh代理请求")
public class AgentRequest extends BasePageRequest {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "IP")
    private String ip;

    @ApiModelProperty(value = "状态")
    private String status;

}
