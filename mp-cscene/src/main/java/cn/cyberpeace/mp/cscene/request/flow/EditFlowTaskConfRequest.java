 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name EditFlowTaskConfRequest.java.
 // File size 712.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.request.flow;

import cn.cyberpeace.mp.flow.bo.FlowTaskConf;
import cn.cyberpeace.mp.flow.model.FlowTaskConfModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName EditFlowTaskConfRequest
 * @Description 修改流量任务配置请求
 * @Author guquan
 * @Date 2023/5/9 19:01
 * @Version 1.0.0
 * @see FlowTaskConfModel
 */
@Data
@ApiModel("修改流量任务配置请求")
public class EditFlowTaskConfRequest extends FlowTaskConfModel {

    /**
     * 流量任务配置
     */
    @ApiModelProperty(value = "流量任务配置")
    private FlowTaskConf flowTaskConf;
}
