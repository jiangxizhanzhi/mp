 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneFlowRecordsResponse.java.
 // File size 1169.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.service.response;

import cn.cyberpeace.mp.flow.model.FlowRecords;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 流量记录返回信息
 * @Author hewei
 * @Date：2022/5/12 16:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "流量记录返回信息")
public class SceneFlowRecordsResponse {


    /**
     * 流量运行状态：0 初始化， 1 运行中， 2 停止中， 3 已停止， 4 未启动过
     */
    @ApiModelProperty(value = "流量运行状态：0 初始化， 1 运行中， 2 停止中， 3 已停止， 4 未启动过")
    private Integer status;

    /**
     * 流量运行结果：1 成功， 2 失败
     */
    @ApiModelProperty(value = "流量运行结果：1 成功， 2 失败")
    private Integer result;


    /**
     * 流量记录列表
     */
    @ApiModelProperty(value = "流量记录列表")
    private List<FlowRecords> flowRecordsList;

}
