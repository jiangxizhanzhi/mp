 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BatchActuatorResponse.java.
 // File size 909.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.response;

import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName BatchActuatorResponse
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "流量批量执行回调方法返回信息")
public class BatchActuatorResponse {

    /**
     * 执行成功任务列表
     */
    @ApiModelProperty(value = "执行成功任务列表")
    private List<FlowTaskModel> successTaskModels;

    /**
     * 执行失败任务列表
     */
    @ApiModelProperty(value = "执行失败任务列表")
    private List<FlowTaskModel> errorTaskModels;

}
