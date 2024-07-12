 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AddFlowTaskRequest.java.
 // File size 833.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.request;

import cn.cyberpeace.mp.flow.model.FlowScriptParamModel;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName AddFlowTaskRequest
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see FlowTaskModel
 */
@Data
@ApiModel(value = "新增流量任务请求")
public class AddFlowTaskRequest extends FlowTaskModel {

    /**
     * 网络id
     */
    @ApiModelProperty(value = "网络id")
    private String networkId;

    /**
     * 攻击脚本参数列表
     */
    @ApiModelProperty(value = "攻击脚本运行参数")
    private List<FlowScriptParamModel> scriptParamModels;

}
