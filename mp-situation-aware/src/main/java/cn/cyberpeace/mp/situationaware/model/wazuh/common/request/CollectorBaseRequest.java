 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CollectorBaseRequest.java.
 // File size 672.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model.wazuh.common.request;

import cn.cyberpeace.mp.situationaware.model.wazuh.common.BasePageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/24 16:12
 */
@Data
@ApiModel("系统清单请求")
public class CollectorBaseRequest extends BasePageRequest {

    @ApiModelProperty(value = "代理名称")
    private String agentName;

    public CollectorBaseRequest() {

    }

    public CollectorBaseRequest(String agentName) {
        this.agentName = agentName;
    }
}
