 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name QueryFlowTaskConfResponse.java.
 // File size 718.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.response;

import cn.cyberpeace.mp.flow.bo.FlowTaskConf;
import cn.cyberpeace.mp.flow.model.FlowTaskConfModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName QueryFlowTaskConfResponse
 * @Description 查询流量任务配置响应体
 * @Author guquan
 * @Date 2023/5/9 20:45
 * @Version 1.0.0
 * @see FlowTaskConfModel
 */
@Data
@ApiModel("查询流量任务配置响应体")
public class QueryFlowTaskConfResponse extends FlowTaskConfModel {

    /**
     * 流量任务配置
     */
    @ApiModelProperty(value = "流量任务配置")
    private FlowTaskConf flowTaskConf;
}
